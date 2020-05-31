package com.example.roadmission

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient

class NearbyPlaces(var context: Context) {

    private var placesClient: PlacesClient
    private var API_KEY = "AIzaSyD_iMiqZQSByWjfyiibLO5Haud2-ySkCkI"

    init {
        Places.initialize(context, API_KEY)
        placesClient = Places.createClient(context)
    }

    fun getPlaces(callback: (MutableList<Place>) -> Unit){
        val placeFields: List<Place.Field> = listOf(
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.RATING
        )
        val request = FindCurrentPlaceRequest.builder(placeFields).build()

        val placeResponse: Task<FindCurrentPlaceResponse> = placesClient.findCurrentPlace(request)

        placeResponse.addOnCompleteListener { task: Task<FindCurrentPlaceResponse?> ->
            run {
                val placesList: MutableList<Place> = ArrayList()
                try {
                    val response = task.result
                    for (place in response!!.placeLikelihoods) {
                        placesList.add(place.place)
                    }
                    callback(placesList)
                }
                catch (x: Exception){}
            }
        }
    }
}