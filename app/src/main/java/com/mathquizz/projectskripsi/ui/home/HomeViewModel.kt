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

    private val _materiList = MutableStateFlow<Resource<List<Materi>>>(Resource.Unspecified())
    val materiList: StateFlow<Resource<List<Materi>>> = _materiList

    private val _clickableItems = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val clickableItems: StateFlow<Map<String, Boolean>> = _clickableItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var userId: String? = null
    private var materiId: String? = null

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress


    init {
        getUser()
        fetchMateriList()
        fetchProgress()
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

    private fun fetchMateriList() {
        viewModelScope.launch {
            _materiList.emit(Resource.Loading())
            try {
                val result = firebaseCommon.getMateriList()
                if (result.isEmpty()) {
                    _materiList.emit(Resource.Error("No materi found"))
                } else {
                    // Fetch progress for each materi
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val progressMap = mutableMapOf<String, Int>()
                        val progressDeferreds = result.map { materi ->
                            async {
                                val progress = firebaseCommon.getCurrentProgress(userId, materi.materiId)
                                progressMap[materi.materiId] = progress
                            }
                        }
                        progressDeferreds.awaitAll()

                        // Update the progress in each materi
                        val materiWithProgress = result.map { materi ->
                            materi.copy(progress = progressMap[materi.materiId] ?: 0)
                        }
                        _materiList.emit(Resource.Success(materiWithProgress))
                    } else {
                        _materiList.emit(Resource.Error("Pengguna tidak diautentikasi"))
                    }
                }
            } catch (e: Exception) {
                _materiList.emit(Resource.Error(e.message.toString()))
            }
        }
    }
    private fun fetchProgress() {
        userId?.let { userId ->
            materiId?.let { materiId ->
                viewModelScope.launch {
                    try {
                        val currentProgress = firebaseCommon.getCurrentProgress(userId, materiId)
                        _progress.emit(currentProgress)
                    } catch (e: Exception) {
                        Log.e("SubMateriViewModel", "Error fetching progress", e)
                    }
                }
            }
        }
    }
    fun checkClickability() {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch
            val materiList = _materiList.value.data ?: return@launch

            val progressMap = mutableMapOf<String, Int>()
            val itemIdToClickability = mutableMapOf<String, Boolean>()

            val progressDeferreds = materiList.map { materi ->
                async {
                    val progress = firebaseCommon.getCurrentProgress(userId, materi.materiId)
                    progressMap[materi.materiId] = progress
                }
            }

            progressDeferreds.awaitAll()

            for (materi in materiList) {
                val isClickable = if (materi.materiId == materiList[0].materiId) {
                    true
                } else {
                    val previousMateri = materiList.find { it.urutan == materi.urutan - 1 }
                    val previousMateriId = previousMateri?.materiId
                    val previousMateriProgress = previousMateriId?.let { progressMap[it] } ?: 0
                    previousMateriProgress >= 80
                }
                itemIdToClickability[materi.materiId] = isClickable
            }

            Log.d("Clickability", "Clickable Items: $itemIdToClickability")
            _clickableItems.value = itemIdToClickability
            _isLoading.value = false
        }
    }
}