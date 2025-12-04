package com.mathquizz.projectskripsi.ui.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mathquizz.projectskripsi.MainActivity
import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.ui.onboarding.OnBoardingActivity
import com.mathquizz.projectskripsi.util.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private companion object {
        private const val DURATION_ANIMATION = 1000L
        private const val ALPHA_VAL = 1f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("ActivityCheck", "SplashActivity dipanggil")   //Cek Pemanggilan

        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        val splash: ImageView = findViewById(R.id.splashImage)
        splash.alpha = 0f
        splash.animate().setDuration(DURATION_ANIMATION).alpha(ALPHA_VAL).withEndAction {
            val intent = if (PreferenceHelper.isLoggedIn(this)) {
                Intent(this, MainActivity::class.java)
            } else {
                Intent(this, OnBoardingActivity::class.java)
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        supportActionBar?.hide()
    }
}