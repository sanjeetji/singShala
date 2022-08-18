package com.sensibol.lucidmusic.singstr.network.service.feed

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClapCountRequest(
    @Json(name = "attempt_id")
    val attemptId: String,

    @Json(name = "clap_count")
    val clapCount: Int
)
