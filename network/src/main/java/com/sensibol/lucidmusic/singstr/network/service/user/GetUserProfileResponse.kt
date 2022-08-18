package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.User
import com.sensibol.lucidmusic.singstr.network.data.response.UserDetailResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun UserDetailResponse.toUser() = User(
    id = id,
    name = firstName,
    lastName = lastName,
    handle = handle,
    mobileNumber = mobileNumber,
    email = email,
    isVerified = isEmailVerified,
    dpUrl = profilePicture,
    status = bio,
    dob = dob,
    city = city,
    state = state,
    sex = gender,
    isOnBoarded = isOnBoarded
)

@JsonClass(generateAdapter = true)
internal data class GetUserProfileResponse(
    val success: Boolean,
    val data: Data
) {
    @JsonClass(generateAdapter = true)
    internal data class Data(
        @Json(name = "user")
        val user: UserDetailResponse
    )
}


