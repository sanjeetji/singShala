package com.sensibol.lucidmusic.singstr.network.service.sing

import com.sensibol.lucidmusic.singstr.network.service.AUTH_HEADER
import retrofit2.http.*

internal interface NodeJsRetrofitSingWebService {

    @GET("sing/song/trending")
    suspend fun getTrendingSongs(
        @Header(AUTH_HEADER) token: String,
        @Query("page", encoded = true) genre: Int?
    ): GetTrendingSongResponse

    @POST("watch/update-media-url-for-staging-only")
    suspend fun updateDraftForStaging(
        @Header(AUTH_HEADER) token: String,
        @Body updateDraftForStagingRequestBody: UpdateDraftForStagingRequestBody
    ): UpdateDraftForStagingResponse
}