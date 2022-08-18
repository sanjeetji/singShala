package com.sensibol.lucidmusic.singstr.network.service.user


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
internal data class IsUserOnBoardedResponse(
    val success: Boolean,
    val data: IsUserOnBoarded
) {
    @JsonClass(generateAdapter = true)
    internal data class IsUserOnBoarded(
        @Json(name = "status")
        val status: Boolean?
    )
}