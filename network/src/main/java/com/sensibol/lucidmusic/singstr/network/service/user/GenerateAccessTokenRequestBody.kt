package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GenerateAccessTokenRequestBody(
    val provider: String,
    val token: String
)