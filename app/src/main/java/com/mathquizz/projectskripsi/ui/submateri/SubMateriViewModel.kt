package com.mathquizz.projectskripsi.ui.submateri

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathquizz.projectskripsi.data.SubMateri
import com.mathquizz.projectskripsi.firebase.FirebaseCommon
import com.mathquizz.projectskripsi.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubMateriViewModel @Inject constructor(
    private val firebaseCommon: FirebaseCommon

) : ViewModel() {

    private val _subMateriList = MutableStateFlow<Resource<List<SubMateri>>>(Resource.Unspecified())
    val subMateriList: StateFlow<Resource<List<SubMateri>>> = _subMateriList

    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress


    private var userId: String? = null
    private var materiId: String? = null
    private var collectionName: String = "materi"

    fun setMateriId(collectionName: String, materiId: String) {
        this.collectionName = collectionName
        this.materiId = materiId
        fetchSubMateri()
        fetchProgress()
    }

    fun setUserId(userId: String) {
        this.userId = userId
        fetchProgress() // Fetch progress when userId is set
    }

    private fun fetchSubMateri() {
        materiId?.let { id ->
            viewModelScope.launch {
                _subMateriList.emit(Resource.Loading())
                try {
                    val result = firebaseCommon.getSubMateriList(collectionName, id)
                    val subMateriList = result.toObjects(SubMateri::class.java)
                    _subMateriList.emit(Resource.Success(subMateriList))
                } catch (e: Exception) {
                    _subMateriList.emit(Resource.Error(e.message ?: "Error occurred"))
                }
            }
        }

    }
    fun fetchProgress() {
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



}
