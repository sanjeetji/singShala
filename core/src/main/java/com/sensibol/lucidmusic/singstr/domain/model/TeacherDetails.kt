package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class TeacherDetails(
    val id: String,
    val userId: String,
    val name: String,
    val age: Int = 0 ,
    val profile_img_url: String,
    val experience: String,
    val followers: Int = 0,
    val bio: String,
    val attributes: List<Attributes>,
    val IsLoginUserfollowers:Boolean
) {
    data class Attributes(
        val details: List<Details>,
        val title: String,
        val order: Int,
        var image: String = ""
    ){
        data class Details(
            val title: String,
            val sub_title: String,
        )
    }
}


