package com.sensibol.lucidmusic.singstr.domain.model

import java.util.*

data class CourseRequiredSong(
    val count: Int,
    val progression_score: Int,
    val sections_mandatory: Boolean,
    val sections_scorable: Boolean,
    val section_progression_score: Int,
    val song_ids: List<String>,
   // val academyQuestion: AcademyQuestion
)