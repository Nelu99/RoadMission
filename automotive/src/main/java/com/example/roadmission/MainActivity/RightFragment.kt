
package com.example.roadmission

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.Toast
import androidx.fragment.app.Fragment

class RightFragment : Fragment() {

    companion object{
        lateinit var chronometer:Chronometer
    }

    private lateinit var mView: View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_right, container, false)
        startChronometer(savedInstanceState)
        return mView
    }
    private fun startChronometer(savedInstanceState: Bundle?)
    {
        val sharedPrefs = activity?.getSharedPreferences("com.example.roadmission", Context.MODE_PRIVATE)
        chronometer = mView.findViewById(R.id.chronometer)
        if(sharedPrefs?.getBoolean("should_update_chronometer", false)!!) {
            with (sharedPrefs.edit()) {
                putBoolean("should_update_chronometer", false)
                apply()
            }
            chronometer.base = sharedPrefs.getLong("chronometer_timer",0L)
        }
        else if (savedInstanceState != null) {
            chronometer.base = savedInstanceState.getLong("time");
        }
        chronometer.start();
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong("time", chronometer.base)
        super.onSaveInstanceState(outState)
    }
}