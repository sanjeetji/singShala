package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.domain.model.FollowingFeed
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJsFeedWebService
import javax.inject.Inject

class GetFollowingFeedUseCase @Inject constructor(
    private val nodeJsFeedWebService: NodeJsFeedWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(pageToken: String?): Feed {
        val authToken = appDatabase.getAuthToken()
        return nodeJsFeedWebService.getFollowingFeed(authToken,pageToken)
    }
}