package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.DeleteUser
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetUserProfileDeleteResponse.toDeleteUser() = DeleteUser(
    status = status,
    message = message,
    data = data
)

@JsonClass(generateAdapter = true)
internal data class GetUserProfileDeleteResponse(
    @Json(name = "status")
    val status: Int,
    @Json(name = "message")
    val message: String,
    @Json(name = "data")
    val data: String
)


