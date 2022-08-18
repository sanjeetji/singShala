package com.sensibol.lucidmusic.singstr.network.service.curriculum

import com.sensibol.lucidmusic.singstr.network.service.AUTH_HEADER
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

internal interface RetrofitCurriculumWebService {

    @POST("curriculam/excercise")
    suspend fun submitExerciseScore(
        @Header(AUTH_HEADER) token: String,
        @Body submitScoreRequestBody: SubmitExerciseScoreRequestBody
    ): SubmitExerciseScoreResponse
}