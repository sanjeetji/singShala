package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import javax.inject.Inject

class GetLearnLessonsWrtTagsUseCase @Inject constructor(
    private val learnWebService: LearnWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(lessonTags: List<String>? = null): List<LessonMini> {
        val authToken = appDatabase.getAuthToken()
        return learnWebService.getLessonsOfTags(authToken,lessonTags)
    }
}