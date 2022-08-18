package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.Song
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun SongResponse.toSong(): Song = Song(
    artists = artists.map { it.toArtist() },
    difficulty = difficulty,
    id = id,
    isPracticable = isLearnModeAvailable,
    order = order,
    thumbnailUrl = thumbnailUrl,
    title = title,
    lyricsStartTimeMS = lyricsStartTimeSec,
    lyrics = lyrics
)

@JsonClass(generateAdapter = true)
internal data class SongResponse(
    @Json(name = "artists")
    val artists: List<ArtistResponse>,

    @Json(name = "difficulty")
    val difficulty: String,

    @Json(name = "id")
    val id: String,

    @Json(name = "learn_mode_available")
    val isLearnModeAvailable: Boolean,

    @Json(name = "order")
    val order: Int,

    @Json(name = "thumbnail_url")
    val thumbnailUrl: String,

    @Json(name = "title")
    val title: String,

    @Json(name = "lyrics_start_time")
    val lyricsStartTimeSec: Int,

    @Json(name = "lyrics")
    val lyrics: String,

    )