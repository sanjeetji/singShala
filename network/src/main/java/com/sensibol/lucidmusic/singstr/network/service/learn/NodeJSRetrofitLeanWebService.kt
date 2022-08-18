package com.sensibol.lucidmusic.singstr.network.service.learn

import com.sensibol.lucidmusic.singstr.network.service.AUTH_HEADER
import com.sensibol.lucidmusic.singstr.network.service.user.AddDeleteSubscriberRequestBody
import com.sensibol.lucidmusic.singstr.network.service.user.IsSuccessResponse
import retrofit2.http.*

internal interface NodeJSRetrofitLeanWebService {

    @GET("contentdelivery/exercise/{exerciseId}")
    suspend fun getExerciseFromId(
        @Header(AUTH_HEADER) token: String,
        @Path("exerciseId", encoded = true) exerciseId: String?
    ): GetExerciseFromIdResponse

    @GET("curriculam/lessonByConcept/{conceptId}")
    suspend fun getLessonListByConceptId(
        @Header(AUTH_HEADER) token: String,
        @Path("conceptId") conceptId: String
    ): GetConceptLessonResponse

    @POST("user/saveLesson")
    suspend fun addAllToMyList(
        @Header(AUTH_HEADER) token: String,
        @Body requestBody: SaveAllToMyListRequestBody
    ): AddAllToMyListResponse

    @GET("teacher/details/{teacherId}")
    suspend fun getTeacherAllDetails(
        @Header(AUTH_HEADER) token: String,
        @Path("teacherId") teacherId: String?
    ): GetTeacherDetailsResponse

}