package com.sensibol.lucidmusic.singstr.network.service.cover

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeleteDraftRequestBody(
    @Json(name = "attempt_id")
    val attemptId: String
)
