package com.sensibol.lucidmusic.singstr.network.service.learn

import com.sensibol.lucidmusic.singstr.network.data.response.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal class GetTuneLessonResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "data")
    val data: GetTuneLessonData,
) {
    @JsonClass(generateAdapter = true)
    class GetTuneLessonData(
        @Json(name = "by_tune")
        val getByTuneLessons: GetByTuneLessons
    ){
        @JsonClass(generateAdapter = true)
        class GetByTuneLessons(
            @Json(name = "Lessons")
            val lessons: List<LessonMiniResponse>
        )
    }
}