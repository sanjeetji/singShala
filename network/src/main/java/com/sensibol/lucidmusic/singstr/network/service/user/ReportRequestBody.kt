package com.sensibol.lucidmusic.singstr.network.service.user


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReportRequestBody(
    @Json(name = "message")
    val message: String,
    @Json(name = "reason")
    val reason: List<String>,
    @Json(name = "id")
    val reportedFor: String
)