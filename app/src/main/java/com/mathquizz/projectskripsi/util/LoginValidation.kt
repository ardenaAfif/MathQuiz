package com.mathquizz.projectskripsi.util

sealed class LoginValidation (){
    object Success: LoginValidation()
    data class Failed(val message:String): LoginValidation()
}

data class LoginFieldState(
    val email: LoginValidation,
    val password: LoginValidation
)