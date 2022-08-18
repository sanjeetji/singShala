package com.sensibol.lucidmusic.singstr.network.service.file

import com.sensibol.lucidmusic.singstr.network.framework.ProgressRequestBody
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

internal interface RetrofitFileTransferWebService {

    @PUT
    suspend fun uploadFile(@Url url: String, @Body progressRequestBody: ProgressRequestBody)
}