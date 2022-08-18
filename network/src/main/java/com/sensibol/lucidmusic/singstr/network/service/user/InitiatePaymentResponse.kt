package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class InitiatePaymentResponse(
    val success: Boolean,
    val data: Data
) {
    @JsonClass(generateAdapter = true)
    internal data class Data(
        @Json(name = "token")
        val token: String,
    )
}