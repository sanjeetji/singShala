package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.UserMini
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun UserMiniResponse.toUserMini() = UserMini(
    id = id,
    handle = handle,
    dpUrl = dpUrl,
    displayName = name
)

@JsonClass(generateAdapter = true)
data class UserMiniResponse(
    @Json(name = "id")
    val id: String,

    @Json(name = "display_name")
    val name: String,

    @Json(name = "profile_url")
    val dpUrl: String,

    @Json(name = "user_handle")
    val handle:String
)
