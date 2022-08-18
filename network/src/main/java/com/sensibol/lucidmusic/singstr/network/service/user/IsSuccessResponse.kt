package com.sensibol.lucidmusic.singstr.network.service.user


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
internal data class IsSuccessResponse(
    @Json(name = "success")
    val success: Boolean
)