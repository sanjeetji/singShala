package com.sensibol.lucidmusic.singstr.network.service.learn
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json


@JsonClass(generateAdapter = true)
data class MyListRequestBody(
    @Json(name = "lesson_id")
    val lessonId: String
)