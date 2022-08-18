package com.sensibol.lucidmusic.singstr.network.service.sing.detailAnalysis


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Author(
    @Json(name = "display_name")
    val displayName: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "profile_url")
    val profileUrl: String,
    @Json(name = "user_handle")
    val userHandle: String
)