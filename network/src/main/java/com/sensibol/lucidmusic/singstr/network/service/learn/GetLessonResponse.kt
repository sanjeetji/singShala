package com.sensibol.lucidmusic.singstr.network.service.learn

import com.sensibol.lucidmusic.singstr.network.data.response.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal class GetLessonResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "data")
    val data: LessonDataResponse,
) {
    @JsonClass(generateAdapter = true)
    class LessonDataResponse(
        @Json(name = "lesson")
        val lesson: LessonResponse
    )
}