package com.sensibol.lucidmusic.singstr.network.data.response

import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json


@JsonClass(generateAdapter = true)
class LessonInfoResonse(
    val difficulty: String,
    @Json(name = "display_name")
    val displayName: String,
    val id: String,
    val order: Int,
    @Json(name = "subscription_purchase_type")
    val subscriptionPurchaseType: String,
    @Json(name = "primary_tag")
    val type: String,
    @Json(name = "video")
    val videoInfo: VideoInfo
)