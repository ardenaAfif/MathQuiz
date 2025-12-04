package com.mathquizz.projectskripsi.util

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.mathquizz.projectskripsi.R

fun setStatusBarColor(ctx: Context, activity: AppCompatActivity, rootLayout: View, window: Window) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
        ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            val height = insets.top
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = insets.top
                bottomMargin = insets.bottom
            }

            val statusBarView = View(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    height
                )
                setBackgroundColor(ContextCompat.getColor(ctx, R.color.blue_1))
            }
            activity.addContentView(statusBarView, statusBarView.layoutParams)

            WindowInsetsCompat.CONSUMED
        }
    } else {
        window.statusBarColor = ContextCompat.getColor(ctx, R.color.blue_1)
    }

}