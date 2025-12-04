package com.mathquizz.projectskripsi.ui.modul


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathquizz.projectskripsi.data.Modul
import com.mathquizz.projectskripsi.firebase.FirebaseCommon
import com.mathquizz.projectskripsi.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ModulViewModel @Inject constructor(

    private val firebaseCommon: FirebaseCommon,




) : ViewModel() {
    private var materiId: String? = null
    private var subMateriId: String? = null
    private val _modul = MutableStateFlow<Resource<List<Modul>>>(Resource.Unspecified())
    val modul: StateFlow<Resource<List<Modul>>> = _modul
    private val _progress = MutableStateFlow(0)
    val progress: StateFlow<Int> = _progress

    fun setModulId(materiId: String, subMateriId: String) {
        this.materiId = materiId
        this.subMateriId = subMateriId
        fetchModul()

    }


    private fun fetchModul() {
        val mId = materiId
        val sId = subMateriId
        materiId?.let {
            if (mId != null && sId != null) {
                viewModelScope.launch {
                    _modul.emit(Resource.Loading())
                    try {
                        val result = firebaseCommon.getModul(mId, sId)
                        val modul = result.toObjects(Modul::class.java)
                        _modul.emit(Resource.Success(modul))
                    } catch (e: Exception) {
                        _modul.emit(Resource.Error(e.message ?: "Error occurred"))
                    }
                }
            }
        }
    }

    fun updateUserProgress(userId: String, materiId: String, subMateriId: String, newProgress: Int, onPopupNeeded: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val (issuccess, proses) = firebaseCommon.updateProgressAndProcess(userId, materiId, subMateriId, newProgress)
                onPopupNeeded(!issuccess && !proses)
            } catch (e: Exception) {
                Log.e("ModulViewModel", "Error updating progress", e)
                onPopupNeeded(false)
            }
        }
    }


    fun getMateriTitle(materiId: String, callback: (String?) -> Unit) {
        viewModelScope.launch {
            val materi = firebaseCommon.getMateriById(materiId)
            callback(materi?.title)
        }
    }



}
