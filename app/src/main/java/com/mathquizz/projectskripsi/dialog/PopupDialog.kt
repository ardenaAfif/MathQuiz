package com.mathquizz.projectskripsi.dialog

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.mathquizz.projectskripsi.R

fun Activity.showPopupDialog(message: String, progress: Int, onYesClicked: () -> Unit) {
    val dialog = Dialog(this)
    dialog.setCanceledOnTouchOutside(false)
    // Request the feature before setting content
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

    // Inflate the custom layout
    val view = layoutInflater.inflate(R.layout.popup_result, null)
    dialog.setContentView(view)

    // Find views in the layout
    val tvPopupMessage = view.findViewById<TextView>(R.id.tvPopup)
    val tvMessage = view.findViewById<TextView>(R.id.tvPopupProgress)
    val btnDismiss = view.findViewById<Button>(R.id.btnDismiss)

    // Set the message text
    tvPopupMessage.text = "Selamat!"
    tvMessage.text = message

    // Set up dialog window attributes
    dialog.window?.apply {
        setBackgroundDrawableResource(android.R.color.transparent)
        setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setGravity(Gravity.CENTER)  // Center the dialog
    }

    // Set up dismiss button
    btnDismiss.setOnClickListener {
        onYesClicked()
        dialog.dismiss()
    }

    // Show the dialog
    dialog.show()
}