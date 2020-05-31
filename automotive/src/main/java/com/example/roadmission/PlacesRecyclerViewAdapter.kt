package com.example.roadmission


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.Place


class PlacesRecyclerViewAdapter(private val placesList: List<Place>, ctx: Context) :
    RecyclerView.Adapter<PlacesRecyclerViewAdapter.ViewHolder>() {
    override fun getItemCount(): Int {
        return placesList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.places_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val place = placesList[position]
        holder.name.text = place.name
        holder.address.text = place.address
        holder.phone.text = place.phoneNumber
        if (place.websiteUri != null) {
            holder.website.text = place.websiteUri.toString()
        }
        if (place.rating!! > -1) {
            holder.ratingBar.numStars = place.rating as Int
        } else {
            holder.ratingBar.visibility = View.GONE
        }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name)
        var address: TextView = view.findViewById(R.id.address)
        var phone: TextView = view.findViewById(R.id.phone)
        var website: TextView = view.findViewById(R.id.website)
        var ratingBar: RatingBar = view.findViewById(R.id.rating)

    }

}