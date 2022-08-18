package com.sensibol.lucidmusic.singstr.network.service.learn
import com.squareup.moshi.JsonClass

import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class SaveAllToMyListRequestBody(
    @Json(name = "lesson_ids")
    val lessonIds: List<String>
)