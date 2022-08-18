package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.McqQuestion
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import javax.inject.Inject

class GetAcademyQuestionUseCase @Inject constructor(
    private val learnWebService: LearnWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(previousId: String? = null): McqQuestion {
        val authToken = appDatabase.getAuthToken()
        return learnWebService.getAcademyQuestion(authToken, previousId)
    }
}