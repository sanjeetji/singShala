package com.sensibol.lucidmusic.singstr.network.service.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignupUserRequestBody (
    @Json(name = "login_type")
    val loginType: Int,
    @Json(name = "social_id")
    val socialId: String,
    @Json(name = "first_name")
    val firstName: String,
    @Json(name = "last_name")
    val lastName: String,
    @Json(name = "user_handle")
    val userHandle: String,
    @Json(name = "sex")
    val sex: String,
    @Json(name = "contact_number")
    val contactNumber: String,
    @Json(name = "profile_img")
    val profileImg: String,
    @Json(name = "dob")
    val dob: String,
    @Json(name = "display_name")
    val displayName: String,
    @Json(name = "singerType")
    val singerType: String,
)