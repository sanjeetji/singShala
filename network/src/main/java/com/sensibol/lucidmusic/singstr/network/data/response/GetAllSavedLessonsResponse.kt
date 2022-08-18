package com.sensibol.lucidmusic.singstr.network.data.response
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json


@JsonClass(generateAdapter = true)
data class GetAllSavedLessonsResponse(
    @Json(name = "data")
    val data: Data,
    @Json(name = "success")
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "lesson")
        val lesson: List<MyListLessonMini>
    )
}