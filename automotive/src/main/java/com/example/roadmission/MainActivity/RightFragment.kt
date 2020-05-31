
package com.example.roadmission

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.Place


class RightFragment : Fragment() {

    companion object{
        lateinit var chronometer:Chronometer
    }

    private lateinit var mView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var nearbyPlaces: NearbyPlaces

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
        nearbyPlaces()
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
        nearbyPlaces.getPlaces()
        recyclerView.adapter?.notifyDataSetChanged()
        super.onSaveInstanceState(outState)
    }

    private fun nearbyPlaces() {

        recyclerView = requireActivity().findViewById(R.id.places_lst);
        val recyclerLayoutManager = LinearLayoutManager(context)
        val data: MutableList<Place> = ArrayList()

        recyclerView.layoutManager = recyclerLayoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(
            recyclerView.context,
            recyclerLayoutManager.orientation
        ))
        recyclerView.adapter = PlacesRecyclerViewAdapter(
            data,
            requireContext()
        )

        nearbyPlaces = NearbyPlaces(requireContext(), recyclerView, data)

        nearbyPlaces.getPlaces()
    }
}