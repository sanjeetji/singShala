package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.Comments
import com.sensibol.lucidmusic.singstr.domain.model.McqQuestion
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import javax.inject.Inject

class AddFeedCommentUseCase @Inject constructor(
    private val learnWebService: FeedWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(attemptId: String, comment: String, ): Boolean {
        val authToken = appDatabase.getAuthToken()
        return learnWebService.addFeedComment(authToken, attemptId, comment)
    }
}