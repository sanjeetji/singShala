package com.sensibol.lucidmusic.singstr.network.service.learn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetTeacherRequestBody(
    val id: String,
)

