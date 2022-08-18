package com.sensibol.lucidmusic.singstr.network.service.feed

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddFeedCommentRequest(
    @Json(name = "comment")
    val comment: String,

    @Json(name = "attempt_id")
    val attemptId: String
)
