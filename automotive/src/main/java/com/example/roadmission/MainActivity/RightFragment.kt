package com.example.roadmission

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
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