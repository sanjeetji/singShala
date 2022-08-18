package com.sensibol.lucidmusic.singstr.network.service.learn

import com.sensibol.lucidmusic.singstr.network.data.response.GetAllSavedLessonsResponse
import com.sensibol.lucidmusic.singstr.network.service.AUTH_HEADER
import com.sensibol.lucidmusic.singstr.network.service.user.IsSuccessResponse
import retrofit2.http.*

internal interface RetrofitLearnWebService {

    @GET("curriculam/lesson")
    suspend fun getLesson(
        @Header(AUTH_HEADER) token: String,
        @Query("id", encoded = true) query: String?,
    ): GetLessonResponse

    @GET("curriculam/lesson")
    suspend fun getExercise(
        @Header(AUTH_HEADER) token: String,
        @Query("id", encoded = true) query: String?,
    ): GetExerciseResponse

    @GET("academy/trivia/lesson")
    suspend fun getTriviaLesson(
        @Header(AUTH_HEADER) token: String,
        @Query("lesson_id", encoded = true) query: String?,
    ): GetTriviaLessonResponse

    @GET("curriculam/concept/all")
    suspend fun getLearnContent(
        @Header(AUTH_HEADER) token: String
    ): GetLearnContentResponse

    @GET("curriculam/lesson/new")
    suspend fun getNewLesson(
        @Header(AUTH_HEADER) token: String
    ): GetNewLessonsResponse

    @GET("academy/trivia/question")
    suspend fun getAcademyQuestion(
        @Header(AUTH_HEADER) token: String,
        @Query("id", encoded = true) query: String?,
    ): GetAcademyQuestionResponse

    @POST("academy/trivia/question/answer")
    suspend fun submitAnswer(
        @Header(AUTH_HEADER) token: String,
        @Body submitAcademyAnswerRequestBody: SubmitAnswerRequestBody
    ): SubmitAcademyAnswerResponse

    @POST("curriculam/lesson/watched")
    suspend fun sendLessonWatchedDurationMS(
        @Header(AUTH_HEADER) token: String,
        @Body lessonWatchedRequestBody: LessonWatchedRequestBody
    ): IsSuccessResponse

    @GET("teacher")            // need to add the url end point
    suspend fun getTeacherDetails(
        @Header(AUTH_HEADER) token: String,
        @Query("id", encoded = true) query: String?
    ): GetTeacherResponse


    @POST("user/lesson")
    suspend fun addToMyList(
        @Header(AUTH_HEADER) token: String,
        @Body myListRequestBody: MyListRequestBody
    ): IsSuccessResponse


    @GET("user/lesson/all")
    suspend fun getMyList(
        @Header(AUTH_HEADER) token: String,
    ): GetAllSavedLessonsResponse

    @HTTP(method = "DELETE", path = "user/lesson", hasBody = true)
    suspend fun removeFromMyList(
        @Header(AUTH_HEADER) token: String,
        @Body myListRequestBody: MyListRequestBody
    ): IsSuccessResponse

    @GET("curriculam/lesson/tags")
    suspend fun getTags(
        @Header(AUTH_HEADER) token: String
    ): GetLessonTagsResponse

    @GET("curriculam/lesson/filter")
    suspend fun getLessonsOfTags(
        @Header(AUTH_HEADER) token: String,
        @Query("tags", encoded = true) query: List<String>?,
    ): GetNewLessonsResponse

    @GET("curriculam/concept/all/byTune")
    suspend fun getLessonByTune(
        @Header(AUTH_HEADER) token: String
    ): GetTuneLessonResponse

    @GET("curriculam/concept/all/byTime")
    suspend fun getLessonByTime(
        @Header(AUTH_HEADER) token: String
    ): GetTimingLessonResponse

    @POST("teacher/subscribe")
    suspend fun addSubscribeTeacher(
        @Header(AUTH_HEADER) token: String,
        @Body followUnfollowTeacherRequestBody: FollowUnfollowTeacherRequestBody
    ): IsSuccessResponse

    @HTTP(method = "DELETE", path = "teacher/subscribe", hasBody = true)
    suspend fun deleteSubscriberTeacher(
        @Header(AUTH_HEADER) token: String,
        @Body followUnfollowTeacherRequestBody: FollowUnfollowTeacherRequestBody
    ): IsSuccessResponse
}