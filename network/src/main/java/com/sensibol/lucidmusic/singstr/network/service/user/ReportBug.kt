package com.sensibol.lucidmusic.singstr.network.service.user


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReportBug(
    @Json(name = "message")
    val message: String,
)