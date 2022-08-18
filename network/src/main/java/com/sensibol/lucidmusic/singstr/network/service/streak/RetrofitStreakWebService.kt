package com.sensibol.lucidmusic.singstr.network.service.streak

import com.sensibol.lucidmusic.singstr.network.service.AUTH_HEADER
import com.sensibol.lucidmusic.singstr.network.service.curriculum.SubmitExerciseScoreRequestBody
import com.sensibol.lucidmusic.singstr.network.service.curriculum.SubmitExerciseScoreResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

internal interface RetrofitStreakWebService {

    @GET("user/login/streak")
    suspend fun checkStreak(
        @Header(AUTH_HEADER) token: String
    ): CheckStreakResponse

    @GET("user/login/get-streak")
    suspend fun getStreakInfo(
        @Header(AUTH_HEADER) token: String
    ): GetStreakInfoResponse

}