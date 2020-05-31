package com.example.roadmission

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient

class NearbyPlaces(var context: Context, var recyclerView: RecyclerView, var data: MutableList<Place>) {

    private var placesClient: PlacesClient

    init {
        Places.initialize(context, "AIzaSyAmzuHW-fgs-I4Ur1TY-eNgJ0af1SxvB-o")
        placesClient = Places.createClient(context)
    }

    fun getPlaces() {
        val placeFields: List<Place.Field> = listOf(
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.PHONE_NUMBER,
            Place.Field.WEBSITE_URI,
            Place.Field.RATING
        )
        val request = FindCurrentPlaceRequest.builder(placeFields).build()

        val placeResponse: Task<FindCurrentPlaceResponse> = placesClient.findCurrentPlace(request)

        placeResponse.addOnCompleteListener { task: Task<FindCurrentPlaceResponse?> ->
            if (task.isSuccessful) {
                val placesList: MutableList<Place> = ArrayList()
                val response = task.result

                for (place in response!!.placeLikelihoods) {
                    placesList.add(place.place)
                }

                data.clear()
                data.addAll(placesList)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }
}