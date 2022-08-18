package com.sensibol.lucidmusic.singstr.network.service.user


import com.sensibol.lucidmusic.singstr.domain.model.Attempt
import com.sensibol.lucidmusic.singstr.network.data.response.SubmitScore
import com.squareup.moshi.JsonClass

internal fun GetUserCoversResponse.Data.Submit.toCover() = Attempt(
    id = attempt_id,
    comment = comment,
    coverUrl = cover_url,
    publicMediaUrl = public_media_url,
    score = score.toScore(),
    songId = song_id,
    songTitle = song_title
)

internal fun SubmitScore.toScore(): Attempt.Score =
    Attempt.Score(detail_score.toDetailScore(), total)

internal fun SubmitScore.DetailScore.toDetailScore(): Attempt.Score.DetailScore =
    Attempt.Score.DetailScore(lesson_score, reviewData, timing_score, tune_score)

@JsonClass(generateAdapter = true)
internal data class GetUserCoversResponse(
    val success: Boolean,
    val data: Data,
) {

    @JsonClass(generateAdapter = true)
    internal data class Data(
        val submits: List<Submit>
    ) {

        @JsonClass(generateAdapter = true)
        data class Submit(
            val attempt_id: String,
            val attempt_time: String,
            val comment: String,
            val cover_url: String,
            val public_media_url: String,
            val score: SubmitScore,
            val song_id: String,
            val song_title: String
        )
    }
}
