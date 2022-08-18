package com.sensibol.lucidmusic.singstr.network.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VideoInfoResponse(
    @Json(name = "video_url")
    val videoUrl: String,
    @Json(name = "thumbnail_url")
    val thumbnailUrl: String,
    @Json(name = "duration")
    val duration: String
)