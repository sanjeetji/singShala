package com.sensibol.lucidmusic.singstr.network.service.streak

import com.sensibol.lucidmusic.singstr.domain.model.CheckStreak
import com.sensibol.lucidmusic.singstr.network.service.curriculum.SubmitExerciseScoreResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


internal fun CheckStreakResponse.toCheckStreak() = CheckStreak(
    message = message,
    data = data
)

@JsonClass(generateAdapter = true)
internal data class CheckStreakResponse(

    @Json(name = "message")
    val message: String,

    @Json(name = "data")
    val data: String
)
