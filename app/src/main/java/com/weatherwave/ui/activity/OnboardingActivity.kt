package com.weatherwave.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import com.weatherwave.R
import com.weatherwave.ui.adapter.OnboardingAdapter

class OnboardingActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = OnboardingAdapter(this)

        val dotsIndicator: DotsIndicator = findViewById(R.id.dots_indicator)
        dotsIndicator.setViewPager2(viewPager)
    }

    fun navigateToNextPage() {
        val nextItem = viewPager.currentItem + 1
        if (nextItem < (viewPager.adapter?.itemCount ?: 0)) {
            viewPager.setCurrentItem(nextItem, true)
        }
    }

    fun skipOnboarding() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    fun finishOnboarding() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
