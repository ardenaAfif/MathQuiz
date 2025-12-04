package com.mathquizz.projectskripsi.dialog

import android.app.Activity
import android.widget.Button
import android.widget.EditText
import com.mathquizz.projectskripsi.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Activity.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
){
    val dialog = BottomSheetDialog(this)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.setCanceledOnTouchOutside(false)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val edEmail = view.findViewById<EditText>(R.id.edResetPasword)
    val buttonSend = view.findViewById<Button>(R.id.btnSendResetPassword)
    val buttonCancel = view.findViewById<Button>(R.id.btnCancelResetPassword)

    buttonSend.setOnClickListener{
        val email = edEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }
    buttonCancel.setOnClickListener{
        dialog.dismiss()
    }
}