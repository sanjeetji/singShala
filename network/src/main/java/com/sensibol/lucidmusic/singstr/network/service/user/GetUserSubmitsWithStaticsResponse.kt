package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.network.data.response.SubmitScore
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetUserSubmitsWithStaticsResponse.toOtherUserSubmitWithStatics() = SubmitsWithStatics(
    attempt = data.map { it.toAttemptWithStatics() }
)

internal fun GetUserSubmitsWithStaticsResponse.SubmitWithStatics.SubmitStatics.toCoverStatics() =
    Cover.Statistics(
        clapCount = clapCount,
        shareCount = shareCount,
        viewCount = viewCount
    )

internal fun GetUserSubmitsWithStaticsResponse.SubmitWithStatics.toAttemptWithStatics() = AttemptWithStatics(
    id = attemptId,
    comment = comment,
    coverUrl = coverUrl,
    publicMediaUrl = publicMediaUrl,
    songId = songId,
    songTitle = songTitle,
    statics = submitStatics.toCoverStatics()
)


@JsonClass(generateAdapter = true)
data class GetUserSubmitsWithStaticsResponse(
    @Json(name = "data")
    val data: List<SubmitWithStatics>,
    @Json(name = "message")
    val success: String
) {
    @JsonClass(generateAdapter = true)
    data class SubmitWithStatics(
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
        val songTitle: String,
        @Json(name = "statistics")
        val submitStatics: SubmitStatics
    ) {
        @JsonClass(generateAdapter = true)
        data class SubmitStatics(
            @Json(name = "clap_count")
            val clapCount: Long,
            @Json(name = "share_count")
            val shareCount: Long,
            @Json(name = "view_count")
            val viewCount: Long
        )
    }

}