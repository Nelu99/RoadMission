package com.example.roadmission

import android.os.Bundle
import android.os.SystemClock
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
        chronometer = mView.findViewById(R.id.chronometer)
        if (LocationService.shouldResetChronometer) {
            if (savedInstanceState != null)
                chronometer.base = savedInstanceState.getLong("time");
            else
                chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong("time", chronometer.base)
        super.onSaveInstanceState(outState)
    }
}