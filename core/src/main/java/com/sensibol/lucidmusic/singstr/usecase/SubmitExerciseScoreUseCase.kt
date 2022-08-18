package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.ExerciseScore
import com.sensibol.lucidmusic.singstr.domain.webservice.CurriculumWebService
import javax.inject.Inject

class SubmitExerciseScoreUseCase @Inject constructor(
    private val curriculumWebService: CurriculumWebService,
    private val database: AppDatabase
) {
    suspend operator fun invoke(exerciseScore: ExerciseScore) {
        curriculumWebService.submitExerciseScore(database.getAuthToken(), exerciseScore)
    }
}