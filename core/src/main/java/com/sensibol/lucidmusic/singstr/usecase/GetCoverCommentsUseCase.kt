package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.Comment
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import javax.inject.Inject

class GetCoverCommentsUseCase @Inject constructor(
    private val learnWebService: FeedWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(coverId: String): List<Comment> {
        val authToken = appDatabase.getAuthToken()
        return learnWebService.getCoverComments(authToken, coverId)
    }
}