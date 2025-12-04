package com.mathquizz.projectskripsi.data

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubMateri(

    @DocumentId
    val submateriId: String,
    val imageURL: String,
    val title: String,
    val category: String,
    val isPretest: Boolean,
    val urutan: Long,
    val question : Int,
    var isProgressUpdated: Boolean // Added this field

) : Parcelable {



    constructor() : this ("","","","",false,0L,0,false)
}
