package com.example.roadmission
import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import com.google.android.gms.location.LocationListener;
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.widget.Chronometer
import android.widget.Toast
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_right.view.*

class LocationService : Service(), GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
    LocationListener{


    private val INTERVAL: Long = 1000 * 2
    private val FASTESTINTERVAL: Long = 1000

    private lateinit var mlocationRequest: LocationRequest
    private lateinit var mGoogleApiClient: GoogleApiClient

    private var lastLocation: Location = Location("dummylocation")
    private var currentLocation: Location = Location("dummylocation")
    private var mBinder:IBinder = LocalBinder()
    private var minutesStopped:Int = 0

    init {
        checkForStop()
    }

    private fun checkForStop(){
        if(lastLocation == currentLocation){
            minutesStopped += 1
            RightFragment.chronometer.start()
        }
        else if(minutesStopped >= 5){  //minim 5 minute pauza
            minutesStopped = 0
        }
        else
        {
            minutesStopped = 0
        }
        android.os.Handler().postDelayed(Runnable {checkForStop()}, 60000) //1minute
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

    fun createLocationRequest(){
        mlocationRequest = LocationRequest.create()
        mlocationRequest.setInterval(INTERVAL)
        mlocationRequest.setFastestInterval(FASTESTINTERVAL)
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

    }

    override
    fun onConnected(bundle:Bundle?)
    {
        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mlocationRequest,this)
        }catch (e:SecurityException)
        {

        }

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
        currentLocation= location

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

}