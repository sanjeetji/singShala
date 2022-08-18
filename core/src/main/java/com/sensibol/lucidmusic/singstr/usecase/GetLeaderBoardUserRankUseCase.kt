package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class GetLeaderBoardUserRankUseCase @Inject constructor(
    val userWebService: UserWebService,
    val appDatabase: AppDatabase
) {

    suspend operator fun invoke(): Int = userWebService.getLeaderBoardUserRank(appDatabase.getAuthToken())
}