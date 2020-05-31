package com.example.roadmission.LibraryActivity

import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.roadmission.ProgressDatabase
import com.example.roadmission.R
import java.text.SimpleDateFormat
import java.util.*


class LibraryActivity : AppCompatActivity() {

    private val DB_NAME = "copiedDB.db"
    private val DB_PATH: String by lazy { this.applicationInfo.dataDir + "/databases/" }

    companion object{
        lateinit var missionsDB: ProgressDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_library)
        setupToolbar()
        initUserName()
        missionsDB = ProgressDatabase(this)

    }

    private fun initUserName() {
        val userName: EditText = findViewById(R.id.user_name)
        val sharedPrefs: SharedPreferences = getSharedPreferences(
            "com.example.roadmission",
            Context.MODE_PRIVATE
        )
        userName.setText(
            sharedPrefs.getString("user_name", ""),
            TextView.BufferType.EDITABLE
        )

        userName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val editor = sharedPrefs.edit()
                editor.putString("user_name", userName.text.toString())
                editor.apply()
            }
        })
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

    fun backButton(view: View) {
        finish()
    }

    private fun setupToolbar() {
        val mToolbar = findViewById<View>(R.id.toolbar_library) as Toolbar
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    private fun getDate(): String? {
        val pattern = "dd MMM yyyy"
        val simpleDateFormat = SimpleDateFormat(pattern)
        return simpleDateFormat.format(Date())
    }

    fun showAllMissions(view: View) {
        val copiedDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY)
        val res: Cursor? = copiedDB.rawQuery("select * from TABLE_MISSIONS", null)
        val buffer = StringBuffer()
        while(res?.moveToNext()!!){
            buffer.append("\n${res.getString(0)}\n\n")
            buffer.append("${res.getString(1)}\n\n")
            buffer.append("Weather : ${res.getString(2)}\n\n")
            buffer.append("Passengers : ${res.getString(3)}\n\n")
            buffer.append("Difficulty : ${res.getString(4)}\n\n")
        }
        res.close()
        showDialog(buffer.toString())
    }

    fun showAllAchievements(view: View) {
        val res: Cursor? = missionsDB.getAllAchievements()
        val buffer = StringBuffer()
        while(res?.moveToNext()!!){
            buffer.append("\nMission : ${res.getString(0)}\n\n")
            buffer.append("Difficulty : ${res.getString(1)}\n\n")
            buffer.append("Total Completions : ${res.getString(2)}\n\n")
            buffer.append("Last Completion : ${res.getString(3)}\n\n")
        }
        res.close()
        showDialog(buffer.toString())
    }

    fun showAchievementsToday(view: View) {
        val res: Cursor = getDate()?.let { missionsDB.getAchievementByDate(it) }!!
        val buffer = StringBuffer()
        buffer.append("\n${getDate()}\n\n")
        while(res.moveToNext()){
            buffer.append("Mission : ${res.getString(0)}\n\n")
            buffer.append("Difficulty : ${res.getString(1)}\n\n")
            buffer.append("Total Completions : ${res.getString(2)}\n\n")
        }
        res.close()
        showDialog(buffer.toString())
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
}
