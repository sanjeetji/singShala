package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.DownloadVideoUrl
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetDownloadVideoResponse.toDownloadVideoUrl() = DownloadVideoUrl(
    videoUrl =  data
)
@JsonClass(generateAdapter = true)
data class GetDownloadVideoResponse(
    @Json(name = "message")
    val message: String,
    @Json(name = "data")
    val data: String
)
