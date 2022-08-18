package com.sensibol.lucidmusic.singstr.domain.model

data class UserHandle(
    val status: Int,
    val message: String,
    val data: Data
){
    data class Data(
        val userHandle:String
    )
}
