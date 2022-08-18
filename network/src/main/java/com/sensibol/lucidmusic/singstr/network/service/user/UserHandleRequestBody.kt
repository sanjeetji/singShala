package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserHandleRequestBody (
    @Json(name = "first_name")
    val firstName: String,
    @Json(name = "last_name")
    val lastName:String
)