package com.mathquizz.projectskripsi.ui.onboarding

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.adapter.OnBoardingAdapter
import com.mathquizz.projectskripsi.databinding.ActivityOnBoardingBinding
import com.mathquizz.projectskripsi.util.setStatusBarColor
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("ActivityCheck", "OnBoardingActivity dipanggil")   //Cek Pemanggilan

        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        setStatusBarColor(this, this, binding.root, window)

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