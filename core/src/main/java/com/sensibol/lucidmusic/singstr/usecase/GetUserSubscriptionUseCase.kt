package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.ProSubscription
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class GetUserSubscriptionUseCase @Inject constructor(
    private val userWebService: UserWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(): ProSubscription {
        val authToken = appDatabase.getAuthToken()
        return userWebService.getUserSubscription(authToken)
    }
}