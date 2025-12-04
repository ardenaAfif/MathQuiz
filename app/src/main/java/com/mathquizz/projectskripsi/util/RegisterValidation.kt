package com.mathquizz.projectskripsi.util

sealed class RegisterValidation (){
    object Success: RegisterValidation()
    data class Failed(val message:String): RegisterValidation()
}

data class RegisterFieldState(
    val nim: RegisterValidation,
    val name: RegisterValidation,
    val email: RegisterValidation,
    val prodi: RegisterValidation,
    val semester: RegisterValidation,
    val password: RegisterValidation
)