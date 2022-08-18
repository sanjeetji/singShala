package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import javax.inject.Inject

class SendLessonWatchedDurationUseCase @Inject constructor(
    private val learnWebService: LearnWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(lessonId: String, durationMS: Long): Boolean {
        val authToken = appDatabase.getAuthToken()
        return learnWebService.sendLessonWatchedDurationMS(authToken, lessonId, durationMS)
    }
}