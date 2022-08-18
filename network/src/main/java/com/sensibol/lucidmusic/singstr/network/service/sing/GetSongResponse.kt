package com.sensibol.lucidmusic.singstr.network.service.sing


import com.sensibol.lucidmusic.singstr.network.data.response.SongResponse
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class GetSongResponse(
    val data: Data,
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        val song: SongResponse
    )
}