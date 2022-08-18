package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.network.service.AUTH_HEADER
import okhttp3.MultipartBody
import retrofit2.http.*


internal interface NodeJSRetrofitUserWebService {

    @GET("user/oneReupeeSubscription/assign-user/{code}")
    suspend fun claimFreeSubscription(
        @Header(AUTH_HEADER) token: String,
        @Path("code") code: String,
    ) : ClaimFreeSubscriptionResponse

    @GET("user/userSubscribeList")
    suspend fun getFollowingUser(
        @Header(AUTH_HEADER) token: String,
        @Query("userId", encoded = true) userId: String
    ): GetFollowingUserResponse

    @GET("user/userSubscriptionList")
    suspend fun getFollowerUser(
        @Header(AUTH_HEADER) token: String,
        @Query("userId", encoded = true) userId: String
    ): GetFollowerUserResponse

    @DELETE("user/userDelete")
    suspend fun deleteUserProfile(
        @Header(AUTH_HEADER) token: String
    ): GetUserProfileDeleteResponse

    @GET("notifier/list")
    suspend fun getNotifications(
        @Header(AUTH_HEADER) token: String,
        @Query("page_token") pageToken: String?
    ): GetNotificationsResponse

    @GET("cover/submits")
    suspend fun getUserSubmits(
        @Header(AUTH_HEADER) token: String,
        @Query("other_user_id") otherUserId: String?
    ): GetUserSubmitsWithStaticsResponse

    @POST("watch/draft")
    suspend fun getCoverOrDraftScore(
        @Header(AUTH_HEADER) token: String,
        @Body getDraftOrCoverRequestBody: GetDraftOrCoverRequestBody
    ): GetDraftOrCoverResponse


    //    @POST("watch/download-video")
    @POST("user/get-s3-Object")
    suspend fun getDownloadVideoUrl(
        @Body getDownloadVideoUrlBody: GetDownloadVideoUrlBody
    ): GetDownloadVideoResponse

    @POST("user/signUp")
    suspend fun signupUser(
        @Body signupUserRequestBody: SignupUserRequestBody
    ): GetSingupUserResponse


    @POST("user/existence")
    suspend fun checkExistUser(
        @Body checkUserExistRequestBody: CheckUserExistRequestBody
    ): GetUserExistsResponse

    @POST("user/createHandle")
    suspend fun userHandle(
        @Body userHandleRequestBody: UserHandleRequestBody
    ): GetUserHandleResponse

    @GET("user/teacher/check")
    suspend fun checkTeacher(
        @Header(AUTH_HEADER) token: String,
        @Query("user_id") userId: String
    ): CheckTeacherResponse

}

