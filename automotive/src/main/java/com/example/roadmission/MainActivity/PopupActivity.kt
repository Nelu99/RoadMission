package com.example.roadmission

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class PopupActivity : AppCompatActivity() {

    private lateinit var checkBox: CheckBox
    private val DB_NAME = "copiedDB.db"
    private val DB_PATH: String by lazy { this.applicationInfo.dataDir + "/databases/" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        copyDataBase()
        checkDontAskAgain()
        setContentView(R.layout.activity_popup)
        checkBox = findViewById(R.id.popup_checkbox)
        initSpinner()
    }

    private fun checkDontAskAgain() {
        val sharedPrefs = getSharedPreferences("com.example.roadmission", Context.MODE_PRIVATE);
        val dontAskAgain = sharedPrefs.getBoolean("dont_ask_again", false)
        if(dontAskAgain)
            startActivity(Intent(this@PopupActivity,MainActivity::class.java))
    }

    fun onSubmit(view: View) {
        val sharedPrefs = getSharedPreferences("com.example.roadmission", Context.MODE_PRIVATE);
        with (sharedPrefs.edit()) {
            putString("number_of_passengers", getNumberOfPeople())
            apply()
        }
        if (checkBox.isChecked) {
            with (sharedPrefs.edit()) {
                putBoolean("dont_ask_again", true)
                apply()
            }
        }

        startActivity(Intent(this@PopupActivity,MainActivity::class.java))
    }

    private fun getNumberOfPeople(): String {
        val spinner: Spinner = findViewById(R.id.popup_spinner)
        return when (spinner.selectedItem.toString()) {
            "Just me" -> "0"
            "1" -> "1"
            "2+" -> "2"
            else -> "0"
        }
    }

    private fun initSpinner() {
        val spinner: Spinner = findViewById(R.id.popup_spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.passengers,
            R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
            spinner.adapter = adapter
        }
    }

    @Throws(IOException::class)
    private fun copyDataBase() {
        val sharedPrefs = getSharedPreferences("com.example.roadmission", Context.MODE_PRIVATE);
        if(sharedPrefs.getBoolean("database_copied",false))
            return
        val myInput: InputStream = assets.open(DB_NAME)
        val outFileName: String = DB_PATH + DB_NAME
        val myOutput: OutputStream = FileOutputStream(outFileName)
        val buffer = ByteArray(1024)
        var length: Int
        while (myInput.read(buffer).also { length = it } > 0) {
            myOutput.write(buffer, 0, length)
        }
        with (sharedPrefs.edit()) {
            putBoolean("database_copied", true)
            apply()
        }
        myOutput.flush()
        myOutput.close()
        myInput.close()
    }

}
