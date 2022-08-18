package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.DraftsPage
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import javax.inject.Inject

class GetCoverDraftsUseCase @Inject constructor(
    private val feedWebService: FeedWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(pageToken: String?): DraftsPage {
        val authToken = appDatabase.getAuthToken()
        return feedWebService.getDraftCovers(authToken,pageToken)
    }
}