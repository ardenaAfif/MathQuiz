package com.mathquizz.projectskripsi.ui.onboarding

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.adapter.OnBoardingAdapter
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("ActivityCheck", "OnBoardingActivity dipanggil")   //Cek Pemanggilan

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)
        val fragmentList = arrayListOf(
            FirstScreenFragment(),
            SecondScreenFragment(),
            ThirdScreenFragment()
        )

        val adapter = OnBoardingAdapter(
            fragmentList,
            supportFragmentManager,
            lifecycle
        )

        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        viewPager.adapter = adapter

        val indicator = findViewById<DotsIndicator>(R.id.dots_indicator)
        indicator.attachTo(viewPager)
    }
}