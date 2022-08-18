package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class GenerateAccessTokenPhoneRequestBody(
    val provider: String,
    val token: String,
    val first_name:String
)