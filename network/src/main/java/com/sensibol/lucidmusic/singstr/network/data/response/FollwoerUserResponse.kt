package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.FollowersUser
import com.sensibol.lucidmusic.singstr.domain.model.FollowingUser
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun FollwoerUserResponse.toFollowerUser() = FollowersUser(
    _id = _id,
    email = email,
    first_name = first_name,
    last_name = last_name,
    user_handle = user_handle,
    image = image,
    subscribe = subscribe
)

@JsonClass(generateAdapter = true)
class FollwoerUserResponse(
    @Json(name = "_id")
    val _id: String,

    @Json(name = "email")
    val email: String,

    @Json(name = "first_name")
    val first_name: String,

    @Json(name = "last_name")
    val last_name: String,

    @Json(name = "user_handle")
    val user_handle: String,

    @Json(name = "image")
    val image: String,

    @Json(name = "subscribe")
    val subscribe: Boolean
)

