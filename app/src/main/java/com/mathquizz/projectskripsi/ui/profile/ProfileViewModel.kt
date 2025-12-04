package com.mathquizz.projectskripsi.ui.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathquizz.projectskripsi.data.User
import com.mathquizz.projectskripsi.util.Resource
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : ViewModel() {
    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user: StateFlow<Resource<User>> = _user
    private val _uploadState = MutableStateFlow<Resource<Unit>>(Resource.Unspecified())
    val uploadState: StateFlow<Resource<Unit>> = _uploadState
    private val _passwordChangeResult = MutableStateFlow<Resource<Boolean>>(Resource.Unspecified())
    val passwordChangeResult: StateFlow<Resource<Boolean>> = _passwordChangeResult.asStateFlow()

    init {
        getUser()
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        val user = auth.currentUser

        if (user != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)

            user.reauthenticate(credential)
                .addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    _passwordChangeResult.value = Resource.Success(true)
                                } else {
                                    _passwordChangeResult.value = Resource.Error("Failed to change password. Make sure the old password is correct and the new password meets the requirements.")
                                }
                            }
                    } else {
                        _passwordChangeResult.value = Resource.Error("Failed to reauthenticate user. Make sure the old password is correct.")
                    }
                }
        } else {
            _passwordChangeResult.value = Resource.Error("User is not authenticated.")
        }
    }
    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            _uploadState.value = Resource.Loading()
            _user.emit(Resource.Loading())
            val userId = auth.uid ?: return@launch
            val storageRef = storage.reference.child("profile_images/$userId.jpg")
            try {
                val uploadTaskSnapshot = storageRef.putFile(imageUri).await()
                val downloadUrl = uploadTaskSnapshot.storage.downloadUrl.await().toString()
                updateProfileImageUrl(downloadUrl)
                _uploadState.value = Resource.Success(Unit)
            } catch (e: Exception) {
                Log.e("UploadFile", e.message.toString())
                _user.emit(Resource.Error(e.message ?: "Error occurred during profile image upload"))
                _uploadState.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun updateProfileImageUrl(imageUrl: String) {
        val userId = auth.uid ?: return
        firestore.collection("user").document(userId)
            .update("imagePath", imageUrl)
            .addOnSuccessListener {
                getUser()
            }
            .addOnFailureListener {
                Log.e("UpdateProfile", it.message.toString())
            }
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
                            _user.value = Resource.Error("Failed to retrieve user data")
                        }
                    } else {
                        _user.value = Resource.Error("User document does not exist")
                    }
                }
            }
        } ?: run {
            _user.value = Resource.Error("User is not authenticated")
        }
    }


}
