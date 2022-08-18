package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.MyLessonMini
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import javax.inject.Inject

class GetSavedAllMyLessons @Inject constructor(
    private val learnWebService: LearnWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(): List<MyLessonMini> {
        val authToken = appDatabase.getAuthToken()
        return learnWebService.getAllSavedLessonsLesson(authToken)
    }
}