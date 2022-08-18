package com.sensibol.lucidmusic.singstr.network.service.learn

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class FollowUnfollowTeacherRequestBody(
    val teacher_id: String,
)
