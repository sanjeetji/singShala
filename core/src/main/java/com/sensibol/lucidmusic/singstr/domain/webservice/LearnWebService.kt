package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.*

interface LearnWebService {

    suspend fun getLesson(authToken: AuthToken, lessonId: String): Lesson

    suspend fun getTriviaLesson(authToken: AuthToken, lessonId: String): LessonMini

    suspend fun getNewLesson(authToken: AuthToken): List<LessonMini>

    suspend fun sendLessonWatchedDurationMS(authToken: AuthToken, lessonId: String, durationMS: Long): Boolean

    suspend fun getLearnContent(authToken: AuthToken): AcademyContent

    suspend fun getAcademyQuestion(authToken: AuthToken, previousId: String?): McqQuestion

    suspend fun getSubmitAnswer(authToken: AuthToken, questionId: String, answerId: String): AnswerSubmitData

    suspend fun getTeacherDetails(authToken: AuthToken, id: String?): TeacherDetails

    suspend fun addToMyList(authToken: AuthToken, lessonId: String): Boolean

    suspend fun getAllSavedLessonsLesson(authToken: AuthToken): List<MyLessonMini>

    suspend fun removeFromMyList(authToken: AuthToken, lessonId: String): Boolean

    suspend fun getTags(authToken: AuthToken): List<LessonTags>

    suspend fun getLessonsOfTags(authToken: AuthToken,tagTitle:List<String>?): List<LessonMini>

    suspend fun getTuneLessons(authToken: AuthToken) : List<LessonMini>

    suspend fun getTimingLessons(authToken: AuthToken) : List<LessonMini>

    suspend fun addSubscribeTeacher(authToken: AuthToken, teacherId: String): Boolean

    suspend fun deleteSubscriberTeacher(authToken: AuthToken, teacherId: String): Boolean

}