package com.mathquizz.projectskripsi.ui.about

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mathquizz.projectskripsi.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutUsageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_usage)

        Log.d("ActivityCheck", "AboutUsageActivity dipanggil")   //Cek Pemanggilan

    }
}