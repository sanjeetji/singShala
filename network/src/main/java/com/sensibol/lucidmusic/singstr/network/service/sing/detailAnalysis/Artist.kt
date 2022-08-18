package com.sensibol.lucidmusic.singstr.network.service.sing.detailAnalysis


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Artist(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String
)