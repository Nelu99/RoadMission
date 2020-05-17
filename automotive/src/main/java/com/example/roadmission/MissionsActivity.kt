package com.example.roadmission

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field

class MissionsActivity : AppCompatActivity() {

    private lateinit var buttonFragment: Fragment
    private lateinit var missionFragment: Fragment
    private lateinit var viewPager: DeactivatedViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_missions)
        startFragments()
        setupToolbar()
    }

    fun generateMission(view: View) {
        changeFragment()
    }

    private fun changeFragment() {
        try {
            val mScroller: Field = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.isAccessible = true
            val scroller = FixedSpeedScroller(viewPager.context)
            mScroller.set(viewPager, scroller)
        }
        catch (e: NoSuchFieldException){}
        catch (e: IllegalArgumentException){}
        catch (e: IllegalAccessException){}
        viewPager.setCurrentItem(1, true)

        val view = findViewById<FrameLayout>(R.id.frame_new_mission)
        viewPager.setBackgroundResource(R.drawable.rounded_left)
        val param = view.layoutParams as ViewGroup.MarginLayoutParams
        param.setMargins(15,0,0,0)
        view.layoutParams = param

        val btn = findViewById<Button>(R.id.fragment_button)
        btn.isEnabled = false

        slideView(view)
    }

    private fun slideView(view: View) {
        val slideAnimator = ValueAnimator.ofInt(0, 300)
            .setDuration(1000)
        slideAnimator.addUpdateListener { animation ->
            val value = animation.getAnimatedValue()
            view.layoutParams.width = value.toString().toInt()
            view.requestLayout()
        }
        val animationSet = AnimatorSet()
        animationSet.interpolator = AccelerateDecelerateInterpolator()
        animationSet.play(slideAnimator)
        animationSet.start()
    }

    private fun startFragments() {
        val pagerAdapter = PagerAdapter(supportFragmentManager)
        viewPager = findViewById(R.id.view_pager_missions)
        buttonFragment = ButtonFragment()
        missionFragment = MissionFragment()
        viewPager.adapter = pagerAdapter
        viewPager.currentItem = 0
    }

    fun backButton(view: View) {
        finish()
    }

    private fun setupToolbar() {
        val mToolbar = findViewById<View>(R.id.toolbar_missions) as Toolbar
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    inner class PagerAdapter(fm: FragmentManager) :

        FragmentStatePagerAdapter(
            fm,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {

        private var fragmentNames = arrayOf("button", "mission")

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> buttonFragment
                1 -> missionFragment
                else -> buttonFragment
            }
        }

        override fun getCount(): Int {
            return fragmentNames.size
        }
    }

}
