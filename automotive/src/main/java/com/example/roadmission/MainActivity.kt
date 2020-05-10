package com.example.roadmission

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.widget.Chronometer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout



class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var tts: TextToSpeechRM
    }

    private lateinit var leftFragment: Fragment
    private lateinit var middleFragment: Fragment
    private lateinit var rightFragment: Fragment
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var locationManager: LocationManager
    private lateinit var myService:LocationService
    private lateinit var sc: ServiceConnection

    private var status: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_main)
        startFragment()
        tts = TextToSpeechRM(applicationContext)
        checkPermissions()
        startLocationService()
    }



    private fun bindService(){
        if(status == true)
        {
            return
        }
        var i = Intent(applicationContext, LocationService::class.java)
        bindService(i,sc, Context.BIND_AUTO_CREATE)
        status = true
    }

    override fun onDestroy() {
        if(status == true) {
            unbindService()
        }
        super.onDestroy()
    }

    fun unbindService(){
        if(status == false)
        {
            return
        }
        unbindService(sc)
        status = false

    }

    private fun startLocationService(){
        sc = object: ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {
                status = false
            }
            override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
                var binder = iBinder as LocationService.LocalBinder
                myService = binder.getService()
                status = true
            }
        }
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            return
        }
        if(status == false)
        {
            bindService()
        }
    }

    private fun checkPermissions()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1000)
        }
        checkGPS()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1000 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "GRANTED", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this, "DENIED", Toast.LENGTH_SHORT).show()
            }

        }
        return

    }

    private fun checkGPS(){

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            showGPSDisabledAlert()
        }
    }

    private fun showGPSDisabledAlert()
    {
        Toast.makeText(this, "GPS DISABLED", Toast.LENGTH_SHORT).show()
    }

    private fun setTheme() {
        val sharedPrefs = getSharedPreferences("com.example.roadmission", Context.MODE_PRIVATE);
        val lightModePref = sharedPrefs.getBoolean("enable_light_mode", false)
        if(lightModePref) {
            setTheme(R.style.CustomThemeLight)
        }
        else {
            setTheme(R.style.CustomThemeDark)
        }
    }

    private fun startFragment() {
        val pagerAdapter = PagerAdapter(supportFragmentManager)
        tabLayout = findViewById(R.id.tab_layout_main)
        viewPager = findViewById(R.id.view_pager)
        leftFragment = LeftFragment()
        middleFragment = MiddleFragment()
        rightFragment = RightFragment()
        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = pagerAdapter
        if(intent.getBooleanExtra("CHANGED_THEME", false)) {
            viewPager.currentItem = 0
        }
        else
            viewPager.currentItem = 1
    }

    inner class PagerAdapter(fm: FragmentManager) :

        FragmentStatePagerAdapter(
            fm,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {

        private var fragmentNames = arrayOf("left", "middle", "right")

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> leftFragment
                1 -> middleFragment
                2 -> rightFragment
                else -> middleFragment
            }
        }

        override fun getCount(): Int {
            return fragmentNames.size
        }
    }



}