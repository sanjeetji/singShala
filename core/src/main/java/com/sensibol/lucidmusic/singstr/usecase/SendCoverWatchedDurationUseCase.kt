package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import javax.inject.Inject

class SendCoverWatchedDurationUseCase @Inject constructor(
    private val feedWebService: FeedWebService,
    private val appDatabase: AppDatabase,
) {

    suspend operator fun invoke(attemptId: String, watchDurationMS: Long) =
        feedWebService.sendCoverWatchedDurationMS(appDatabase.getAuthToken(), attemptId, watchDurationMS)

}