package com.sensibol.lucidmusic.singstr.network.service.feed

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommentOnCoverRequest(
    @Json(name = "comment")
    val commentId: String,

    @Json(name = "attempt_id")
    val attemptId: String,

    @Json(name = "tag_user_id")
    val tagUserId:List<String>
)
