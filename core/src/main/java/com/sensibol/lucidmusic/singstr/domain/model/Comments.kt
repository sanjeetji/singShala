package com.sensibol.lucidmusic.singstr.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class Comments(
    val comments: List<Comment>,
    val total: Int,
    val nextPageToken: String,
) {
    data class Comment(
        val id: String,
        val attempt_id: String,
        val comment: String,
        val timestamp: Long,
        val user: CommentUser,
        val time: String,
    ){
        data class CommentUser(
            val id: String,
            val firstName: String,
            val lastName: String,
            val profile_url: String,
            val display_name: String,
        )
    }
}


