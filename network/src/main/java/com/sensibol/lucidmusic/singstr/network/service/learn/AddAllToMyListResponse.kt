package com.sensibol.lucidmusic.singstr.network.service.learn

import com.sensibol.lucidmusic.singstr.network.data.response.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal class AddAllToMyListResponse(
    @Json(name = "message")
    val success: String,
    @Json(name = "data")
    val data: String,
)