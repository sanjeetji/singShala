package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import javax.inject.Inject

class GetFeedUseCase @Inject constructor(
    private val feedWebService: FeedWebService,
) {
    suspend operator fun invoke(pageToken: String?): Feed {
        return feedWebService.getFeed(pageToken)
    }
}