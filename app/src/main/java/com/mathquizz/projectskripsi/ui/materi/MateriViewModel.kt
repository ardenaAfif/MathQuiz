package com.mathquizz.projectskripsi.ui.materi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mathquizz.projectskripsi.data.Materi
import com.mathquizz.projectskripsi.firebase.FirebaseCommon
import com.mathquizz.projectskripsi.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MateriViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _materiList = MutableStateFlow<Resource<List<Materi>>>(Resource.Unspecified())
    val materiList: StateFlow<Resource<List<Materi>>> = _materiList

    private val _clickableItems = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val clickableItems: StateFlow<Map<String, Boolean>> = _clickableItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchMateriList(collectionName: String) {
        viewModelScope.launch {
            _materiList.emit(Resource.Loading())
            try {
                val result = firebaseCommon.getMateriList(collectionName)

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
            _clickableItems.value = itemIdToClickability
            _isLoading.value = false
        }
    }
}