package com.mathquizz.projectskripsi.util

import android.util.Patterns

fun validateNim(nim: Long): RegisterValidation {
    if (nim <= 0)
        return RegisterValidation.Failed("NIM must be a number and cannot be empty")

    return RegisterValidation.Success
}
fun validateName(name: String): RegisterValidation{

    if (name.isEmpty())
        return RegisterValidation.Failed("Name cannot be empety")

    return RegisterValidation.Success
}
fun validateEmail(email: String): RegisterValidation{

    if (email.isEmpty())
        return RegisterValidation.Failed("Email cannot be empety")

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Incorrect Email Format")

    return RegisterValidation.Success
}
fun validateProdi(prodi: String): RegisterValidation {
    if (prodi.isEmpty())
        return RegisterValidation.Failed("Prodi cannot be empety")
    return RegisterValidation.Success
}
fun validateSemester(semester: Int): RegisterValidation {
    if (semester <= 0)
        return RegisterValidation.Failed("Semester harus berupa angka dan tidak boleh kosong")

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation {
    if (password.isEmpty())
        return RegisterValidation.Failed("Password cannot be empety")

    if (password.length < 8)
        return RegisterValidation.Failed("Passwords cannot be less than 8")

    return RegisterValidation.Success
}
fun validateEmailLogin(email: String): LoginValidation{

    if (email.isEmpty())
        return LoginValidation.Failed("Email cannot be empety")

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return LoginValidation.Failed("Incorrect Email Format")

    return LoginValidation.Success
}

fun validatePasswordLogin(password: String, correctPassword: String): LoginValidation {
    if (password.isEmpty())
        return LoginValidation.Failed("Password cannot be empety")

    if (password != correctPassword)
        return LoginValidation.Failed("password wrong")

    return LoginValidation.Success
}
