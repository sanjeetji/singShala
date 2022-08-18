package com.sensibol.lucidmusic.singstr.network.service.sing.detailAnalysis


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Expression(
    @Json(name = "meend")
    val meend: Int,
    @Json(name = "ornament")
    val ornament: Int,
    @Json(name = "vibrato")
    val vibrato: Int
)