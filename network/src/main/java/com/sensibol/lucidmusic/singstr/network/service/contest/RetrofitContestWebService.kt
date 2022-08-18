package com.sensibol.lucidmusic.singstr.network.service.contest

import com.sensibol.lucidmusic.singstr.network.data.response.GetAllContestResponse
import com.sensibol.lucidmusic.singstr.network.data.response.GetContestByIdResponse
import com.sensibol.lucidmusic.singstr.network.service.AUTH_HEADER
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

internal interface RetrofitContestWebService {

    @GET("contest")
    suspend fun getContestData(
        @Header(AUTH_HEADER) token: String): GetAllContestResponse

    @GET("contest/{contestId}")
    suspend fun getContestDataById(
        @Header(AUTH_HEADER) token: String, @Path(value = "contestId") contestId:String): GetContestByIdResponse



}