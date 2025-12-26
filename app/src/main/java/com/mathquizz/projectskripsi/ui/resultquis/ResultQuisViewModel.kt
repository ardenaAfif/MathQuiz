package com.mathquizz.projectskripsi.ui.resultquis

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathquizz.projectskripsi.firebase.FirebaseCommon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject


@HiltViewModel
class ResultQuisViewModel @Inject constructor(

    private val firebaseCommon: FirebaseCommon

    ) : ViewModel() {
    private val _progress = MutableStateFlow<Int>(0)
    val progress: StateFlow<Int> get() = _progress
    private var userId: String? = null
    private var materiId: String? = null
    private var collectionName: String = "materi"

    fun setMateriId(collectionName: String, materiId: String) {
        this.collectionName = collectionName
        this.materiId = materiId
        fetchProgressIfReady()
    }

    fun setUserId(userId: String) {
        this.userId = userId
        fetchProgressIfReady()
    }
    private fun fetchProgressIfReady() {
        if (userId != null && materiId != null) {
            fetchProgress()
        }
    }
    private fun fetchProgress() {
        viewModelScope.launch {
            try {
                userId?.let { uid ->
                    materiId?.let { mid ->
                        val currentProgress = firebaseCommon.getCurrentProgress(uid, mid)
                        _progress.emit(currentProgress)
                    }
                }
            } catch (e: Exception) {
                Log.e("ResultQuisViewModel", "Error fetching progress", e)
            }
        }
    }



    fun getMateriTitle(callback: (String?) -> Unit) {
        materiId?.let { id ->
            viewModelScope.launch {
                try {
                    val materi = firebaseCommon.getMateriById(collectionName, id)
                    callback(materi?.title)
                } catch (e: Exception) {
                    Log.e("ResultQuisViewModel", "Error fetching materi title", e)
                    callback(null)
                }
            }
        } ?: run {
            Log.e("ResultQuisViewModel", "MateriId is null")
            callback(null)
        }
    }
}