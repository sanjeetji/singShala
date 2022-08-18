package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import timber.log.Timber
import javax.inject.Inject

class SyncFCMTokenUseCase @Inject constructor(
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(token: String?) {
        Timber.d("invoke: FCMToken=$token")
        appDatabase.setFCMToken(token)
    }

}