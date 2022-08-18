package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class SetUserOnBoardedUseCase @Inject constructor(
    private val userWebService: UserWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(isUserOnBoarded: Boolean): Boolean? {
        return userWebService.updateUserOnBoardStatus(appDatabase.getAuthToken(), isUserOnBoarded)
    }
}