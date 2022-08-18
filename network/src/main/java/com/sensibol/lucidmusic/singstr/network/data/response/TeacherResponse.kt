package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.Teacher
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun TeacherResponse.toTeacher() = Teacher(
    id = id,
    name = name,
    profileImgUrl = profileImgUrl
)

@JsonClass(generateAdapter = true)
data class TeacherResponse(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "profile_img_url")
    val profileImgUrl: String,
)