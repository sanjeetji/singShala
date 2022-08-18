package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.LeaderboardUser
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class GetLeaderUserListUseCase @Inject constructor(
    val appDatabase: AppDatabase,
    val userWebService: UserWebService
) {
    suspend operator fun invoke(): List<LeaderboardUser> {
        val authToken = appDatabase.getAuthToken()
        return userWebService.getLeaderBoardUserList(authToken)
    }
}