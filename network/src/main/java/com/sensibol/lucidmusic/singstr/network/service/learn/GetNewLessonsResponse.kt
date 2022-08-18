package com.sensibol.lucidmusic.singstr.network.service.learn


import com.sensibol.lucidmusic.singstr.network.data.response.LessonMiniResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
@JsonClass(generateAdapter = true)
internal class GetNewLessonsResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "data")
    val data: LessonDataResponse,
) {
    @JsonClass(generateAdapter = true)
    class LessonDataResponse(
        @Json(name = "levels")
        val lesson: List<LessonMiniResponse>
    )
}