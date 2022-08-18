package com.sensibol.lucidmusic.singstr.network.data.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class LessonResponseFromExerciseId (
    @Json(name = "order")
    val displayOrder: Int,

    @Json(name = "_id")
    val id: String,

    @Json(name = "exercises")
    val exercises: List<ExerciseResponseFromId>,
)