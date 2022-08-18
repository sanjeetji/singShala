package com.sensibol.lucidmusic.singstr.network.service.sing.detailAnalysis


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Song(
    @Json(name = "album")
    val album: String,
    @Json(name = "artists")
    val artists: List<Artist>,
    @Json(name = "difficulty")
    val difficulty: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "lyrics")
    val lyrics: String,
    @Json(name = "lyrics_start_time")
    val lyricsStartTime: Int,
    @Json(name = "order")
    val order: Int,
    @Json(name = "thumbnail_url")
    val thumbnailUrl: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "year")
    val year: Int
)