package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.Artist
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun TrendingSongMiniResponse.toSongMini() = SongMini(
    id = id,
    order = 0,
    title = title,
    artists = artists.map { Artist(it) },
    difficulty = difficulty,
    thumbnailUrl = thumbnailUrl,
    isPracticable = isLearnModeAvailable == true,
    lyricsStartTimeMS = lyricsStartTimeSec * 1_000,
    lyrics = lyrics
)
@JsonClass(generateAdapter = true)
data class TrendingSongMiniResponse(
    @Json(name = "_id")
    val id: String,

    @Json(name = "title")
    val title: String,

    @Json(name = "artists")
    val artists: List<String>,

    @Json(name = "difficulty")
    val difficulty: String,

    @Json(name = "thumbnail_url")
    val thumbnailUrl: String,

    // FIXME - make this parameter compulsory from the backend
    @Json(name = "learn_mode_available")
    val isLearnModeAvailable: Boolean?,

    @Json(name = "lyrics_start_time")
    val lyricsStartTimeSec: Int,

    @Json(name = "lyrics")
    val lyrics: String,
)