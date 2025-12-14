package com.mathquizz.projectskripsi.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathquizz.projectskripsi.data.Materi
import com.mathquizz.projectskripsi.data.User
import com.mathquizz.projectskripsi.firebase.FirebaseCommon
import com.mathquizz.projectskripsi.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon

) : ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user: StateFlow<Resource<User>> = _user

    private val _clickableItems = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val clickableItems: StateFlow<Map<String, Boolean>> = _clickableItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress


    init {
        getUser()
    }

    private fun getUser() {
        auth.currentUser?.let { firebaseUser ->
            val userUid = firebaseUser.uid
            val userRef = firestore.collection("user").document(userUid)

            userRef.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _user.value = Resource.Error(error.message ?: "Error fetching user")
                    return@addSnapshotListener
                }

                snapshot?.let { document ->
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            _user.value = Resource.Success(user)
                        } else {
                            _user.value = Resource.Error("Gagal mengambil data pengguna")
                        }
                    } else {
                        _user.value = Resource.Error("Dokumen pengguna tidak ada")
                    }
                }
            }
        } ?: run {
            _user.value = Resource.Error("Pengguna tidak diautentikasi")
        }
    }
}