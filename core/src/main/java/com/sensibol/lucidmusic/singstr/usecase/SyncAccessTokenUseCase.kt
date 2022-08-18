package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import timber.log.Timber
import javax.inject.Inject

class SyncAccessTokenUseCase @Inject constructor(
    private val userWebService: UserWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(provider: String, token: String) {
        Timber.d("invoke: provider=$provider, toke=$token")
        val authToken = userWebService.generateAccessToken(provider, token)
        Timber.d("invoke: authToken=$authToken")
        appDatabase.setAuthToken(authToken)
    }

}