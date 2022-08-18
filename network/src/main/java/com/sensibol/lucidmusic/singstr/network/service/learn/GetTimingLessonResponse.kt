package com.sensibol.lucidmusic.singstr.network.service.learn

import com.sensibol.lucidmusic.singstr.network.data.response.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal class GetTimingLessonResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "data")
    val data: GetTuneLessonData,
) {
    @JsonClass(generateAdapter = true)
    class GetTuneLessonData(
        @Json(name = "by_time")
        val getByTimingLesson: GetByTimingLesson
    ){
        @JsonClass(generateAdapter = true)
        class GetByTimingLesson(
            @Json(name = "Lessons")
            val lessons : List<LessonMiniResponse>
        )
    }
}