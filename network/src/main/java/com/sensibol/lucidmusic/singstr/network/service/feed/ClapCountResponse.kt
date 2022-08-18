package com.sensibol.lucidmusic.singstr.network.service.feed

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ClapCountResponse(
    @Json(name = "data")
    val data: Data,

    @Json(name = "success")
    val success: Boolean
) {

    @JsonClass(generateAdapter = true)
    class Data
}
