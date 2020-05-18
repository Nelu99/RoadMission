package com.example.roadmission

import android.content.Context
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient

class NearbyPlaces(var context: Context, var item1: TextView, var item2: TextView, var item3: TextView) {

    private var placesClient: PlacesClient

    init {
        Places.initialize(context, "AIzaSyAmzuHW-fgs-I4Ur1TY-eNgJ0af1SxvB-o")
        placesClient = Places.createClient(context)
    }

    fun getPlaces() {
        val placeFields: List<Place.Field> = listOf(
            Place.Field.NAME
//            Place.Field.RATING
        )
        val request = FindCurrentPlaceRequest.builder(placeFields).build()

        val placeResponse: Task<FindCurrentPlaceResponse> = placesClient.findCurrentPlace(request)

        placeResponse.addOnCompleteListener { task: Task<FindCurrentPlaceResponse?> ->
            if (task.isSuccessful) {
                val response = task.result
                item1.text = response!!.placeLikelihoods[0].place.name
                item2.text = response.placeLikelihoods[1].place.name
                item3.text = response.placeLikelihoods[2].place.name
            }
        }
    }
}