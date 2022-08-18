package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.CheckTeacher
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun CheckTeacherResponse.toCheckTeacher() = CheckTeacher(
    message = message,
    teacherId = data
)

@JsonClass(generateAdapter = true)
internal data class CheckTeacherResponse(
    @Json(name = "message")
    val message: String,
    @Json(name = "data")
    val data: String
)