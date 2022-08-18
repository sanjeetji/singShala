package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.ExerciseFromId
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSLearnWebService
import javax.inject.Inject

class GetExerciseFromIdUseCase @Inject constructor(
    private val nodeJSLearnWebService: NodeJSLearnWebService,
    private val appDatabase: AppDatabase
) {

    suspend operator fun invoke(exerciseId: String) : ExerciseFromId{
        val authToken = appDatabase.getAuthToken()
        return nodeJSLearnWebService.getExerciseFromId(authToken, exerciseId)
    }
}