package com.example.roadmission

import android.app.Service
import android.content.Intent
import android.location.Location
import com.google.android.gms.location.LocationListener;
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.model.Place

class LocationService : Service(), GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
    LocationListener {

    private val INTERVAL: Long = 1000 * 2
    private val FASTESTINTERVAL: Long = 1000

    private lateinit var mlocationRequest: LocationRequest
    private lateinit var mGoogleApiClient: GoogleApiClient

    companion object {
        lateinit var lastList: MutableList<Place>
    }

    private var recyclerView: RecyclerView
    private var nearbyPlaces: NearbyPlaces
    private var lastLocation: Location = Location("dummylocation")
    private var currentLocation: Location = Location("dummylocation")
    private var mBinder:IBinder = LocalBinder()
    private var minutesStopped:Int = 0
    private var distance:Double = 0.0

    init {
        RightFragment.chronometer.base = SystemClock.elapsedRealtime()
        RightFragment.chronometer.start()
        recyclerView = MainActivity.myActivity.findViewById(R.id.places_lst)!!
        nearbyPlaces = NearbyPlaces(MainActivity.myContext)
        android.os.Handler().postDelayed(Runnable {initPlaces()}, 2000)
        android.os.Handler().postDelayed({updateStop()}, 1000) //1minute - 60.000
    }

    private fun initPlaces() {
        recyclerView = MainActivity.myActivity.findViewById(R.id.places_lst)!!
        nearbyPlaces = NearbyPlaces(MainActivity.myContext)
        val recyclerLayoutManager = LinearLayoutManager(MainActivity.myContext)
        recyclerView.layoutManager = recyclerLayoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(
            recyclerView.context,
            recyclerLayoutManager.orientation
        ))
        nearbyPlaces.getPlaces { data ->
            recyclerView.adapter = PlacesRecyclerViewAdapter(data,MainActivity.myContext)
            lastList = data
        }
    }

    private fun updateStop(){
        RightFragment.chronometer.start()
        minutesStopped += 1
        android.os.Handler().postDelayed({updateStop()}, 1000) //1minute
    }

    override fun onBind(intent: Intent?): IBinder? {
        createLocationRequest()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .build()
        mGoogleApiClient.connect()
        return mBinder
    }

    inner class LocalBinder : Binder() {
        fun getService (): LocationService {
            return this@LocationService
        }
    }

    private fun createLocationRequest(){
        mlocationRequest = LocationRequest.create()
        mlocationRequest.interval = INTERVAL
        mlocationRequest.fastestInterval = FASTESTINTERVAL
        mlocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override
    fun onConnected(bundle:Bundle?)
    {
        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mlocationRequest,this)
        }catch (e:SecurityException) { }

    }
    override
    fun onConnectionSuspended(i:Int)
    {
        //to do
    }
    override
    fun onConnectionFailed(connectionResult:ConnectionResult)
    {
        //to do
    }

    override fun onLocationChanged(location: Location) {
        lastLocation = currentLocation
        currentLocation = location
        distance = lastLocation.distanceTo(currentLocation)/1.0
        if(distance > 1.00) {
            initPlaces()
            if(minutesStopped >= 5)
            {
                RightFragment.chronometer.base = SystemClock.elapsedRealtime();
                RightFragment.chronometer.stop();
            }
            minutesStopped = 0
        }
    }

    override fun onUnbind(intent:Intent):Boolean{
        stopLocationUpdates()
        if(mGoogleApiClient.isConnected)
        {
            mGoogleApiClient.disconnect()
        }
        lastLocation = Location("dummylocation")
        currentLocation = Location("dummylocation")
        return super.onUnbind(intent)
    }

    fun stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this)
    }

    fun getLatitude():String {
        return currentLocation.latitude.toString()
    }

    fun getLongitude():String{
        return currentLocation.longitude.toString()
    }

}