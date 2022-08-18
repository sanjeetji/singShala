package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.CheckStreak
import com.sensibol.lucidmusic.singstr.domain.model.ContestData
import com.sensibol.lucidmusic.singstr.domain.model.StreakInfo
import com.sensibol.lucidmusic.singstr.domain.webservice.ContestWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.StreakWebService
import timber.log.Timber
import javax.inject.Inject

class GetContestInfoByIdUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    private val contestWebService: ContestWebService
)
{
    suspend operator fun invoke() : ContestData {
        Timber.v("invoke: IN")
        val authToken = appDatabase.getAuthToken()
        Timber.d("invoke: authToken=$authToken")
        return contestWebService.getContestInfoById(authToken,"61b1ac4072fe0ed03b471933")
    }
}