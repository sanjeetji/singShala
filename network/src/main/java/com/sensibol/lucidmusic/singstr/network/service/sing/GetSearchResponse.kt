package com.sensibol.lucidmusic.singstr.network.service.sing


import com.sensibol.lucidmusic.singstr.domain.model.SearchData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetSearchResponse.SearchData.toSearchData() = SearchData(
    lesson = lesson.map { it.toLesson() },
    song = song.map { it.toSong() },
    user = user.map { it.toUser() }
)

internal fun GetSearchResponse.SearchData.Lesson.toLesson() = SearchData.Lesson(
    difficulty = difficulty,
    id = id,
    primaryTag = primaryTag,
    status = status,
    subscriptionPurchaseType = subscriptionPurchaseType,
    tags = tags,
    teacherName = teacherName,
    thumbnailUrl = thumbnailUrl,
    title = title,
    videoName = videoName,
)

internal fun GetSearchResponse.SearchData.Song.toSong() = SearchData.Song(
    album = album,
    artists = artists,
    difficulty = difficulty,
    duration = duration,
    genres = genres,
    id = id,
    status = status,
    thumbnailUrl = thumbnailUrl,
    title = title,
)

internal fun GetSearchResponse.SearchData.User.toUser() = SearchData.User(
    firstName = firstName,
    gender = gender,
    lastName = lastName,
    profileImg = profileImg,
    userHandle = userHandle,
    id = id
)

@JsonClass(generateAdapter = true)
data class GetSearchResponse(
    @Json(name = "data")
    val data: SearchData,
    @Json(name = "success")
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class SearchData(
        @Json(name = "lesson")
        val lesson: List<Lesson>,
        @Json(name = "song")
        val song: List<Song>,
        @Json(name = "user")
        val user: List<User>
    ) {
        @JsonClass(generateAdapter = true)
        data class Lesson(
            @Json(name = "difficulty")
            val difficulty: String,
            @Json(name = "id")
            val id: String,
            @Json(name = "primary_tag")
            val primaryTag: String,
            @Json(name = "status")
            val status: String,
            @Json(name = "subscription_purchase_type")
            val subscriptionPurchaseType: String,
            @Json(name = "tags")
            val tags: List<String>,
            @Json(name = "teacher_name")
            val teacherName: String,
            @Json(name = "thumbnail_url")
            val thumbnailUrl: String,
            @Json(name = "title")
            val title: String,
            @Json(name = "video_name")
            val videoName: String
        )

        @JsonClass(generateAdapter = true)
        data class Song(
            @Json(name = "album")
            val album: String,
            @Json(name = "artists")
            val artists: List<String>,
            @Json(name = "difficulty")
            val difficulty: String,
            @Json(name = "duration")
            val duration: String,
            @Json(name = "genres")
            val genres: List<String>,
            @Json(name = "id")
            val id: String,
            @Json(name = "status")
            val status: String,
            @Json(name = "thumbnail_url")
            val thumbnailUrl: String,
            @Json(name = "title")
            val title: String
        )

        @JsonClass(generateAdapter = true)
        data class User(
            @Json(name = "first_name")
            val firstName: String,
            @Json(name = "gender")
            val gender: String,
            @Json(name = "id")
            val id: String,
            @Json(name = "last_name")
            val lastName: String,
            @Json(name = "profile_img")
            val profileImg: String,
            @Json(name = "user_handle")
            val userHandle: String
        )
    }
}