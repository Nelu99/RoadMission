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
        if (place.rating != null) {
            holder.ratingBar.numStars = place.rating!!.toInt()
        } else {
            holder.ratingBar.visibility = View.GONE
        }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView = view.findViewById(R.id.name)
        var address: TextView = view.findViewById(R.id.address)
        var ratingBar: RatingBar = view.findViewById(R.id.rating)

    }

}