package com.sensibol.lucidmusic.singstr.domain.model


data class AnswerSubmitData(
    val answer: Answer
) {
    data class Answer(
        val isAnswerCorrect: Boolean,
        val lessonId: String,
        val xp: String
    )
}