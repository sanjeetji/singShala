package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CheckUserExistRequestBody (
    @Json(name = "check_value")
    val checkValue: String
)