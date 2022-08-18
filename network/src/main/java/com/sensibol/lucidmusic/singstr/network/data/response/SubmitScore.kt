package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.Attempt
import com.squareup.moshi.JsonClass

internal fun SubmitScore.toScore() = Attempt.Score(
    detailScore = detail_score.toDetailScore(),
    total = total
)

internal fun SubmitScore.DetailScore.toDetailScore() = Attempt.Score.DetailScore(
    lessonScore = lesson_score,
    reviewData = reviewData,
    timingScore = timing_score,
    tuneScore = tune_score
)

@JsonClass(generateAdapter = true)
data class SubmitScore(
    val detail_score: DetailScore,
    val total: Float
) {

    @JsonClass(generateAdapter = true)
    data class DetailScore(
        val lesson_score: Float,
        val reviewData: String,
        val timing_score: Float,
        val tune_score: Float
    )
}