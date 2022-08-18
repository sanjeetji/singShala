package com.sensibol.lucidmusic.singstr.network.service.learn

import com.sensibol.lucidmusic.singstr.domain.model.ExerciseFromId
import com.sensibol.lucidmusic.singstr.network.data.response.LessonResponseFromExerciseId
import com.sensibol.lucidmusic.singstr.network.data.response.toExercise
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetExerciseFromIdResponse.ExerciseFromIdDataResponse.toExerciseFromId() = ExerciseFromId(
    exercise = lessons[0].exercises.map { it.toExercise() },
    lessonId = _id
)

@JsonClass(generateAdapter = true)
class GetExerciseFromIdResponse(

    @Json(name = "message")
    val success: String,
    @Json(name = "data")
    val data: List<ExerciseFromIdDataResponse>,
) {
    @JsonClass(generateAdapter = true)
    class ExerciseFromIdDataResponse(
        @Json(name = "_id")
        val _id: String,
        @Json(name = "lessons")
        val lessons: List<LessonResponseFromExerciseId>,
    )
}