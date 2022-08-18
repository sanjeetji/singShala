package com.sensibol.lucidmusic.singstr.network.service.cover

import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Score(

    @Json(name = "total")
    val total: Float,

    @Json(name = "detail_score")
    val detailScore: DetailScore,

    @Json(name = "difficulty_level")
    val difficultyLevel: Int


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


internal fun SingScore.toScore(songMini: SongMini) = Score(
    total = totalScore,
    difficultyLevel = when(songMini.difficulty) {
        "hard"->3
        "medium"->2
        else -> 1
    },
    detailScore = toDetailScore()
)

internal fun SingScore.toDetailScore() = Score.DetailScore(
    tuneScore = tuneScore,
    timingScore = timingScore,
    reviewData = reviewData
)
