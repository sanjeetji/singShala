package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.Exercise
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun ExerciseResponse.toExercise() = Exercise(
    id = id,
    name = name,
    subtitle = subtitle,
    status = status
)

@JsonClass(generateAdapter = true)
internal class ExerciseResponse(
    @Json(name = "id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "order")
    val order: Int,
    @Json(name = "status")
    val status: String,
    @Json(name = "subtitle")
    val subtitle: String,
)