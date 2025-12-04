package com.mathquizz.projectskripsi.dialog

import android.app.Activity
import android.widget.Button
import android.widget.EditText



import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mathquizz.projectskripsi.R


fun Activity.setupChangePasswordDialog(
    onChangeClick: (String, String, String) -> Unit
) {
    val dialog = BottomSheetDialog(this, R.style.RoundedBottomSheetDialog)
    dialog.setCanceledOnTouchOutside(false)
    val view = layoutInflater.inflate(R.layout.change_password_dialog, null)
    dialog.setContentView(view)
    dialog.show()

    val editTextPasswordOld = view.findViewById<EditText>(R.id.edit_Text_PasswordOld)
    val editTextPasswordNew = view.findViewById<EditText>(R.id.edit_Text_PasswordNew)
    val editTextConfirmationPasswordNew = view.findViewById<EditText>(R.id.edit_Text_Confirmation_Password_New)
    val buttonChange = view.findViewById<Button>(R.id.button_Change)

    buttonChange.setOnClickListener {
        val oldPassword = editTextPasswordOld.text.toString()
        val newPassword = editTextPasswordNew.text.toString()
        val confirmNewPassword = editTextConfirmationPasswordNew.text.toString()
        onChangeClick(oldPassword, newPassword, confirmNewPassword)
        dialog.dismiss()
    }
}