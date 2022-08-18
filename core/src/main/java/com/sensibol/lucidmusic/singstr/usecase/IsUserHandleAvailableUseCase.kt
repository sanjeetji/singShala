package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class IsUserHandleAvailableUseCase @Inject constructor(
    private val userWebService: UserWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(userHandle: String): Boolean {
        return userWebService.isUserHandleAvailable(appDatabase.getAuthToken(), userHandle)
    }
}