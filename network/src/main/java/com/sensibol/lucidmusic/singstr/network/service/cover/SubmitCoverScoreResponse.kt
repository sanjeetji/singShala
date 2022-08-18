package com.sensibol.lucidmusic.singstr.network.service.cover

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SubmitCoverScoreResponse(
    @Json(name = "success")
    val success: Boolean,

    @Json(name = "data")
    val data: Data

) {

    @JsonClass(generateAdapter = true)
    internal data class Data(
        @Json(name = "attempt_id")
        val attemptId: String,
    )
}

