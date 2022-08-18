package com.sensibol.lucidmusic.singstr.network.service.learn

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LessonWatchedRequestBody(

    @Json(name = "lesson_id")
    val lessonId: String,

    @Json(name = "duration")
    val watchedDurationMS: Long
)