package com.mathquizz.projectskripsi.ui.onboarding

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mathquizz.projectskripsi.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SecondScreenFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_screen, container, false)

        Log.d("ActivityCheck", "SecondScreenFragment dipanggil")   //Cek Pemanggilan
    }


}