package com.example.roadmission

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme()
        setContentView(R.layout.activity_main)
        startFragment()
        tts = TextToSpeechRM(applicationContext)
    }

    private fun setTheme() {
        val sharedPrefs = getPreferences(Context.MODE_PRIVATE)
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