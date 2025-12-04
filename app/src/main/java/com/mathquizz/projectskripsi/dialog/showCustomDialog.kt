package com.mathquizz.projectskripsi.dialog

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.mathquizz.projectskripsi.R

fun Activity.showCustomPopup(message: String) {
    val dialog = Dialog(this)
    dialog.setCanceledOnTouchOutside(false)
    // Request the feature before setting content
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

    // Inflate the custom layout
    val view = layoutInflater.inflate(R.layout.dialog_popup, null)
    dialog.setContentView(view)

    // Find views in the layout
    val imgPopup = view.findViewById<ImageView>(R.id.imgPopup)
    val tvPopupMessage = view.findViewById<TextView>(R.id.tvPopupMessage)
    val btnDismiss = view.findViewById<Button>(R.id.btnDismiss)

    imgPopup.drawable
    // Set the message text
    tvPopupMessage.text = message

    // Set up dialog window attributes
    dialog.window?.apply {
        setBackgroundDrawableResource(android.R.color.transparent)
        setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setGravity(Gravity.CENTER)  // Center the dialog
    }

    // Set up dismiss button
    btnDismiss.setOnClickListener {
        dialog.dismiss()
    }

    // Show the dialog
    dialog.show()
}