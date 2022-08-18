package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.ExerciseScore

interface CurriculumWebService {

    suspend fun submitExerciseScore(authToken: AuthToken, exerciseScore: ExerciseScore)
}