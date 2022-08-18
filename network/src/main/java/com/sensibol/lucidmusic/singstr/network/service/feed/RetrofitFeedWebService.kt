package com.sensibol.lucidmusic.singstr.network.service.feed

import com.sensibol.lucidmusic.singstr.network.service.AUTH_HEADER
import com.sensibol.lucidmusic.singstr.network.service.user.IsSuccessResponse
import retrofit2.http.*

internal interface RetrofitFeedWebService {

    @GET("watch/feed")
    suspend fun getFeed(
        @Query("page_token", encoded = true) pageToken: String?,
    ): GetFeedsResponse

    @GET("watch/feedbysong")
    suspend fun getFeedBySong(
        @Header(AUTH_HEADER) token: String,
        @Query("song_id") songId: String,
        @Query("page_token") pageToken: String?
    ): GetFeedsResponse

    @GET("watch/cover/id")
    suspend fun getFeedByCover(
        @Header(AUTH_HEADER) token: String,
        @Query("attempt_id") coverId: String,
    ): GetFeedsResponse

    @GET("watch/feedbyuser")
    suspend fun getFeedByUser(
        @Header(AUTH_HEADER) token: String?,
        @Query("user_id", encoded = true) userId: String,
        @Query("page_token") pageToken: String?
    ): GetFeedsResponse

    @GET("cover/getvideourl")
    suspend fun getCoverVideoUrl(
        @Query("attempt_id", encoded = true) attemptId: String,
    ): GetCoverVideoUrlResponse

    @PUT("watch/clap")
    suspend fun addClap(
        @Header(AUTH_HEADER) token: String,
        @Body clapCountRequest: ClapCountRequest
    ): ClapCountResponse

    @GET("watch/comment")
    suspend fun getCoverComments(
        @Header(AUTH_HEADER) token: String,
        @Query("attempt_id", encoded = true) attemptId: String,
    ): GetCoverCommentsResponse

    @POST("watch/comment")
    suspend fun addFeedComment(
        @Header(AUTH_HEADER) token: String,
        @Body addFeedCommentRequest: AddFeedCommentRequest
    ): AddFeedCommentResponse

    @POST("watch/coverwatched")
    suspend fun sendCoverWatchedDurationMS(
        @Header(AUTH_HEADER) token: String,
        @Body coverWatchedRequest: CoverWatchedRequest
    )

    @HTTP(method = "DELETE", path = "watch/deletecover", hasBody = true)
    suspend fun deleteCover(
        @Header(AUTH_HEADER) token: String,
        @Body deleteCoverRequestBody: DeleteCoverRequestBody
    ): IsSuccessResponse

    @GET("watch/draft")
    suspend fun getDraftCovers(
        @Header(AUTH_HEADER) token: String,
        @Query("next_page_token") nextPageToken: String?,
    ): GetCoverDraftsResponse
}