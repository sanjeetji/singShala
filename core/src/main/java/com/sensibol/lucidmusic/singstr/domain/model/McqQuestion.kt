package com.sensibol.lucidmusic.singstr.domain.model


data class McqQuestion(
    val attributes: QuestionAttributes,
    val id: String,
    val options: List<Option>,
) {
    data class QuestionAttributes(
        val text: String
    )

    data class Option(
        val attributes: OptionAttributes,
        val id: String
    ) {
        data class OptionAttributes(
            val text: String
        )
    }
}