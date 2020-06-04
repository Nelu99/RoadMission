package com.example.roadmission

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MissionsActivity : AppCompatActivity() {

    private lateinit var buttonFragment: Fragment
    private lateinit var missionFragment: Fragment
    private lateinit var viewPager: DeactivatedViewPager
    private lateinit var progressDB: ProgressDatabase
    private val DB_NAME = "copiedDB.db"
    private val DB_PATH: String by lazy { applicationInfo.dataDir + "/databases/" }
    private lateinit var res: Cursor
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_missions)
        startFragments()
        setupToolbar()
        progressDB = ProgressDatabase(this)
    }

    private fun setTheme() {
        val sharedPrefs = getSharedPreferences("com.example.roadmission", Context.MODE_PRIVATE);
        val lightModePref = sharedPrefs.getBoolean("enable_light_mode", false)
        if(lightModePref) {
            setTheme(R.style.CustomThemeLight)
        }
        else {
            setTheme(R.style.CustomThemeDark)
        }
    }

    fun generateMission(view: View) {
        val copiedDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY)
        val passengers = getPassengers()
        val weather = getWeather()
        res = copiedDB.rawQuery(
            "select * from TABLE_MISSIONS where (WEATHER = '$weather' or WEATHER = 'ALL') " +
                    "and PASSENGERS = '$passengers'", null)
        if(res.count == 0) {
            res.close()
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(true)
            builder.setMessage("No mission found")
            val alert = builder.create()
            alert.show()
            alert.window!!.attributes
            val textViewMessage = alert.findViewById<View>(android.R.id.message) as TextView?
            textViewMessage!!.textSize = 32f
            MainActivity.tts.speak("No missions found")
            return
        }
        val random: Int = Random().nextInt(res.count)
        for(i in 0..random)
            res.moveToNext()
        updateMissionData()
        if(viewPager.currentItem == 0)
            changeFragment()
    }

    private fun updateMissionData() {
        val textTitle = findViewById<TextView>(R.id.mission_title)
        textTitle.text = res.getString(0)
        val textMission = findViewById<TextView>(R.id.mission_text)
        textMission.text = res.getString(1)
        val textDifficulty = findViewById<TextView>(R.id.mission_difficulty)
        textDifficulty.text = "Difficulty : " + res.getString(4)
        val countDownValue = when(res.getString(4)) {
            "EASY" -> 9
            "AVERAGE" -> 6
            "HARD" -> 3
            else -> 6
        }
        MainActivity.tts.speak("Your mission is " + res.getString(0) +
            "." + res.getString(1) + "Hurry up, you only have $countDownValue minutes for this task!")
        val textCountDown = findViewById<TextView>(R.id.count_down_text)
        textCountDown.text = countDownValue.toString()
        timer?.cancel()
        timer = object : CountDownTimer((5000 * countDownValue).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                textCountDown.text = String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
            }

            override fun onFinish() {
                textCountDown.text = "0 min, 0 sec"
                showDialog()
                cancel()
            }
        }.start()
    }

    private fun showDialog(){
        lateinit var dialog:AlertDialog
        val builder = AlertDialog.Builder(this)
        val titleOfDialog = TextView(applicationContext)
        titleOfDialog.height = 100
        titleOfDialog.text = "Time is up!"
        titleOfDialog.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
        titleOfDialog.setTextColor(Color.WHITE)
        titleOfDialog.gravity = Gravity.CENTER
        builder.setCustomTitle(titleOfDialog)
        builder.setMessage("Did you complete you mission?")
        val dialogClickListener = DialogInterface.OnClickListener{_,which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> addProgressAchievement()
                DialogInterface.BUTTON_NEGATIVE -> {
                    MainActivity.tts.speak("Mission Failed, you ran out of time")
                    Toast.makeText(this, "Mission failed", Toast.LENGTH_SHORT).show()
                    res.close()
                }
            }
        }
        builder.setPositiveButton("YES",dialogClickListener)
        builder.setNegativeButton("NO",dialogClickListener)
        dialog = builder.create()
        dialog.show()
        dialog.window!!.attributes
        val textViewMessage = dialog.findViewById<View>(android.R.id.message) as TextView?
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.white))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, R.color.white))
        textViewMessage!!.textSize = 32f
    }

    private fun addProgressAchievement() {
        if(progressDB.isAchievementRegistered(res.getString(0)))
            progressDB.updateAchievement(res.getString(0), res.getString(4), 1, getDate().toString())
        else
            progressDB.addAchievement(res.getString(0), res.getString(4), 1, getDate().toString())
        Toast.makeText(this, "Achievement added", Toast.LENGTH_SHORT).show()
        MainActivity.tts.speak("Congratulations on completing the mission, the achievement was added to your library")
        res.close()
    }

    private fun getDate(): String? {
        val pattern = "dd MMM yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern)
        return simpleDateFormat.format(Date())
    }

    private fun getWeather(): String {
        val sharedPrefs = getSharedPreferences("com.example.roadmission", Context.MODE_PRIVATE);
        return when(sharedPrefs.getString("weather","clear sky")){
            "clear sky" -> "SUNNY"
            "few clouds", "scattered clouds", "broken clouds", "shower rain",
            "rain", "thunderstorm" -> "RAINY"
            else -> "ALL"
        }
    }

    private fun getPassengers(): String? {
        val sharedPrefs = getSharedPreferences("com.example.roadmission", Context.MODE_PRIVATE);
        var passengers = sharedPrefs.getString("number_of_passengers","0")
        if(passengers == "2") passengers = "2+"
        return passengers
    }

    private fun changeFragment() {
        try {
            val mScroller: Field = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.isAccessible = true
            val scroller = FixedSpeedScroller(viewPager.context)
            mScroller.set(viewPager, scroller)
        }
        catch (e: NoSuchFieldException){}
        catch (e: IllegalArgumentException){}
        catch (e: IllegalAccessException){}
        viewPager.setCurrentItem(1, true)

        val view = findViewById<FrameLayout>(R.id.frame_new_mission)
        viewPager.setBackgroundResource(R.drawable.rounded_left)
        val param = view.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(15,0,0,0)
        view.layoutParams = param

        val btn = findViewById<Button>(R.id.fragment_button)
        btn.isEnabled = false

        slideView(view)
    }

    private fun slideView(view: View) {
        val slideAnimator = ValueAnimator.ofInt(0, 300)
            .setDuration(1000)
        slideAnimator.addUpdateListener { animation ->
            val value = animation.animatedValue
            view.layoutParams.width = value.toString().toInt()
            view.requestLayout()
        }
        val animationSet = AnimatorSet()
        animationSet.interpolator = AccelerateDecelerateInterpolator()
        animationSet.play(slideAnimator)
        animationSet.start()
    }

    private fun startFragments() {
        val pagerAdapter = PagerAdapter(supportFragmentManager)
        viewPager = findViewById(R.id.view_pager_missions)
        buttonFragment = ButtonFragment()
        missionFragment = MissionFragment()
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = 0
    }

    override fun onBackPressed() {
        timer?.cancel()
        MainActivity.tts.stop()
        moveTaskToBack(true)
    }

    fun backButton(view: View) {
        timer?.cancel()
        MainActivity.tts.stop()
        finish()
    }

    private fun setupToolbar() {
        val mToolbar = findViewById<View>(R.id.toolbar_missions) as Toolbar
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    inner class PagerAdapter(fm: FragmentManager) :

        FragmentStatePagerAdapter(
            fm,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {

        private var fragmentNames = arrayOf("button", "mission")

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> buttonFragment
                1 -> missionFragment
                else -> buttonFragment
            }
        }

        override fun getCount(): Int {
            return fragmentNames.size
        }
    }

}
