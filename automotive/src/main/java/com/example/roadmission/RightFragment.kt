package com.example.roadmission

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer

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
        startChronometer()
        return mView
    }
    private fun startChronometer()
    {
        chronometer = mView.findViewById<Chronometer>(R.id.chronometer)
        chronometer.start()
    }
}