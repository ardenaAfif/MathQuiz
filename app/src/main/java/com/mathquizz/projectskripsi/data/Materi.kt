package com.mathquizz.projectskripsi.data

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class Materi(
    @DocumentId
    var materiId: String,
    val imageUrl: String,
    val title: String,
    val category: String,
    val urutan: Long,
    val progressindikator: Long,
    val progress: Int

) : Parcelable {

    constructor() : this ("","","","",0L,0L,0)
}
