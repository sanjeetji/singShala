package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.Lesson
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import javax.inject.Inject

class GetLessonByTimingUseCase @Inject constructor(
    private val learnWebService: LearnWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(): List<LessonMini> {
        val authToken = appDatabase.getAuthToken()
        return learnWebService.getTimingLessons(authToken)
    }
}