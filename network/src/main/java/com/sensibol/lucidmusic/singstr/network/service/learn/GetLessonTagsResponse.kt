package com.sensibol.lucidmusic.singstr.network.service.learn
import com.sensibol.lucidmusic.singstr.domain.model.Genre
import com.sensibol.lucidmusic.singstr.domain.model.LessonTags
import com.sensibol.lucidmusic.singstr.domain.model.TeacherDetails
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetLessonTagsResponse.Data.LessonTags.toLessonTags() = LessonTags(
    id = id,
    title=title
)
@JsonClass(generateAdapter = true)
internal class GetLessonTagsResponse(
    @Json(name = "success")
    val success: Boolean,
    @Json(name = "data")
    val data: Data,
){
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "lesson_tags")
        val lessonTags: List<LessonTags>
    ){
        @JsonClass(generateAdapter = true)
        data class LessonTags(
            @Json(name = "id")
            val id: String,
            @Json(name = "title")
            val title: String
        )
    }
}