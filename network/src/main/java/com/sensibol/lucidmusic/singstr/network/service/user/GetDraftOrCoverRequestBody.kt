package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetDraftOrCoverRequestBody (
    @Json(name = "user_id")
    val userId: String,
    @Json(name = "type")
    val type: String,
)