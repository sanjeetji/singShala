package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import javax.inject.Inject


class GetFollowerUserUseCase @Inject constructor(
    private val userWebService: NodeJSUserWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(userId: String): List<FollowersUser> {
        val authToken = appDatabase.getAuthToken()
        return userWebService.getFollowerUser(authToken, userId)
    }
}