package com.example.roadmission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.ExpandableListView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

class RightFragment : Fragment() {

    companion object{
        lateinit var chronometer:Chronometer
    }

    private lateinit var item1: TextView
    private lateinit var item2: TextView
    private lateinit var item3: TextView
    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater.inflate(R.layout.fragment_right, container, false)
        startChronometer(savedInstanceState)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        item1 = requireView().findViewById(R.id.item_1)
        item2 = requireView().findViewById(R.id.item_2)
        item3 = requireView().findViewById(R.id.item_3)
        nearbyPlaces()
    }
    private fun startChronometer(savedInstanceState: Bundle?)
    {
        chronometer = mView.findViewById(R.id.chronometer)
        if (savedInstanceState != null) {
            chronometer.setBase(savedInstanceState.getLong("time"));
        }
        chronometer.start();
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong("time", chronometer.base)
        super.onSaveInstanceState(outState)
    }

    private fun nearbyPlaces() {

        val nearbyPlaces = NearbyPlaces(requireContext(), item1, item2, item3)

        nearbyPlaces.getPlaces()
    }
}