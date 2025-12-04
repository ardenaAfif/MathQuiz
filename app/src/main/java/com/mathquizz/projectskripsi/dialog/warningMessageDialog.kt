package com.mathquizz.projectskripsi.dialog


import android.app.Dialog
import android.content.Intent
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

import com.mathquizz.projectskripsi.R
import com.mathquizz.projectskripsi.ui.login.LoginActivity
import com.mathquizz.projectskripsi.util.PreferenceHelper
import com.google.firebase.auth.FirebaseAuth

fun Fragment.setupWarningMessageDialog(message: String,  onYesClicked: () -> Unit) {
    val dialog = Dialog(requireContext())
    dialog.setCanceledOnTouchOutside(false)
    // Request the feature before setting content
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

    // Inflate the custom layout
    val view = layoutInflater.inflate(R.layout.reset_point_dialog, null)
    dialog.setContentView(view)

    val imgPopup = view.findViewById<ImageView>(R.id.imgPopupWarning)
    val tvPopupWarning = view.findViewById<TextView>(R.id.tvPopupWarning)
    val tvPopupProgress = view.findViewById<TextView>(R.id.tvPopupProgress)
    val tvPopupSoal = view.findViewById<TextView>(R.id.tvPopupSoal)
    val btnNo = view.findViewById<Button>(R.id.btnNo)
    val btnYes = view.findViewById<Button>(R.id.btnYes)

    // Set the dialog message and other text
    tvPopupWarning.text = "Konfirmasi Keluar"
    tvPopupProgress.text = "Apakah Anda Yakin"
    tvPopupSoal.text = message
    dialog.window?.apply {
        setBackgroundDrawableResource(android.R.color.transparent)
        setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        setGravity(Gravity.CENTER)  // Center the dialog
    }
    // Set button click listeners if needed
    btnNo.setOnClickListener {
        dialog.dismiss()
        // Handle "No" button click
    }

    btnYes.setOnClickListener {
        onYesClicked()
        dialog.dismiss()
        FirebaseAuth.getInstance().signOut()

        // Update login status to false
        PreferenceHelper.setLoggedIn(requireContext(), false)
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
                 activity?.finish()
    }

    // Show the dialog
    dialog.show()
}