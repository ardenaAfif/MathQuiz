package com.mathquizz.projectskripsi.ui.about

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.databinding.ActivityAboutBinding
import com.mathquizz.projectskripsi.util.setStatusBarColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("ActivityCheck", "AboutActivity dipanggil")   //Cek Pemanggilan

        enableEdgeToEdge()
        setStatusBarColor(this, this, binding.root, window)

    }
}