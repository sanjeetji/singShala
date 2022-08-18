package com.sensibol.lucidmusic.singstr.network.data.response

import com.sensibol.lucidmusic.singstr.domain.model.ConceptInfo
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.network.service.learn.GetConceptLessonResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun ConceptInfoResponse.toConceptInfo() = ConceptInfo(
    id = id,
    displayName = displayName,
    name = name,
    order = order,
    lessonList = lessonsList.map { it.toLessonMini() }
)

@JsonClass(generateAdapter = true)
class ConceptInfoResponse(
    @Json(name = "id")
    val id: String,

    @Json(name = "display_name")
    val displayName: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "order")
    val order: Int,

    @Json(name = "lesson")
    val lessonsList: List<AllConceptLessonMiniResponse>

)

