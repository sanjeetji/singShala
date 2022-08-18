package com.sensibol.lucidmusic.singstr.domain.model

data class CheckUserExists(
    val status: Int,
    val message: String,
    val data: Data
){
    data class Data(
        val userExists:Boolean
    )
}
