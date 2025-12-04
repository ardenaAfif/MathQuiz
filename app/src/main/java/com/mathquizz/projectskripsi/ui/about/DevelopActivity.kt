package com.mathquizz.projectskripsi.ui.about

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.mathquizz.projectskripsi.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DevelopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_develop)

        Log.d("ActivityCheck", "DevelopActivity dipanggil")   //Cek Pemanggilan

    }
}