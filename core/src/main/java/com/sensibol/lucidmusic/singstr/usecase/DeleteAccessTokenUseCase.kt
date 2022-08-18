package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import timber.log.Timber
import javax.inject.Inject

class DeleteAccessTokenUseCase @Inject constructor(
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke() {
        Timber.v("invoke: IN")
        appDatabase.deleteAuthToken()
        Timber.v("invoke: OUT")
    }

}