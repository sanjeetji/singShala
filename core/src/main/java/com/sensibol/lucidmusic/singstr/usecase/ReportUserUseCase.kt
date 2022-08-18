package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class ReportUserUseCase @Inject constructor(
    private val userWebService: UserWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(message: String, reason: List<String>, reportedFor: String): Boolean {
        val authToken = appDatabase.getAuthToken()
        return userWebService.reportUser(authToken, message, reason, reportedFor)
    }
}