package com.sensibol.lucidmusic.singstr.gui.app.learn.academy

import com.sensibol.lucidmusic.singstr.domain.model.AcademyContent
import com.sensibol.lucidmusic.singstr.domain.model.AnswerSubmitData
import com.sensibol.lucidmusic.singstr.domain.model.Lesson
import com.sensibol.lucidmusic.singstr.domain.model.McqQuestion

data class AcademyView(
    val concepts: AcademyContent.LessonGroup?,
    val academyQuestion: McqQuestion?,
    val answer: AnswerSubmitData.Answer?,
    val lesson: Lesson?,
    val viewType: String
) {
    data class Concept(
        val displayName: String,
        val id: String,
        val lesson: List<Lesson>,
        val name: String,
        val order: Int,
        val question: Question
    ) {
        data class Lesson(
            val conceptId: String,
            val conceptOrder: Int,
            val difficulty: String,
            val displayName: String,
            val id: String,
            val name: String,
            val order: Int
        )
    }

    data class Question(
        val attributes: QuestionAttributes,
        val id: String,
        val lessonId: String,
        val options: List<Option>,
        val schoolId: String
    ) {
        data class QuestionAttributes(
            val imageUrl: String,
            val musicNotationFileUrl: String,
            val optionType: String,
            val questionType: String,
            val text: String
        )

        data class Option(
            val attributes: OptionAttributes,
            val id: String
        ) {
            data class OptionAttributes(
                val audioUrl: String,
                val imageUrl: String,
                val musicNotationFileUrl: String,
                val text: String
            )
        }
    }
}

enum class AcademyType {
    Question,
    CorrectAnswer,
    InCorrectAnswer,
    Concept
}
