package com.sensibol.lucidmusic.singstr.network.service.curriculum

import com.sensibol.lucidmusic.singstr.domain.model.ExerciseScore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Score(

    @Json(name = "total")
    val total: Float,

    @Json(name = "detail_score")
    val detailScore: DetailScore
) {

    @JsonClass(generateAdapter = true)
    data class DetailScore(
        @Json(name = "lesson_score")
        val lessonScore: Float = 0.0f,

        @Json(name = "timing_score")
        val timingScore: Float,

        @Json(name = "tune_score")
        val tuneScore: Float,

        @Json(name = "reviewData")
        val reviewData: String
    )
}


internal fun ExerciseScore.toScore() = Score(
    total = totalScore,
    detailScore = toDetailScore()
)

internal fun ExerciseScore.toDetailScore() = Score.DetailScore(
    tuneScore = tuneScore,
    timingScore = timingScore,
    reviewData = reviewData
)
