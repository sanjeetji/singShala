package com.sensibol.lucidmusic.singstr.network.service.feed

import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.network.data.response.toSongMini
import com.sensibol.lucidmusic.singstr.network.data.response.toUserMini
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetFollowingFeedResponse.FollowingFeed.FollowingFeedData.Attempt.toAttemptData() = CoverData.Attempt(
    id = id,
    orderId = orderId,
    coverUrl = coverUrl,
    time = time,
    liked = liked,
    caption = caption
)

internal fun GetFollowingFeedResponse.FollowingFeed.FollowingFeedData.SongMini.toSongMini() = SongMini(
    id = id,
    order = 0,
    title = title,
    lyrics = lyrics,
    artists = listOf<Artist>(Artist(artists[0].name)),
    difficulty = difficulty,
    thumbnailUrl = thumbnailUrl,
    isPracticable = false,
    lyricsStartTimeMS = lyricsStartTime,
)

internal fun GetFollowingFeedResponse.FollowingFeed.FollowingFeedData.UserMini.toUserMini() = UserMini(
    id = _id,
    handle = userHandle,
    dpUrl = profileImg
)

internal fun GetFollowingFeedResponse.FollowingFeed.FollowingFeedData.Statistics.toStatistics() = Cover.Statistics(
    shareCount = shareCount,
    clapCount = clapCount,
    viewCount = viewCount
)

internal fun GetFollowingFeedResponse.FollowingFeed.FollowingFeedData.SongMini.Artist.toArtistData() = CoverData.SongMini.Artist(
    name = name
)

internal fun GetFollowingFeedResponse.FollowingFeed.FollowingFeedData.SongMini.Artist.toArtist() = Artist(
    name = name
)

internal fun GetFollowingFeedResponse.FollowingFeed.toFeed() = Feed(
    nextPageToken = nextPageToken,
    learnSlot =  emptyList<LearnSlot>(),
    covers = submits.map {
        Cover(
            id = it.attempt.id,
            caption = it.attempt.caption,
            thumbnailUrl = it.attempt.coverUrl,
            songMini = it.song.toSongMini(),
            userMini = it.author.toUserMini(),
            statistics = it.statistics.toStatistics()
        )
    }
)

@JsonClass(generateAdapter = true)
data class GetFollowingFeedResponse(
    @Json(name = "status")
    val status:Int,
    @Json(name = "message")
    val message: String,
    @Json(name = "data")
    val data: FollowingFeed
) {
    @JsonClass(generateAdapter = true)
    data class FollowingFeed(
        @Json(name = "submits")
        val submits: List<FollowingFeedData>,
        @Json(name = "next_page_token")
        val nextPageToken:String
    ) {
        @JsonClass(generateAdapter = true)
        data class FollowingFeedData(
            @Json(name = "attempt")
            val attempt: Attempt,
            @Json(name = "song")
            val song: SongMini,
            @Json(name = "author")
            val author: UserMini,
            @Json(name = "statistics")
            val statistics: Statistics
        ){
            @JsonClass(generateAdapter = true)
            data class Attempt(
                @Json(name = "id")
                val id: String,
                @Json(name = "order_id")
                val orderId: String,
                @Json(name = "cover_url")
                val coverUrl: String,
                @Json(name = "time")
                val time: String,
                @Json(name = "liked")
                val liked: Boolean,
                @Json(name = "caption")
                val caption: String
            )

            @JsonClass(generateAdapter = true)
            data class SongMini(
                @Json(name = "id")
                val id: String,
                @Json(name = "title")
                val title: String,
                @Json(name = "album")
                val album: String,
                @Json(name = "year")
                val year: Int,
                @Json(name = "lyrics")
                val lyrics: String,
                @Json(name = "lyrics_start_time")
                val lyricsStartTime: Int,
                @Json(name = "artists")
                val artists: List<Artist>,
                @Json(name = "thumbnail_url")
                val thumbnailUrl: String,
                @Json(name = "difficulty")
                val difficulty: String,
            ){
                @JsonClass(generateAdapter = true)
                data class Artist(
                    @Json(name = "name")
                    val name: String
                )
            }

            @JsonClass(generateAdapter = true)
            data class UserMini(
                @Json(name = "first_name")
                val firstName: String,
                @Json(name = "last_name")
                val lastName: String,
                @Json(name = "profile_img")
                val profileImg: String,
                @Json(name = "user_handle")
                val userHandle: String,
                @Json(name = "_id")
                val _id: String
            )

            @JsonClass(generateAdapter = true)
            data class Statistics(
                @Json(name = "like_count")
                val likeCount: Int,
                @Json(name = "comment_count")
                val commentCount: Int,
                @Json(name = "share_count")
                val shareCount: Long,
                @Json(name = "clap_count")
                val clapCount: Long,
                @Json(name = "view_count")
                val viewCount: Long,
            )
        }
    }
}