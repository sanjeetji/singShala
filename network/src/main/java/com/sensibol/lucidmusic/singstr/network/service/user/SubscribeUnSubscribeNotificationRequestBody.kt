package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SubscribeUnSubscribeNotificationRequestBody(
    @Json(name = "subscription_id")
    val subscriberId: String,
    @Json(name = "subscription_type")
    val subscriptionType: String,
)