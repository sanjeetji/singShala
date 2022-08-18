package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.UserReviewAccount
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class GetUserReviewAccountUseCase @Inject constructor(
    private val userWebService: UserWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(userId: String): UserReviewAccount {
        val authToken = appDatabase.getAuthToken()
        return userWebService.getUserReviewRemaining(authToken, userId)
    }
}