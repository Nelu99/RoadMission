package com.example.roadmission.LibraryActivity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.roadmission.R

class LibraryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_library)
        setupToolbar()
        initUserName()
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

}
