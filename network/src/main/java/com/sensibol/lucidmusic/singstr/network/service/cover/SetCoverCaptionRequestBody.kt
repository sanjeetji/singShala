package com.sensibol.lucidmusic.singstr.network.service.cover

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SetCoverCaptionRequestBody(

    @Json(name = "attmpt_id")
    val attemptId: String,

    @Json(name = "caption")
    val caption: String,
)
