package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InAppPurchaseVerifyRequestBody(
    @Json(name = "platform")
    val platform: String,
    @Json(name = "receipt")
    val receipt: String,
    @Json(name = "signature")
    val signature: String
)
