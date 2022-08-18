package com.sensibol.lucidmusic.singstr.network.service.learn


import com.sensibol.lucidmusic.singstr.domain.model.AnswerSubmitData
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun SubmitAcademyAnswerResponse.AcademyAnswerData.toAcademyAnswerData() = AnswerSubmitData(
    answer = answer.toAnswer()
)

internal fun SubmitAcademyAnswerResponse.AcademyAnswerData.Answer.toAnswer() = AnswerSubmitData.Answer(
    isAnswerCorrect = isAnswerCorrect,
    lessonId = lessonId,
    xp = xp
)


@JsonClass(generateAdapter = true)
data class SubmitAcademyAnswerResponse(
    @Json(name = "data")
    val `data`: AcademyAnswerData,
    @Json(name = "success")
    val success: Boolean
) {

    @JsonClass(generateAdapter = true)
    data class AcademyAnswerData(
        @Json(name = "answer")
        val answer: Answer
    ) {
        @JsonClass(generateAdapter = true)
        data class Answer(
            @Json(name = "is_answer_correct")
            val isAnswerCorrect: Boolean,
            @Json(name = "lesson_id")
            val lessonId: String,
            @Json(name = "xp")
            val xp: String
        )
    }
}