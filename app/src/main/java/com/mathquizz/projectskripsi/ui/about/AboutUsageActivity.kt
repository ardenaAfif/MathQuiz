package com.mathquizz.projectskripsi.ui.about

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.databinding.ActivityAboutUsageBinding
import com.mathquizz.projectskripsi.util.applySystemBarInsets
import com.mathquizz.projectskripsi.util.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutUsageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutUsageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("ActivityCheck", "AboutUsageActivity dipanggil")   //Cek Pemanggilan

        enableEdgeToEdge()
        setStatusBarColor(this, this, binding.root, window)

        binding.appBarLayoutUsage.applySystemBarInsets(applyTop = true)
        binding.main.applySystemBarInsets(applyBottom = true)
    }
}