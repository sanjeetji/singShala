package com.sensibol.lucidmusic.singstr.network.service.cover

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyCoverRequestBody(
    val token: String
)
