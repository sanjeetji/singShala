package com.sensibol.lucidmusic.singstr.network.service.learn


import com.sensibol.lucidmusic.singstr.network.data.response.LearnContentDataResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal class GetLearnContentResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "data")
    val data: LearnContentDataResponse,
)