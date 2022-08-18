package com.sensibol.lucidmusic.singstr.domain.model


data class AcademyContent(
    val lessonGroups: List<LessonGroup>,
    val recommendedLesson: LessonMini,
    val lessonTune: LessonTune,
    val lessonTime: LessonTime
) {
    data class LessonGroup(
        val id: String,
        val title: String,
        val displayOrder: Int,
        var lessons: List<LessonMini>
    )
    data class LessonTune(
        val title:String,
        val score:Int,
        val lessons:List<LessonMini>
    )
    data class LessonTime(
        val title:String,
        val score:Int,
        val lessons:List<LessonMini>
    )
}
