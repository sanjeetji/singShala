package com.sensibol.lucidmusic.singstr.network.data.response


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class     UserDetailResponse(
    @Json(name = "id")
    val id: String,

    @Json(name = "artist_name")
    val name: String,

    @Json(name = "user_handle")
    val handle: String,

    @Json(name = "display_name")
    val displayName: String,

    @Json(name = "dob")
    val dob: String,

    @Json(name = "email")
    val email: String,

    @Json(name = "first_name")
    val firstName: String,

    @Json(name = "is_email_verified")
    val isEmailVerified: Boolean,

    @Json(name = "last_name")
    val lastName: String,

    @Json(name = "mobile_number")
    val mobileNumber: String,

    @Json(name = "profile_picture")
    val profilePicture: String,

    @Json(name = "sex")
    val gender: String,

    @Json(name = "city")
    val city: String,

    @Json(name = "state")
    val state: String,

    @Json(name = "bio")
    val bio: String,

    @Json(name = "onboard_status")
    val isOnBoarded: Boolean = false
)