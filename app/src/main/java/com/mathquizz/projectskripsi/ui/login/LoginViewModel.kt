package com.mathquizz.projectskripsi.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathquizz.projectskripsi.util.LoginFieldState
import com.mathquizz.projectskripsi.util.LoginValidation
import com.mathquizz.projectskripsi.util.Resource
import com.mathquizz.projectskripsi.util.validateEmailLogin
import com.mathquizz.projectskripsi.util.validatePasswordLogin
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    private val _validation = Channel<LoginFieldState>()
    val validation: Flow<LoginFieldState> = _validation.receiveAsFlow()

    fun login(email: String, password: String) {

        if (checkValidation(email, password)) {
            viewModelScope.launch {
                _login.emit(Resource.Loading())
            }
            firebaseAuth.signInWithEmailAndPassword(
                email, password
            )
                .addOnSuccessListener {
                    viewModelScope.launch {
                        it.user?.let {
                            _login.emit(Resource.Success(it))
                        }
                    }
                }
                .addOnFailureListener {exception ->
                    viewModelScope.launch {
                        val errorMessage = when (exception) {
                            is FirebaseAuthInvalidUserException -> "Email not registered"
                            is FirebaseAuthInvalidCredentialsException -> "Email and Password not registered"
                            else -> "Error: ${exception.message}"
                        }
                        _login.emit(Resource.Error(errorMessage))
                    }
                }
        }else {
            val loginFieldState = LoginFieldState(
                validateEmailLogin(email),
                validatePasswordLogin(password, password)
            )
            runBlocking {
                _validation.send(loginFieldState)
            }
        }
    }
    fun resetPassword(email: String){
        if (email.isEmpty()) {
            viewModelScope.launch {
                _resetPassword.emit(Resource.Error("Email cannot be empty"))
            }
            return
        }
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }
        firebaseAuth
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch{
                    _resetPassword.emit(Resource.Success(email))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch{
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }


    }
    private fun checkValidation(email: String, password: String): Boolean {
        val emailValidation = validateEmailLogin(email)
        val passwordValidation = validatePasswordLogin(password, password)
        return emailValidation is LoginValidation.Success && passwordValidation is LoginValidation.Success
    }

}