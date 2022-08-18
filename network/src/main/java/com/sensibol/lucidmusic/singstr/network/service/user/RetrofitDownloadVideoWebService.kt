package com.sensibol.lucidmusic.singstr.network.service.user

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

internal interface RetrofitDownloadVideoWebService {

    @GET
    suspend fun downloadVide(
        @Url fileUrl: String
    ): Call<ResponseBody>
}