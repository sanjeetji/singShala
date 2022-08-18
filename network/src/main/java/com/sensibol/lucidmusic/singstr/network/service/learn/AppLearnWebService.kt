package com.sensibol.lucidmusic.singstr.network.service.learn

import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import com.sensibol.lucidmusic.singstr.network.data.response.toLearnContent
import com.sensibol.lucidmusic.singstr.network.data.response.toLesson
import com.sensibol.lucidmusic.singstr.network.data.response.toLessonMini
import com.sensibol.lucidmusic.singstr.network.service.networkCall
import com.sensibol.lucidmusic.singstr.network.service.sing.toGenre
import com.sensibol.lucidmusic.singstr.network.service.toBearerToken
import javax.inject.Inject

internal class AppLearnWebService @Inject constructor(
    private val learnService: RetrofitLearnWebService
) : LearnWebService {

    override suspend fun getLesson(authToken: AuthToken, lessonId: String): Lesson = networkCall(
        { learnService.getLesson(authToken.toBearerToken(), lessonId) },
        { response -> response.data.lesson.toLesson() }
    )

    override suspend fun getTriviaLesson(authToken: AuthToken, lessonId: String): LessonMini = networkCall(
        { learnService.getTriviaLesson(authToken.toBearerToken(), lessonId) },
        { response -> response.data.lesson.toLessonMini() }
    )

    override suspend fun getNewLesson(authToken: AuthToken): List<LessonMini> = networkCall(
        { learnService.getNewLesson(authToken.toBearerToken()) },
        { response -> response.data.lesson.map { it.toLessonMini() } }
    )

    override suspend fun sendLessonWatchedDurationMS(authToken: AuthToken, lessonId: String, durationMS: Long): Boolean = networkCall(
        { learnService.sendLessonWatchedDurationMS(authToken.toBearerToken(), LessonWatchedRequestBody(lessonId, durationMS)) },
        { response -> response.success }
    )

    override suspend fun getLearnContent(authToken: AuthToken): AcademyContent = networkCall(
        { learnService.getLearnContent(authToken.toBearerToken()) },
        { response -> response.data.toLearnContent() }
    )

    override suspend fun getAcademyQuestion(authToken: AuthToken, previousId: String?): McqQuestion = networkCall(
        { learnService.getAcademyQuestion(authToken.toBearerToken(), previousId) },
        { response -> response.data.question.toQuestion() }
    )

    override suspend fun getSubmitAnswer(authToken: AuthToken, questionId: String, answerId: String): AnswerSubmitData = networkCall(
        { learnService.submitAnswer(authToken.toBearerToken(), SubmitAnswerRequestBody("", questionId, answerId)) },
        { it.data.toAcademyAnswerData() }
    )

    override suspend fun getTeacherDetails(authToken: AuthToken, id: String?): TeacherDetails = networkCall(
        { learnService.getTeacherDetails(authToken.toBearerToken(), id) },
        { response -> response.data.answer.toTeacher() }
    )

    override suspend fun addToMyList(authToken: AuthToken, lessonId: String): Boolean = networkCall(
        { learnService.addToMyList(authToken.toBearerToken(), MyListRequestBody(lessonId))},
        { response -> response.success }
    )

    override suspend fun getAllSavedLessonsLesson(authToken: AuthToken): List<MyLessonMini> = networkCall(
        { learnService.getMyList(authToken.toBearerToken())},
        { response -> response.data.lesson.map { it.toLessonMini() } }
    )

    override suspend fun removeFromMyList(authToken: AuthToken, lessonId: String): Boolean = networkCall(
        { learnService.removeFromMyList(authToken.toBearerToken(), MyListRequestBody(lessonId))},
        { response -> response.success }
    )

    override suspend fun getTags(authToken: AuthToken): List<LessonTags> = networkCall(
        { learnService.getTags(authToken.toBearerToken())},
        { response -> response.data.lessonTags.map { it.toLessonTags() } }
    )

    override suspend fun getLessonsOfTags(authToken: AuthToken, tagTitle: List<String>?): List<LessonMini> =networkCall(
        { learnService.getLessonsOfTags(authToken.toBearerToken(),tagTitle)},
        { response -> response.data.lesson.map { it.toLessonMini()} }
    )

    override suspend fun getTuneLessons(authToken: AuthToken): List<LessonMini> = networkCall(
        { learnService.getLessonByTune(authToken.toBearerToken()) },
        { response -> response.data.getByTuneLessons.lessons.map { it.toLessonMini() } }
    )

    override suspend fun getTimingLessons(authToken: AuthToken): List<LessonMini> = networkCall(
        { learnService.getLessonByTime(authToken.toBearerToken()) },
        { response -> response.data.getByTimingLesson.lessons.map { it.toLessonMini() } }
    )

    override suspend fun addSubscribeTeacher(authToken: AuthToken, teacherId: String): Boolean = networkCall(
        { learnService.addSubscribeTeacher(authToken.toBearerToken(), FollowUnfollowTeacherRequestBody(teacherId)) },
        { response -> response.success }
    )

    override suspend fun deleteSubscriberTeacher(authToken: AuthToken, teacherId: String): Boolean = networkCall(
        {learnService.deleteSubscriberTeacher(authToken.toBearerToken(),FollowUnfollowTeacherRequestBody(teacherId))},
        {response -> response.success }
    )
}