package com.example.roadmission

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.*

class LeftFragment : PreferenceFragmentCompat() {

    private lateinit var switch_light_mode : SwitchPreferenceCompat
    private lateinit var number_of_passengers : ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_left, rootKey)
        initLightModePreference()
        initNrOfPassengers()
    }

    private fun initNrOfPassengers() {
        number_of_passengers = this.findPreference("passengers")!!
        val sharedPrefs = activity?.getSharedPreferences("com.example.roadmission", Context.MODE_PRIVATE);

        sharedPrefs?.getString("number_of_passengers", "0")?.toInt()?.let {number_of_passengers.setValueIndex(it)}

        number_of_passengers.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                when(newValue.toString()) {
                    "Just me" ->
                        if (sharedPrefs != null) {
                            with(sharedPrefs.edit()) {
                                putString("number_of_passengers", "0")
                                apply()
                            }
                        }
                    "1" ->
                        if (sharedPrefs != null) {
                            with(sharedPrefs.edit()) {
                                putString("number_of_passengers", "1")
                                apply()
                            }
                        }
                    "2+" ->
                        if (sharedPrefs != null) {
                            with(sharedPrefs.edit()) {
                                putString("number_of_passengers", "2")
                                apply()
                            }
                        }
                }
                if (sharedPrefs != null) {
                    with(sharedPrefs.edit()) {
                        putBoolean("dont_ask_again", false)
                        apply()
                    }
                }
                sharedPrefs?.getString("number_of_passengers", "0")?.toInt()?.let {number_of_passengers.setValueIndex(it)}

                false
            }
    }

    private fun initLightModePreference() {
        switch_light_mode = this.findPreference("enable_light_mode")!!
        val sharedPrefs = activity?.getSharedPreferences("com.example.roadmission", Context.MODE_PRIVATE);

        switch_light_mode.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                if(switch_light_mode.isChecked){
                    Toast.makeText(activity, "Light Mode Disabled", Toast.LENGTH_SHORT).show()
                    switch_light_mode.isChecked = false
                    if (sharedPrefs != null) {
                            with (sharedPrefs.edit()) {
                                putBoolean("enable_light_mode", false)
                                apply()
                        }
                    }
                } else{
                    Toast.makeText(activity, "Light Mode Enabled", Toast.LENGTH_SHORT).show()
                    switch_light_mode.isChecked = true
                    if (sharedPrefs != null) {
                        with (sharedPrefs.edit()) {
                            putBoolean("enable_light_mode", true)
                            apply()
                        }
                    }
                }
                restart()
                false
            }
    }

    private fun restart(){
        val intent: Intent? = activity?.intent
        intent?.putExtra("CHANGED_THEME", true);
        activity?.finish()
        startActivity(intent)
    }
}