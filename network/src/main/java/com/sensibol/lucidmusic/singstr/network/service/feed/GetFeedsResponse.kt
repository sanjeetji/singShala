package com.sensibol.lucidmusic.singstr.network.service.feed


import com.sensibol.lucidmusic.singstr.domain.model.Cover
import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.domain.model.LearnSlot
import com.sensibol.lucidmusic.singstr.network.data.response.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetFeedsResponse.Data.Submit.StatisticsResponse.toStatistics() = Cover.Statistics(
    clapCount = clapCount,
    shareCount = shareCount,
    viewCount = viewCount
)

internal fun GetFeedsResponse.Data.toFeed() = Feed(
    nextPageToken = nextPageToken,
    learnSlot = learningSlots.map {
        LearnSlot(
            id = it.id,
            title = it.title,
            publishDate = it.publishDate,
            lessonId = it.lessonId,
            difficulty = it.difficulty,
            videoUrl = it.video.videoUrl,
            thumbnailUrl = it.video.thumbnailUrl,
            duration = it.video.duration
        )
    },
    covers = submits.map {
        Cover(
            id = it.attempt.id,
            caption = it.attempt.caption,
            thumbnailUrl = it.attempt.thumbnailUrl,
            songMini = it.song.toSongMini(),
            userMini = it.author.toUserMini(),
            statistics = it.statistics.toStatistics()
        )
    }
)

@JsonClass(generateAdapter = true)
internal class GetFeedsResponse(
    @Json(name = "data")
    val data: Data,
    @Json(name = "success")
    val success: Boolean
) {

    @JsonClass(generateAdapter = true)
    class Data(
        @Json(name = "next_page_token")
        val nextPageToken: String,
        @Json(name = "submits")
        val submits: List<Submit>,
        @Json(name = "learning_slot")
        val learningSlots: List<LearningSlot>
    ) {
        @JsonClass(generateAdapter = true)
        class Submit(
            @Json(name = "attempt")
            val attempt: AttemptResponse,
            @Json(name = "author")
            val author: UserMiniResponse,
            @Json(name = "song")
            val song: SongMiniResponse,
            @Json(name = "statistics")
            val statistics: StatisticsResponse
        ) {
            @JsonClass(generateAdapter = true)
            class AttemptResponse(
                @Json(name = "id")
                val id: String,
                @Json(name = "cover_url")
                val thumbnailUrl: String,
                @Json(name = "caption")
                val caption: String
            )

            @JsonClass(generateAdapter = true)
            class StatisticsResponse(
                @Json(name = "clap_count")
                val clapCount: Long,
                @Json(name = "share_count")
                val shareCount: Long,
                @Json(name = "view_count")
                val viewCount: Long
            )
        }

        @JsonClass(generateAdapter = true)
        class LearningSlot(
            @Json(name = "id")
            val id: String,
            @Json(name = "title")
            val title: String,
            @Json(name = "publish_date")
            val publishDate: String,
            @Json(name = "lesson_id")
            val lessonId: String,
            @Json(name = "difficulty")
            val difficulty: String,
            @Json(name = "video")
            val video: VideoInfoResponse
        )
    }
}