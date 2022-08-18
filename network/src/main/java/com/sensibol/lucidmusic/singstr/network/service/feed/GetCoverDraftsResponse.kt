package com.sensibol.lucidmusic.singstr.network.service.feed


import com.sensibol.lucidmusic.singstr.domain.model.DraftsPage
import com.sensibol.lucidmusic.singstr.domain.model.Draft
import com.sensibol.lucidmusic.singstr.network.data.response.SongMiniResponse
import com.sensibol.lucidmusic.singstr.network.data.response.UserMiniResponse
import com.sensibol.lucidmusic.singstr.network.data.response.toSongMini
import com.sensibol.lucidmusic.singstr.network.data.response.toUserMini
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


internal fun GetCoverDraftsResponse.Data.toDrafts() = DraftsPage(
    drafts = drafts.map {
        Draft(
            song = it.song.toSongMini(),
            attemptId = it.attempt.id,
            userMini = it.author.toUserMini(),
            timeStamp = it.attempt.time,
            totalXP = it.attempt.score.total,
            thumbnailUrl = it.attempt.coverUrl,
        )
    },
    nextPageToken = nextPageToken

)


@JsonClass(generateAdapter = true)
data class GetCoverDraftsResponse(
    @Json(name = "data")
    val data: Data,
    @Json(name = "success")
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "drafts")
        val drafts: List<Draft>,
        @Json(name = "next_page_token")
        val nextPageToken: String
    ) {
        @JsonClass(generateAdapter = true)
        data class Draft(
            @Json(name = "attempt")
            val attempt: Attempt,
            @Json(name = "author")
            val author: UserMiniResponse,
            @Json(name = "song")
            val song: SongMiniResponse,
            @Json(name = "statistics")
            val statistics: Statistics
        ) {
            @JsonClass(generateAdapter = true)
            data class Attempt(
                @Json(name = "caption")
                val caption: String,
                @Json(name = "cover_url")
                val coverUrl: String,
                @Json(name = "id")
                val id: String,
                @Json(name = "liked")
                val liked: Boolean,
                @Json(name = "order_id")
                val orderId: String,
                @Json(name = "score")
                val score: ScoreX,
                @Json(name = "time")
                val time: String
            ) {
                @JsonClass(generateAdapter = true)
                data class ScoreX(
                    @Json(name = "total")
                    val total: Double
                )
            }

            @JsonClass(generateAdapter = true)
            data class Statistics(
                @Json(name = "clap_count")
                val clapCount: Int,
                @Json(name = "comment_count")
                val commentCount: Int,
                @Json(name = "like_count")
                val likeCount: Int,
                @Json(name = "play_count")
                val playCount: Int,
                @Json(name = "share_count")
                val shareCount: Int,
                @Json(name = "view_count")
                val viewCount: Int
            )
        }
    }
}