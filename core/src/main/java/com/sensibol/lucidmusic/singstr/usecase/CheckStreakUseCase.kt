package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.CheckStreak
import com.sensibol.lucidmusic.singstr.domain.webservice.StreakWebService
import javax.inject.Inject

class CheckStreakUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    private val streakWebService: StreakWebService
) {
    suspend operator fun invoke() : CheckStreak {
       return streakWebService.checkLoginStreak(appDatabase.getAuthToken())
    }
}