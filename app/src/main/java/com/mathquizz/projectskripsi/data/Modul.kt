package com.mathquizz.projectskripsi.data

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Modul(
    @DocumentId
    val modulId: String,
    val imagemodul: String,
    val progress: Int,
    val issucces: Boolean

) : Parcelable {

    constructor() : this ("","",0,true)
}