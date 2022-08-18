package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import javax.inject.Inject

class GetFeedByUserUseCase @Inject constructor(
    private val feedWebService: FeedWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(userId: String, pageToken: String? = null): Feed {
        return feedWebService.getFeedByUser(appDatabase.getAuthToken(), userId, pageToken)
    }
}