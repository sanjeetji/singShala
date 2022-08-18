package com.sensibol.lucidmusic.singstr.domain.model

import com.sensibol.lucidmusic.singstr.domain.utils.UniqueFixture.newStringId

object ArtistFixture {

    fun newArtist(
        id: String = newStringId(),
        name: String = "Artist Name",
    ): Artist = Artist(name = name)

    fun newArtists(count: Int = 10): List<Artist> = MutableList(count) { newArtist() }
}