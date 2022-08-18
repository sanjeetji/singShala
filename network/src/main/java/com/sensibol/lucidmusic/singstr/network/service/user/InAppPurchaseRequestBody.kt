package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InAppPurchaseRequestBody(
    @Json(name = "product_id")
    val product_id: String
)
