package com.sensibol.lucidmusic.singstr.domain.model

import com.sensibol.lucidmusic.singstr.domain.model.ArtistFixture.newArtists
import com.sensibol.lucidmusic.singstr.domain.utils.RandomFixture.randomInt
import com.sensibol.lucidmusic.singstr.domain.utils.UniqueFixture
import com.sensibol.lucidmusic.singstr.domain.utils.UniqueFixture.newStringId

object SongFixture {

    fun newSong(
        id: String = newStringId(),
        order: Int = randomInt(from = 0),
        title: String = "Song Title",
        album: String = "Album",
        artists: List<Artist> = newArtists(randomInt(from = 1, until = 4)),
        genres: List<String> = listOf("Rock", "Pop"),
        duration: String = "3:14",
        difficulty: String = "Easy",
        language: String = "Hindi",
        isAvailable: Boolean = true,
        mediaUrl: String = UniqueFixture.newUrl(),
        previewUrl: String = UniqueFixture.newUrl(),
        thumbnailUrl: String = UniqueFixture.newUrl(),
        year: Int = randomInt(from = 1900, until = 2022),
        isPremium: Boolean = false,
        lyricsStartTimeSec: Int = randomInt(from = 10, until = 30),
        lyrics: String = "Lyrics of the song"
    ): Song = Song(
        id = id,
        order = order,
        title = title,
        artists = artists,
        difficulty = difficulty,
        thumbnailUrl = thumbnailUrl,
        isPracticable = true,
        lyricsStartTimeMS = lyricsStartTimeSec,
        lyrics = lyrics
    )

    fun newNSongs(n: Int = 10): Set<Song> {
        val result = mutableSetOf<Song>()
        while (result.size < n) {
            result.add(newSong(newStringId()))
        }
        return result
    }
}