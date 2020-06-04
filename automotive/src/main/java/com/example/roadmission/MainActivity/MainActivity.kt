package com.example.roadmission

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.roadmission.LibraryActivity.LibraryActivity
import com.google.android.material.tabs.TabLayout


class MainActivity : AppCompatActivity() {

    companion object {
        lateinit var tts: TextToSpeechRM
        lateinit var myService:LocationService
        lateinit var myContext: Context
        lateinit var myActivity: Activity
    }

    private lateinit var leftFragment: Fragment
    private lateinit var middleFragment: Fragment
    private lateinit var rightFragment: Fragment
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var locationManager: LocationManager
    private lateinit var sc: ServiceConnection
    private lateinit var weather: Weather

    private var status: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_main)
        myContext = applicationContext
        myActivity = this
        startFragment()
        tts = TextToSpeechRM(applicationContext)
        checkPermissions()
        startLocationService()
        weather = Weather()
        android.os.Handler().postDelayed(Runnable {weatherExecute()}, 2000)
    }

    private fun weatherExecute()
    {
        weather.execute()
        //android.os.Handler().postDelayed(Runnable {weatherExecute()}, 360000) //1hour
    }

    fun missionButton(view : View) {
        startActivity(Intent(this@MainActivity,MissionsActivity::class.java))
    }

    fun libraryButton(view : View) {
        startActivity(Intent(this@MainActivity, LibraryActivity::class.java))
    }

    private fun bindService(){
        if(status)
        {
            return
        }
        val i = Intent(applicationContext, LocationService::class.java)
        bindService(i,sc, Context.BIND_AUTO_CREATE)
        status = true
    }

    override fun onDestroy() {
        if(status) {
            unbindService()
        }
        super.onDestroy()
    }

    private fun unbindService(){
        if(!status)
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
                val binder = iBinder as LocationService.LocalBinder
                myService = binder.getService()
                status = true
            }
        }
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            return
        }
        if(!status)
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
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
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

    fun aboutDialog(view: View) {
        val buffer = StringBuffer()
        buffer.append("\nProiect colectiv realizat sub indrumarea firmei Elektrobit Automotive\n")
        buffer.append("\nCoordonator : Alin Brindusescu\n")
        buffer.append("\nTeam leader : Nelu Maciuca\n")
        buffer.append("\nTeam member : Catalin Iusan\n")
        buffer.append("\nTeam member : Ioan Majdan\n")
        buffer.append("\nAplicatia RoadMission a fost creata pentru divertismentul persoanelor" +
                " care parcurg distanțe considerabile frecvent, incercand sa ofere o experiență" +
                " mai plăcută pe durata transportului. Acest lucru este realizat prin acordarea " +
                "de misiuni bazate pe numărul de pasageri și vreme, de diferite dificultăți. De " +
                "asemenea sunt oferite recomandări pentru stationari pentru a evita oboseala, " +
                "alături de o lista cu locuri și activități recomandate pe durata pauzei.\n")
        showDialog(buffer.toString())
    }

    private fun showDialog(Message: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        builder.setMessage(Message)
        val alert = builder.create()
        alert.show()
        alert.window!!.attributes
        val textViewMessage = alert.findViewById<View>(android.R.id.message) as TextView?
        textViewMessage!!.textSize = 32f
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