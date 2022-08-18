package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.network.data.response.GetAllConceptInfoResponse
import com.sensibol.lucidmusic.singstr.network.service.AUTH_HEADER
import okhttp3.MultipartBody
import retrofit2.http.*


internal interface RetrofitUserWebService {

    @POST("auth/token")
    suspend fun generateAccessToken(
        @Body generateAccessTokenRequestBody: GenerateAccessTokenRequestBody
    ): GenerateAccessTokenResponse

    @GET("user/profile")
    suspend fun getUserProfile(
        @Header(AUTH_HEADER) token: String
    ): GetUserProfileResponse

    @GET("cover/user/submits")
    suspend fun getUserCovers(
        @Header(AUTH_HEADER) token: String
    ): GetUserCoversResponse

    @PUT("user")
    suspend fun updateUserDetails(
        @Header(AUTH_HEADER) token: String,
        @Body updateUserDetailsRequestBody: UpdateUserDetailsRequestBody
    ): UpdateUserDetailsResponse

    @PUT("user/userpref")
    suspend fun addUserPreferences(
        @Header(AUTH_HEADER) token: String,
        @Body addUserPreferenceRequestBody: AddUserPreferenceRequestBody
    ): UpdateUserDetailsResponse

    @GET("user/account/stats")
    suspend fun getUserStats(
        @Header(AUTH_HEADER) token: String
    ): GetUserStatsResponse

    @GET("contest/dailychallenge")
    suspend fun getDailyChallenge(): GetDailyChallengeResponse

    @POST("user/subscribe")
    suspend fun addSubscriber(
        @Header(AUTH_HEADER) token: String,
        @Body addDeleteSubscriberRequestBody: AddDeleteSubscriberRequestBody
    ): IsSuccessResponse

    @HTTP(method = "DELETE", path = "user/subscribe", hasBody = true)
    suspend fun deleteSubscriber(
        @Header(AUTH_HEADER) token: String,
        @Body addDeleteSubscriberRequestBody: AddDeleteSubscriberRequestBody
    ): IsSuccessResponse

    @GET("user/account/profile/other")
    suspend fun getOtherUserProfile(
        @Header(AUTH_HEADER) token: String,
        @Query("otheruser_id", encoded = true) otherUserId: String,
    ): GetOtherUserProfileResponse

    @GET("cover/user/submits/other")
    suspend fun getOtherUserSubmits(
        @Header(AUTH_HEADER) token: String,
        @Query("other_user_id", encoded = true) otherUserId: String,
    ): GetOtherUserSubmitsResponse

    @POST("user/subscription/subscribe")
    suspend fun subscribeNotification(
        @Header(AUTH_HEADER) token: String,
        @Body subscribeUnSubscribeNotificationRequestBody: SubscribeUnSubscribeNotificationRequestBody
    ): IsSuccessResponse

    @POST("user/subscription/unsubscribe")
    suspend fun unsubscribeNotification(
        @Header(AUTH_HEADER) token: String,
        @Body subscribeUnSubscribeNotificationRequestBody: SubscribeUnSubscribeNotificationRequestBody
    ): IsSuccessResponse

    @POST("report/user")
    suspend fun reportUser(
        @Header(AUTH_HEADER) token: String,
        @Body reportRequestBody: ReportRequestBody
    ): IsSuccessResponse

    @POST("report/lesson")
    suspend fun reportLesson(
        @Header(AUTH_HEADER) token: String,
        @Body reportRequestBody: ReportRequestBody
    ): IsSuccessResponse

    @POST("report/cover")
    suspend fun reportCover(
        @Header(AUTH_HEADER) token: String,
        @Body reportRequestBody: ReportRequestBody
    ): IsSuccessResponse

    @POST("report/bug")
    suspend fun reportBug(
        @Header(AUTH_HEADER) token: String,
        @Body reportBug: ReportBug
    ): IsSuccessResponse

    @GET("award/leader/list/user")
    suspend fun getLeaderBoardUserList(
        @Header(AUTH_HEADER) token: String
    ): GetLeaderUserListResponse

    @GET("award/leader/userrank")
    suspend fun getLeaderUserRank(
        @Header(AUTH_HEADER) token: String
    ): GetLeaderBoardUserRankResponse

    @GET("user/account/subscription")
    suspend fun getUserSubscription(
        @Header(AUTH_HEADER) token: String
    ): GetUserSubscriptionResponse

    @POST("user/account")
    suspend fun updateUserSubscription(
        @Header(AUTH_HEADER) token: String,
        @Body updateUserSubscriptionRequestBody: UpdateUserSubscriptionRequestBody
    ): IsSuccessResponse

    /*@GET("notifier/notify/list")
    suspend fun getNotifications(
        @Header(AUTH_HEADER) token: String
    ): GetNotificationsResponse*/

    @GET("user/handlercheck")
    suspend fun isUserHandleAvailable(
        @Header(AUTH_HEADER) token: String,
        @Query("handle", encoded = true) userHandle: String
    ): IsUserHandleAvailableResponse

    @GET("user/account")
    suspend fun getUserReviewRemaining(
        @Header(AUTH_HEADER) token: String,
        @Query("user_id", encoded = true) otherUserId: String,
    ): GetUserReviewRemainingCount

    @GET("appexpiry")
    suspend fun isAppExpired(
        @Query("short_code", encoded = true) appVersionCode: Int,
    ): IsAppExpiredResponse

    @POST("user/account/onboardstatus")
    suspend fun updateUserOnBoardStatus(
        @Header(AUTH_HEADER) token: String,
        @Body updateUserOnboardStatusRequestBody: UpdateUserOnboardStatusRequestBody,
    ): IsUserOnBoardedResponse

    @GET("inapppurchase/product")
    suspend fun getSubscriptionPlan(
        @Header(AUTH_HEADER) token: String
    ): GetSubscriptionPlanResponse

    @POST("inapppurchase")
    suspend fun initiatePayment(
        @Header(AUTH_HEADER) token: String,
        @Body inAppPurchaseRequestBody: InAppPurchaseRequestBody
    ): InitiatePaymentResponse

    @POST("inapppurchase/verify")
    suspend fun inAppPurchaseVerify(
        @Header(AUTH_HEADER) token: String,
        @Body inAppPurchaseVerifyRequestBody: InAppPurchaseVerifyRequestBody
    ): IsSuccessResponse

    @GET("walkthrough/list")
    suspend fun appWalkThroughImages(
        @Header(AUTH_HEADER) token: String
    ): GetWalkThroughSlideResponse

    @Multipart
    @POST("user/update/profilepic")
    suspend fun updateProfilePic(
        @Header(AUTH_HEADER) token: String,
        @Part image: MultipartBody.Part
    ): IsSuccessResponse

    @GET("walkthrough/carousel/listCarousel")
    suspend fun fetchCorouselList(
        @Header(AUTH_HEADER) token: String
    ): ListCorouselResponse

    @GET("curriculam/concept/all/standAlone")
    suspend fun getAllConceptList(
        @Header(AUTH_HEADER) token: String,
        @Query("page_token") nextPageToken: String?
    ): GetAllConceptInfoResponse

    @POST("auth/token")
    suspend fun generateAccessPhoneToken(
        @Body generateAccessTokenPhoneRequestBody: GenerateAccessTokenPhoneRequestBody
    ): GenerateAccessTokenResponse

}

