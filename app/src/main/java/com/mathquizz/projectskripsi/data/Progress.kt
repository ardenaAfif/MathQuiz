package com.mathquizz.projectskripsi.data

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Progress(
    @DocumentId
    val progresindikatorId:String,
    val progressint : Long
) : Parcelable {

    constructor() : this ("",0)
}