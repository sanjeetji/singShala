package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.AnswerSubmitData
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import javax.inject.Inject

class SubmitAnswerUseCase @Inject constructor(
    private val learnWebService: LearnWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(questionId: String, answerId: String): AnswerSubmitData {
        val authToken = appDatabase.getAuthToken()
        return learnWebService.getSubmitAnswer(authToken, questionId, answerId)
    }
}