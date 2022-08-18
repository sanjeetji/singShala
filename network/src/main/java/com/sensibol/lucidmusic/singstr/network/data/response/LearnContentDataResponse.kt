package com.sensibol.lucidmusic.singstr.network.data.response

import android.text.TextUtils
import com.sensibol.lucidmusic.singstr.domain.model.AcademyContent
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun LearnContentDataResponse.toLearnContent() = AcademyContent(
    lessonGroups = lessonGroupResponses.map { it.toLessonGroup() },
    recommendedLesson = recommendedLessonResponse.toLessonMini(),
    lessonTime = timeLessonResponse.toLessonMini(),
    lessonTune = tuneLessonResponse.toLessonMini()
)

internal fun LearnContentDataResponse.LessonGroupResponse.toLessonGroup() = AcademyContent.LessonGroup(
    id = id,
    title = displayName,
    displayOrder = order,
    lessons = lessons.map { it.toLessonMini() }
)
internal fun LearnContentDataResponse.LessonTimeResponse.toLessonMini()=AcademyContent.LessonTime(
    title=title,
    score=score.toInt(),
    lessons = lessons.map { it.toLessonMini() }
)
internal fun LearnContentDataResponse.LessonTuneResponse.toLessonMini()=AcademyContent.LessonTune(
    title=title,
    score=score.toInt(),
    lessons = lessons.map { it.toLessonMini() }
)

@JsonClass(generateAdapter = true)
internal class LearnContentDataResponse(
    @Json(name = "concept")
    val lessonGroupResponses: List<LessonGroupResponse>,
    @Json(name = "recommended_lesson")
    val recommendedLessonResponse: LessonMiniResponse,
    @Json(name="by_tune")
    val tuneLessonResponse:LessonTuneResponse,
    @Json(name="by_time")
    val timeLessonResponse:LessonTimeResponse
) {
    @JsonClass(generateAdapter = true)
    internal data class LessonGroupResponse(
        @Json(name = "id")
        val id: String,
        @Json(name = "display_name")
        val displayName: String,
        @Json(name = "order")
        val order: Int,
        @Json(name = "lesson")
        val lessons: List<LessonMiniResponse>,
    )
    @JsonClass(generateAdapter = true)
    internal data class LessonTuneResponse(
        @Json(name = "title")
        val title: String,
        @Json(name = "score")
        val score: Float,
        @Json(name = "Lessons")
        val lessons:List<LessonMiniResponse>
    )
    @JsonClass(generateAdapter = true)
    internal data class LessonTimeResponse(
        @Json(name = "title")
        val title: String,
        @Json(name = "score")
        val score: Float,
        @Json(name = "Lessons")
        val lessons: List<LessonMiniResponse>
    )

}