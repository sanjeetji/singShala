package com.sensibol.lucidmusic.singstr.network.service.user


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class IsAppExpiredResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "data")
    val data: GetAppExpiryData
){
    @JsonClass(generateAdapter = true)
    internal data class GetAppExpiryData(
        @Json(name = "is_expired")
        val isExpired: Boolean
    )
}

