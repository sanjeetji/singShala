package com.sensibol.lucidmusic.singstr.network.service.sing

import com.sensibol.lucidmusic.singstr.network.data.response.SongMiniResponse
import com.sensibol.lucidmusic.singstr.network.data.response.TrendingSongMiniResponse
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GetTrendingSongResponse(
    val message: String,
    val data: List<TrendingSongMiniResponse>
)
