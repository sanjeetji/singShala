package com.sensibol.lucidmusic.singstr.network.service.learn


import com.sensibol.lucidmusic.singstr.domain.model.McqQuestion
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

internal fun GetAcademyQuestionResponse.AcademyQuestionData.Question.toQuestion() = McqQuestion(
    attributes = attributes.toQuestionAttributes(),
    id = id,
    options = options.map { it.toOption() },
)

internal fun GetAcademyQuestionResponse.AcademyQuestionData.Question.QuestionAttributes.toQuestionAttributes() =
    McqQuestion.QuestionAttributes(
        text = text
    )


internal fun GetAcademyQuestionResponse.AcademyQuestionData.Question.Option.toOption() = McqQuestion.Option(
    attributes = attributes.toOptionAttributes(),
    id = id
)

internal fun GetAcademyQuestionResponse.AcademyQuestionData.Question.Option.OptionAttributes.toOptionAttributes() =
    McqQuestion.Option.OptionAttributes(
        text = text
    )

@JsonClass(generateAdapter = true)
data class GetAcademyQuestionResponse(
    @Json(name = "data")
    val data: AcademyQuestionData,
    @Json(name = "success")
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class AcademyQuestionData(
        @Json(name = "question")
        val question: Question,
    ) {
        @JsonClass(generateAdapter = true)
        data class Question(
            @Json(name = "attributes")
            val attributes: QuestionAttributes,
            @Json(name = "id")
            val id: String,
            @Json(name = "options")
            val options: List<Option>,
        ) {
            @JsonClass(generateAdapter = true)
            data class QuestionAttributes(
                @Json(name = "text")
                val text: String
            )

            @JsonClass(generateAdapter = true)
            data class Option(
                @Json(name = "attributes")
                val attributes: OptionAttributes,
                @Json(name = "id")
                val id: String
            ) {
                @JsonClass(generateAdapter = true)
                data class OptionAttributes(
                    @Json(name = "text")
                    val text: String
                )
            }
        }
    }
}