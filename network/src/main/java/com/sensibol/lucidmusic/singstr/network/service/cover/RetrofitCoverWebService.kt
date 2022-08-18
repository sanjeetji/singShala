package com.sensibol.lucidmusic.singstr.network.service.cover

import com.sensibol.lucidmusic.singstr.network.data.response.GetPublishUrlResponse
import com.sensibol.lucidmusic.singstr.network.service.AUTH_HEADER
import retrofit2.http.*

internal interface RetrofitCoverWebService {

    @POST("cover/submitscore")
    suspend fun submitCoverScore(
        @Header(AUTH_HEADER) token: String,
        @Body submitCoverScoreRequestBody: SubmitCoverScoreRequestBody
    ): SubmitCoverScoreResponse

    @POST("curriculam/practice")
    suspend fun submitPracticeScore(
        @Header(AUTH_HEADER) token: String,
        @Body submitScoreRequestBody: SubmitPracticeScoreRequestBody
    ): SubmitPracticeScoreResponse

    @GET("cover/getpublishurl")
    suspend fun getPublishUrl(
        @Header(AUTH_HEADER) token: String,
        @QueryMap(encoded = true) params: Map<String, String>,
    ): GetPublishUrlResponse

    @POST("cover/draft/verify")
    suspend fun verifyCover(
        @Header(AUTH_HEADER) token: String,
        @Body verifyCoverRequestBody: VerifyCoverRequestBody
    ): VerifyCoverResponse

    @POST("cover/draft/publish")
    suspend fun publishCover(
        @Header(AUTH_HEADER) token: String,
        @Body publishCoverRequestBody: PublishCoverRequestBody
    ): PublishCoverResponse

    @POST("watch/addCaption")
    suspend fun setCoverCaption(
        @Header(AUTH_HEADER) token: String,
        @Body setCoverCaptionRequestBody: SetCoverCaptionRequestBody
    ): AddCoverCaptionResponse

    @POST("cover/updatecoverthubmnail")
    suspend fun setCoverThumbnailTimeMS(
        @Header(AUTH_HEADER) token: String,
        @Body setCoverThumbnailTimeMSRequestBody: SetCoverThumbnailTimeMSRequestBody
    ): SetCoverThumbnailTimeMSResponse

    @HTTP(method = "DELETE", path = "cover/draft", hasBody = true)
    suspend fun deleteDraft(
        @Header(AUTH_HEADER) token: String,
        @Body deleteDraftRequestBody: DeleteDraftRequestBody
    ): DeleteDraftResponse
}