package com.mathquizz.projectskripsi.ui.register

import androidx.lifecycle.ViewModel
import com.mathquizz.projectskripsi.data.User
import com.mathquizz.projectskripsi.util.Constants.USER_COLLECTION
import com.mathquizz.projectskripsi.util.RegisterFieldState
import com.mathquizz.projectskripsi.util.RegisterValidation
import com.mathquizz.projectskripsi.util.Resource
import com.mathquizz.projectskripsi.util.validateEmail
import com.mathquizz.projectskripsi.util.validateName
import com.mathquizz.projectskripsi.util.validateNim
import com.mathquizz.projectskripsi.util.validatePassword
import com.mathquizz.projectskripsi.util.validateProdi
import com.mathquizz.projectskripsi.util.validateSemester
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore

): ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User,password: String){
        if (checkValidation(user, password)) {
            runBlocking {
                _register.emit(Resource.Loading())
            }

            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveUserInfo(it.uid, user)

                    }

                }
                .addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        }else{
            val registerFieldsState = RegisterFieldState(
                validateNim(user.nim), validateName(user.name), validateEmail(user.email),
                validateProdi(user.prodi), validateSemester(user.semester), validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun saveUserInfo(userUid: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }
            .addOnFailureListener{
                _register.value = Resource.Error(it.message.toString())
            }

    }

    private fun checkValidation(user: User, password: String): Boolean {
        val nimValidation = validateNim(user.nim)
        val nameValidation = validateName(user.name)
        val emailValidation = validateEmail(user.email)
        val prodiValidation = validateProdi(user.prodi)
        val semesterValidation = validateSemester(user.semester)
        val passwordValidation = validatePassword(password)
        val shouldRegister = nimValidation is RegisterValidation.Success && nameValidation is RegisterValidation.Success && emailValidation is RegisterValidation.Success && prodiValidation is RegisterValidation.Success && semesterValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success
        return shouldRegister
    }
}
