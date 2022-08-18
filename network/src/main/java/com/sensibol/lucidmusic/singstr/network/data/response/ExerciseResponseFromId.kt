package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.Exercise
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun ExerciseResponseFromId.toExercise() = Exercise(
    id = id,
    name = name,
    subtitle = subtitle,
    status = if(statusInt[0] == 0) "NoAvail" else "Avail"
)

@JsonClass(generateAdapter = true)
class ExerciseResponseFromId(
    @Json(name = "_id")
    val id: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "order")
    val order: Int,
    @Json(name = "subtitle")
    val subtitle: String,
    @Json(name = "status")
    val statusInt: List<Int>
)