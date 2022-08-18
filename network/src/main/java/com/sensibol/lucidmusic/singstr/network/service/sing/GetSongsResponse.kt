package com.sensibol.lucidmusic.singstr.network.service.sing

import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.network.data.response.SongMiniResponse
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class GetSongsResponse(
    val success: Boolean,
    val data: Data
) {
    @JsonClass(generateAdapter = true)
    internal data class Data(
        val songs: List<SongMiniResponse>
    )

}
