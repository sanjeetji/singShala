package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.domain.model.TriviaLesson
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import javax.inject.Inject

class GetTriviaLessonUseCase @Inject constructor(
    private val learnWebService: LearnWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(lessonId: String): LessonMini {
        val authToken = appDatabase.getAuthToken()
        return learnWebService.getTriviaLesson(authToken, lessonId)
    }
}