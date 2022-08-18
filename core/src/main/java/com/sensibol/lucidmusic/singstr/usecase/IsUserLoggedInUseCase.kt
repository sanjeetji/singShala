package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import timber.log.Timber
import javax.inject.Inject

class IsUserLoggedInUseCase @Inject constructor(
    private var appDatabase: AppDatabase,
) {

    suspend operator fun invoke(): Boolean {
        try {
            val authToken = appDatabase.getAuthToken()
            Timber.d("IsUserLoggedInUseCase $authToken")

            // TODO - refresh token if it is about to expire

        } catch (e: Exception) {
            return false
        }
        return true
    }
}