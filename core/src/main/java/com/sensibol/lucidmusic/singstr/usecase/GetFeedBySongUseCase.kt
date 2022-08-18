package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import javax.inject.Inject

class GetFeedBySongUseCase @Inject constructor(
    private val feedWebService: FeedWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(songId: String, pageToken: String? = null): Feed =
        feedWebService.getFeedBySongId(appDatabase.getAuthToken(), songId, pageToken)
}