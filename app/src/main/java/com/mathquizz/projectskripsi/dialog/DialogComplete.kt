package com.mathquizz.projectskripsi.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import com.mathquizz.projectskripsi.MainActivity
import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.ui.materi.MateriActivity

fun Activity.showDialogComplete(onYesClicked: () -> Unit) {
    val dialog = Dialog(this)
    dialog.setCanceledOnTouchOutside(false)
    // Request the feature before setting content
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

    // Inflate the custom layout
    val view = layoutInflater.inflate(R.layout.dialog_selesai, null)
    dialog.setContentView(view)

    // Find views in the layout

    val btnDismiss = view.findViewById<Button>(R.id.btnDismiss)

    // Set the message text



    // Set up dialog window attributes
    dialog.window?.apply {
        setBackgroundDrawableResource(android.R.color.transparent)
        setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setGravity(Gravity.CENTER)  // Center the dialog
    }

    // Set up dismiss button
    btnDismiss.setOnClickListener {
        onYesClicked()
        onYesClicked()
        dialog.dismiss()
        val intent = Intent(this, MateriActivity::class.java).apply {
            putExtra("navigateToHome", true) // Optional: Pass extra data if needed
        }
        startActivity(intent)
        finishAffinity()// Close only the current activity
    }

    // Show the dialog
    dialog.show()
}