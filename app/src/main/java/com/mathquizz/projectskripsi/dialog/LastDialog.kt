package com.mathquizz.projectskripsi.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.mathquizz.projectskripsi.MainActivity
import com.mathquizz.projectskripsi.R

fun Activity.showLastDialog(message: String, onYesClicked: () -> Unit) {
    val dialog = Dialog(this)
    dialog.setCanceledOnTouchOutside(false)
    // Request the feature before setting content
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

    // Inflate the custom layout
    val view = layoutInflater.inflate(R.layout.dialog_last, null)
    dialog.setContentView(view)

    // Find views in the layout
    val tvPopupCongrats = view.findViewById<TextView>(R.id.tvPopupCongrats)
    val tvPopupProgress = view.findViewById<TextView>(R.id.tvPopupProgress)
    val tvPopupSoal = view.findViewById<TextView>(R.id.tvPopupSoal)
    val btnNo = view.findViewById<Button>(R.id.btnNo)
    val btnYes = view.findViewById<Button>(R.id.btnYes)

    // Set the message text
    tvPopupCongrats.text = "Konfirmasi Dialog"
    tvPopupProgress.text = "Materi Sudah Anda Pelajari"
    tvPopupSoal.text = message

    // Set up dialog window attributes
    dialog.window?.apply {
        setBackgroundDrawableResource(android.R.color.transparent)
        setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setGravity(Gravity.CENTER)  // Center the dialog
    }

    // Set up button click listeners
    btnNo.setOnClickListener {
        onYesClicked()
        dialog.dismiss()
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("navigateToHome", true) // Optional: Pass extra data if needed
        }
        startActivity(intent)
        finishAffinity()// Close only the current activity
    }

    btnYes.setOnClickListener {

        dialog.dismiss()

    }

    // Show the dialog
    dialog.show()
}