package com.mathquizz.projectskripsi.data

data class User(
    val nim : Long,
    val name: String,
    val prodi: String,
    val semester: Int,
    val email: String,
    var imagePath: String = ""

)
{
    constructor(): this(0,"","",0,"","")
}
