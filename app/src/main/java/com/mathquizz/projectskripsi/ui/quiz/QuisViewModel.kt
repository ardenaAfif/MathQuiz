package com.mathquizz.projectskripsi.ui.quiz


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mathquizz.projectskripsi.data.Quis
import com.mathquizz.projectskripsi.firebase.FirebaseCommon
import com.mathquizz.projectskripsi.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QuisViewModel @Inject constructor(
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {


    private var materiId: String? = null
    private var subMateriId: String? = null
    private val _quisQuestion = MutableStateFlow<Resource<List<Quis>>>(Resource.Unspecified())
    val quisQuestion: StateFlow<Resource<List<Quis>>> = _quisQuestion


    fun setQuisId(materiId: String, subMateriId: String) {
        this.materiId = materiId
        this.subMateriId = subMateriId
        fetchQuisQuestion()

    }

    fun fetchQuisQuestion() {
        val mId = materiId
        val sId = subMateriId
        materiId?.let {
            if (mId != null && sId != null) {
                viewModelScope.launch {
                    _quisQuestion.emit(Resource.Loading())
                    try {
                        val result = firebaseCommon.getQuisQuestion(mId, sId)
                        val quisQuestion = result.toObjects(Quis::class.java)
                        _quisQuestion.emit(Resource.Success(quisQuestion))
                    } catch (e: Exception) {
                        _quisQuestion.emit(Resource.Error(e.message ?: "Error occurred"))
                    }
                }
            }
        }
    }

    fun updateProgressBasedOnQuiz(userId: String, correctAnswers: Int) {
        val mId = materiId
        if (mId != null) {
            viewModelScope.launch {
                try {
                    firebaseCommon.updateProgressBasedOnQuiz(userId, mId,correctAnswers)
                } catch (e: Exception) {
                    Log.e("QuisViewModel", "Error updating progress", e)
                }
            }
        }
    }
}

