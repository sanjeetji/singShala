package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.domain.model.Notifications
import com.sensibol.lucidmusic.singstr.domain.model.NotificationsData
import com.sensibol.lucidmusic.singstr.domain.model.NotificationsList
import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val nodeJSUserWebService: NodeJSUserWebService,
    private val appDatabase: AppDatabase,
    ) {
    suspend operator fun invoke(nextPageToken: String?): NotificationsList {
        val authToken = appDatabase.getAuthToken()
        return nodeJSUserWebService.getNotifications(authToken,nextPageToken)
    }
}