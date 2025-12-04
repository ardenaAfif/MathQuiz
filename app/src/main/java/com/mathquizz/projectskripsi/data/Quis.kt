package com.mathquizz.projectskripsi.data

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Quis(
    @DocumentId
    val quisId : String,
    val question: String,
    val imgquestion: String, //data imageUrl firestore
    val optionA: String,    //data imageUrl firestore
    val optionB: String,    //data imageUrl firestore
    val optionC: String,    //data imageUrl firestore
    val optionD: String,    //data imageUrl firestore
    val optionE: String,    //data imageUrl firestore
    val answer: String,
    val progress: Int,
    val aditionalquestion: String? = null,
    val issucsess: Boolean
) :Parcelable {

    constructor() : this ("","","","","","","","","",0,"",false)
}
