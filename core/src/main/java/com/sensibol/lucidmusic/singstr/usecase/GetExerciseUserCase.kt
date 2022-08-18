package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.ExerciseFromId
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import javax.inject.Inject

//class GetExerciseUserCase @Inject constructor(
//    private val learnWebService: LearnWebService,
//    private val appDatabase: AppDatabase,
//) {
//    suspend operator fun invoke(exerciseId: String): ExerciseFromId{
//        val authToken = appDatabase.getAuthToken()
////        return learnWebService.getExercise(authToken, exerciseId)
//    }
//}