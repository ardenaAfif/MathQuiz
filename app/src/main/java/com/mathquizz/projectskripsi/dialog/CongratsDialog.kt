package com.mathquizz.projectskripsi.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.mathquizz.projectskripsi.MainActivity
import com.mathquizz.projectskripsi.R

fun Activity.setupCongratsDialog(message: String, progress: Int, onYesClicked: () -> Unit) {
    val dialog = Dialog(this)
    dialog.setCanceledOnTouchOutside(false)
    // Request the feature before setting content
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

    // Inflate the custom layout
    val view = layoutInflater.inflate(R.layout.dialog_congrats, null)
    dialog.setContentView(view)

    val imgPopup = view.findViewById<ImageView>(R.id.imgPopupCongrats)
    val tvPopupCongrats = view.findViewById<TextView>(R.id.tvPopupCongrats)
    val tvPopupMessage = view.findViewById<TextView>(R.id.tvPopupProgress)
    val tvPopupSoal = view.findViewById<TextView>(R.id.tvPopupSoal)
    val btnNo = view.findViewById<Button>(R.id.btnNo)
    val btnYes = view.findViewById<Button>(R.id.btnYes)
    // Set the congratulation and message texts
    tvPopupCongrats.text = "Materi telah di pelajari"
    tvPopupMessage.text = "Progress Point Anda: ${progress}%"
    tvPopupSoal.text = message

    // Set up dialog window attributes
    dialog.window?.apply {
        setBackgroundDrawableResource(android.R.color.transparent)
        setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setGravity(Gravity.CENTER)  // Center the dialog
    }

    // Set up dismiss button
    btnNo.setOnClickListener {
        dialog.dismiss()
        // Handle "No" button click
    }

    btnYes.setOnClickListener {
        onYesClicked()
        dialog.dismiss()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigateToHome", true) // Optional: Pass extra data if needed
        }
        startActivity(intent)
        finishAffinity() // Finish all activities and remove them from the back stack
    }

    // Show the dialog
    dialog.show()
}