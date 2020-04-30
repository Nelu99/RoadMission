package com.example.roadmission

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat

class LeftFragment : PreferenceFragmentCompat() {

    private lateinit var switch_light_mode : SwitchPreferenceCompat

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.fragment_left, rootKey)

        switch_light_mode = this.findPreference<SwitchPreferenceCompat>("enable_light_mode")!!
        val sharedPrefs = activity?.getPreferences(Context.MODE_PRIVATE)
        switch_light_mode.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, _ ->
                if(switch_light_mode.isChecked){
                    Toast.makeText(activity, "Light Mode Disabled", Toast.LENGTH_SHORT).show()
                    switch_light_mode.isChecked = false
                    if (sharedPrefs != null) {
                        with (sharedPrefs.edit()) {
                            putBoolean("enable_light_mode", false)
                            commit()
                        }
                    }
                } else{
                    Toast.makeText(activity, "Light Mode Enabled", Toast.LENGTH_SHORT).show()
                    switch_light_mode.isChecked = true
                    if (sharedPrefs != null) {
                        with (sharedPrefs.edit()) {
                            putBoolean("enable_light_mode", true)
                            commit()
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