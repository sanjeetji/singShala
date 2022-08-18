package com.sensibol.lucidmusic.singstr.network.data.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetPublishUrlResponse(
    @Json(name = "data")
    val `data`: PublishUrlResponse,
    @Json(name = "success")
    val success: Boolean
)

@JsonClass(generateAdapter = true)
data class PublishUrlResponse(
    @Json(name = "expiry_in_sec")
    val expiryInSec: Int,
    @Json(name = "media_url")
    val mediaUrl: String,
    @Json(name = "meta_url")
    val metaUrl: String,
    @Json(name = "raw_recording_url")
    val rawRecordingUrl: String,
    @Json(name = "share_url")
    val shareUrl: String,
    @Json(name = "token")
    val token: String
)
