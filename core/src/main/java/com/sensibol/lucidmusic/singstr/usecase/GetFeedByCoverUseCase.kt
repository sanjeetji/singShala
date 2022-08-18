package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import javax.inject.Inject

class GetFeedByCoverUseCase @Inject constructor(
    private val feedWebService: FeedWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(coverId: String): Feed = feedWebService.getFeedByCoverId(appDatabase.getAuthToken(), coverId)
}