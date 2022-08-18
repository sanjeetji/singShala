package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.NodeDraft
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetDraftOrCoverResponse.GetDraftOrCoverResponseData.toNodeDraft() = NodeDraft(
    attemptId = attemptId,
    totalScore = score.total.toInt(),
    songId = songId,
    attemptTime = attemptTime,
    songTitle = songTitle,
    coverUrl = coverUrl,
    publicMediaUrl = publicMediaUrl
)

@JsonClass(generateAdapter = true)
internal data class GetDraftOrCoverResponse(
    @Json(name = "data")
    val data: List<GetDraftOrCoverResponseData>,
    @Json(name = "message")
    val success: String
) {
    @JsonClass(generateAdapter = true)
    data class GetDraftOrCoverResponseData(
        @Json(name = "attempt_id")
        val attemptId: String,
        @Json(name = "score")
        val score: Score,
        @Json(name = "attempt_time")
        val attemptTime: String,
        @Json(name = "song_id")
        val songId: String,
        @Json(name = "song_title")
        val songTitle: String,
        @Json(name = "cover_url")
        val coverUrl: String,
        @Json(name = "public_media_url")
        val publicMediaUrl: String,
    ) {
        @JsonClass(generateAdapter = true)
        data class Score(
            @Json(name = "total")
            val total: Float,
        )
    }
}