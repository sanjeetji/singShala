package com.sensibol.lucidmusic.singstr.network.service.feed

import com.sensibol.lucidmusic.singstr.domain.model.Comment
import com.sensibol.lucidmusic.singstr.domain.model.UserMini
import com.sensibol.lucidmusic.singstr.network.data.response.UserMiniResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetCoverCommentsResponse.toComments() =
    data.comments.map { it.toComment() }

internal fun GetCoverCommentsResponse.Data.CommentResponse.toComment() = Comment(
    id = id,
    comment = comment,
    timestamp = timestamp,
    userMini = UserMini(
        id = user.id,
        handle = user.handle,
        dpUrl = user.dpUrl
    )
)

@JsonClass(generateAdapter = true)
data class GetCoverCommentsResponse(
    @Json(name = "data")
    val data: Data,
    @Json(name = "success")
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "comments")
        val comments: List<CommentResponse>,

        @Json(name = "total")
        val total: Int,

        @Json(name = "nextPageToken")
        val nextPageToken: String,

        ) {
        @JsonClass(generateAdapter = true)
        data class CommentResponse(
            @Json(name = "id")
            val id: String,

            @Json(name = "attempt_id")
            val attempt_id: String,

            @Json(name = "comment")
            val comment: String,

            @Json(name = "timestamp")
            val timestamp: Long,

            @Json(name = "user")
            val user: UserMiniResponse,

            @Json(name = "time")
            val time: String,
        )
    }
}
