package com.example.roadmission

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var leftFragment : Fragment
    private lateinit var middleFragment : Fragment
    private lateinit var rightFragment : Fragment
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        startFragment()
    }

    fun setupToolbar() {
        val mToolbar = findViewById<View>(R.id.toolbar_main) as Toolbar
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    fun startFragment() {
        val pagerAdapter = PagerAdapter(supportFragmentManager)
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout_main)
        leftFragment = LeftFragment()
        middleFragment = MiddleFragment()
        rightFragment = RightFragment()
        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = pagerAdapter
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
