package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.Attempt
import com.sensibol.lucidmusic.singstr.domain.model.Submits
import com.sensibol.lucidmusic.singstr.network.data.response.SubmitScore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun  GetOtherUserSubmitsResponse.OtherUserSubmitsData.toOtherUserSubmit() = Submits(
    attempt = submits.map { it.toAttempt() }
)

internal fun GetOtherUserSubmitsResponse.OtherUserSubmitsData.Submit.toAttempt() = Attempt(
    id = attemptId,
    comment = comment,
    coverUrl = coverUrl,
    publicMediaUrl = publicMediaUrl,
    score = score.toScore(),
    songId = songId,
    songTitle = songTitle
)


@JsonClass(generateAdapter = true)
data class GetOtherUserSubmitsResponse(
    @Json(name = "data")
    val `data`: OtherUserSubmitsData,
    @Json(name = "success")
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class OtherUserSubmitsData(
        @Json(name = "submits")
        val submits: List<Submit>
    ) {
        @JsonClass(generateAdapter = true)
        data class Submit(
            @Json(name = "attempt_id")
            val attemptId: String,
            @Json(name = "attempt_time")
            val attemptTime: String,
            @Json(name = "comment")
            val comment: String,
            @Json(name = "cover_url")
            val coverUrl: String,
            @Json(name = "public_media_url")
            val publicMediaUrl: String,
            @Json(name = "score")
            val score: SubmitScore,
            @Json(name = "song_id")
            val songId: String,
            @Json(name = "song_title")
            val songTitle: String
        )
    }
}