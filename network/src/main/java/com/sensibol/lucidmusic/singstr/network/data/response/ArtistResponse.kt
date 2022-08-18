package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.Artist
import com.squareup.moshi.JsonClass

internal fun ArtistResponse.toArtist() = Artist(name)


@JsonClass(generateAdapter = true)
data class ArtistResponse(
    val name: String
)