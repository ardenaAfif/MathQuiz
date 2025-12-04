package com.mathquizz.projectskripsi.dialog

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.mathquizz.projectskripsi.R

fun Activity.showQuizDialog(message: String) {
    val dialog = Dialog(this)
    dialog.setCanceledOnTouchOutside(false)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.dialog_quiz)

    // Find views in the layout
    val tvPopupCongrats = dialog.findViewById<TextView>(R.id.tvPopupCongrats)
    val tvPopupMessage = dialog.findViewById<TextView>(R.id.tvPopupMessage)
    val btnDismiss = dialog.findViewById<Button>(R.id.btnDismiss)

    // Set the message text
    tvPopupCongrats.text = "Quiz Terkunci!!"
    tvPopupMessage.text = message

    // Set up dialog window attributes
    dialog.window?.apply {
        setBackgroundDrawableResource(android.R.color.transparent)
        setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setGravity(Gravity.CENTER)
    }

    // Set up dismiss button
    btnDismiss.setOnClickListener {
        dialog.dismiss()
    }

    // Show the dialog
    dialog.show()
}