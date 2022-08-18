package com.sensibol.lucidmusic.singstr.domain.model


data class ConceptInfo(
    val id: String,
    val displayName: String,
    val name: String,
    val order: Int,
    val lessonList: List<LessonMini>
)
