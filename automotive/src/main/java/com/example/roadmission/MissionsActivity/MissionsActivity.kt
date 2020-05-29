package com.example.roadmission

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field
import java.util.*

class MissionsActivity : AppCompatActivity() {

    private lateinit var buttonFragment: Fragment
    private lateinit var missionFragment: Fragment
    private lateinit var viewPager: DeactivatedViewPager
    private val DB_NAME = "copiedDB.db"
    private val DB_PATH: String by lazy { applicationInfo.dataDir + "/databases/" }
    private lateinit var res: Cursor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_missions)
        startFragments()
        setupToolbar()
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
            showDialog("No missions found for your current configuration")
            return
        }
        val random: Int = Random().nextInt(res.count)
        for(i in 0..random)
            res.moveToNext()
        val buffer = StringBuffer()
        buffer.append("\n${res.getString(0)}\n\n")
        buffer.append("${res.getString(1)}\n\n")
        buffer.append("Weather : ${res.getString(2)}\n\n")
        buffer.append("Passengers : ${res.getString(3)}\n\n")
        buffer.append("Difficulty : ${res.getString(4)}\n\n")
        res.close()
        showDialog(buffer.toString())
        if(viewPager.currentItem == 0)
            changeFragment()
    }

    private fun showDialog(Message: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setMessage(Message)
        val alert = builder.create()
        alert.show()
        alert.window!!.attributes
        val textViewMessage = alert.findViewById<View>(android.R.id.message) as TextView?
        textViewMessage!!.textSize = 32f
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
            val value = animation.getAnimatedValue()
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

    fun backButton(view: View) {
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
