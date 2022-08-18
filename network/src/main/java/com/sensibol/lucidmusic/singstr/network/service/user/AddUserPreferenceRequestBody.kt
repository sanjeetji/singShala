package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AddUserPreferenceRequestBody(
    val preferred_language: List<String>?,
    val song_type: List<String>?
)
