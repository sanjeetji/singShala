package com.sensibol.lucidmusic.singstr.network.service.feed

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReplyOnCommentRequest(
    @Json(name = "attempt_id")
    val attemptId: String,

    @Json(name = "comment_id")
    val commentId: String,

    @Json(name = "reply")
    val reply: String,

    @Json(name = "tag_user_id")
    val tagUserId:List<String>
)
