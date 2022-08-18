package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import javax.inject.Inject

class AddClapCountUseCase @Inject constructor(
    private val feedWebService: FeedWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(attemptId: String, clapCount: Int) {
        val authToken = appDatabase.getAuthToken()
        return feedWebService.addClapCount(authToken, attemptId, clapCount)
    }
}