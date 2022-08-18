package com.sensibol.lucidmusic.singstr.network.service.curriculum

import com.sensibol.lucidmusic.singstr.domain.model.ExerciseScore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubmitExerciseScoreRequestBody(

    @Json(name = "exercise_id")
    val exerciseId: String,

    @Json(name = "time_info")
    val timeInfo: TimeInfo,

    @Json(name = "score")
    val score: Score,

    @Json(name = "media_type")
    val mediaType: String,
)

internal fun ExerciseScore.toSubmitExerciseScoreRequestBody() = SubmitExerciseScoreRequestBody(
    exerciseId = exerciseId,
    timeInfo = toTimeInfo(),
    score = toScore(),
    // FIXME - hard-coded media_type
    mediaType = "Audio",
)
