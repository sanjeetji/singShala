package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.domain.model.LessonTags
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import javax.inject.Inject


class GetLessonTagsUseCase @Inject constructor(
    private val learnWebService: LearnWebService,
    private val appDatabase: AppDatabase,
    ) {
    suspend operator fun invoke(): List<LessonTags> {
        val authToken = appDatabase.getAuthToken()
        return learnWebService.getTags(authToken)
    }
}