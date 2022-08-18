package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.utils.UniqueFixture.newStringId
import com.sensibol.lucidmusic.singstr.domain.model.Artist
import com.sensibol.lucidmusic.singstr.domain.model.Song
import com.sensibol.lucidmusic.singstr.network.data.response.NetworkResponseFixture.newGetSongResponse
import com.sensibol.lucidmusic.singstr.network.data.response.NetworkResponseFixture.newNSongResponses
import com.sensibol.lucidmusic.singstr.network.service.sing.toSongMini
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
internal class GetSongsResponseKtTest {

    @Test
    fun `SongResponse with full data maps to Song domain model`() {
        // given
        val songId = newStringId()
        val artistId = newStringId()
        val songResponse = SongMiniResponse(
            id = songId,
            order = 1245,
            title = "Some Title",
            album = "Some Album",
            year = 2021,
            language = "Hindi",
            artistResponses = listOf(ArtistResponse(artistId, "Artist Name")),
            genres = listOf("Genre"),
            duration = "1:30",
            difficulty = "Easy",
            thumbnailURL = "www.thumbnail.url",
            previewURL = "www.preview.url",
            mediaURL = "www.media.url",
            isAvailable = true,
            isLearnModeAvailable = true,
            //usage = listOf(UsageResponse(true))
        )
        val expected = Song(
            id = songId,
            order = 1245,
            title = "Some Title",
            album = "Some Album",
            artists = listOf(Artist(artistId, "Artist Name")),
            genres = listOf("Genre"),
            duration = "1:30",
            difficulty = "Easy",
            language = "Hindi",
            isAvailable = true,
            mediaUrl = "www.media.url",
            previewUrl = "www.preview.url",
            thumbnailUrl = "www.thumbnail.url",
            year = 2021,
            isPremium = true
        )

        // when
        val song = songResponse.toSong()

        // then
        assertEquals(expected, song)
    }

    @Test
    fun `GetSongResponse with full data maps to list of song domain model`() {
        val songResponses = newNSongResponses(10)
        val response = newGetSongResponse(songResponses)

        val expectedSongs: List<Song> = songResponses.map {
            Song(
                id = it.id,
                order = it.order.toInt(),
                title = it.title,
                album = it.album,
                artists = it.artistResponses.map { Artist(it.id, it.name) },
                genres = it.genres,
                duration = it.duration,
                difficulty = it.difficulty,
                language = it.language,
                isAvailable = it.isAvailable,
                mediaUrl = it.mediaURL,
                previewUrl = it.previewURL,
                thumbnailUrl = it.thumbnailURL,
                year = it.year.toInt(),
                isPremium = true/*it.usage[0].premium*/
            )
        }
        val actualSongs = response.data.songMinis.map { it.toSongMini() }

        assertEquals(expectedSongs, actualSongs)
    }

    @Test
    fun `GetSongResponse with empty data maps to empty list of song domain model`() {
        val songResponses = newNSongResponses(0)
        val response = newGetSongResponse(songResponses)

        val expectedSongs: List<Song> = songResponses.map {
            Song(
                id = it.id,
                order = it.order.toInt(),
                title = it.title,
                album = it.album,
                artists = it.artistResponses.map { Artist(it.id, it.name) },
                genres = it.genres,
                duration = it.duration,
                difficulty = it.difficulty,
                language = it.language,
                isAvailable = it.isAvailable,
                mediaUrl = it.mediaURL,
                previewUrl = it.previewURL,
                thumbnailUrl = it.thumbnailURL,
                year = it.year.toInt(),
                isPremium = true/*it.usage[0].premium*/
            )
        }
        val actualSongs = response.data.songMinis.map { it.toSongMini() }

        Assert.assertEquals(0, actualSongs.size)
        Assert.assertEquals(expectedSongs, actualSongs)
    }
}