package com.sensibol.lucidmusic.singstr.network.service.feed

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddFeedCommentReplyResponse(
    @Json(name = "status")
    val status: Int,

    @Json(name = "message")
    val message: String,

    @Json(name = "data")
    val data: String,

)
