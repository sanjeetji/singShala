package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class UpdateUserDetailsResponse(
    val success: Boolean,
    val data: Data
) {
    @JsonClass(generateAdapter = true)
    internal data class Data(
        val message: String?
    )
}
