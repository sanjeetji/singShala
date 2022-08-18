package com.sensibol.lucidmusic.singstr.network.service.learn

import com.sensibol.lucidmusic.singstr.domain.model.ConceptInfo
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.network.data.response.ConceptLessonMiniResponse
import com.sensibol.lucidmusic.singstr.network.data.response.LessonMiniResponse
import com.sensibol.lucidmusic.singstr.network.data.response.toLessonMini
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetConceptLessonResponse.GetConceptLessonData.toConceptInfo() = ConceptInfo(
    id = id,
    displayName = display_name,
    name = name,
    order = order,
    lessonList = lessonsList.map { it.toLessonMini() }
)

@JsonClass(generateAdapter = true)
internal class GetConceptLessonResponse(

    @Json(name = "message")
    val message: String,

    @Json(name = "data")
    val data: List<GetConceptLessonData>
){
    @JsonClass(generateAdapter = true)
    class GetConceptLessonData(

        @Json(name = "_id")
        val id: String,

        @Json(name = "display_name")
        val display_name: String,

        @Json(name = "name")
        val name: String,

        @Json(name = "order")
        val order: Int,

        @Json(name = "lessonsData")
        val lessonsList: List<ConceptLessonMiniResponse>
    )
}