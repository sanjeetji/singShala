package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.utils.UniqueFixture.newStringId
import com.sensibol.lucidmusic.singstr.network.service.sing.GetSongsResponse

internal object NetworkResponseFixture {

    fun newGetSongResponse(
        songMinis: List<SongMiniResponse>
    ) = GetSongsResponse(
        true,
        SongsResponse(songMinis)
    )

    fun newSongResponse(
        id: String,
        title: String = "Sample Song Title",
        thumbnailUrl: String = "http://sample/thumbnail/url"
    ) = SongMiniResponse(
        id = id,
        order = 1,
        title = title,
        album = "Album",
        year = 2021,
        language = "Hindi",
        artistResponses = listOf(ArtistResponse(id = "artistId", name = "Artist Name")),
        genres = listOf("Genre"),
        duration = "90",
        difficulty = "Easy",
        thumbnailURL = thumbnailUrl,
        previewURL = "previewURL",
        mediaURL = "mediaURL",
        isAvailable = true,
        isLearnModeAvailable = true,
        /*usage = listOf(UsageResponse(true))*/
    )


    fun newNSongResponses(n: Int): List<SongMiniResponse> {
        val result = mutableSetOf<SongMiniResponse>()
        while (result.size < n) {
            result.add(newSongResponse(newStringId()))
        }
        return result.toList()
    }
}