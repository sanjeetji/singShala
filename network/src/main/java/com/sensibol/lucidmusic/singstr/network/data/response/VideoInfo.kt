package com.sensibol.lucidmusic.singstr.network.data.response
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json


@JsonClass(generateAdapter = true)
data class VideoInfo(
    val duration: String,
    val id: String,
    @Json(name = "thumbnail_url")
    val thumbnailUrl: String,
    @Json(name = "video_url")
    val videoUrl: String
)