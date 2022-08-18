package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import com.sensibol.lucidmusic.singstr.network.data.response.toAllConceptData
import com.sensibol.lucidmusic.singstr.network.data.response.toConceptData
import com.sensibol.lucidmusic.singstr.network.service.networkCall
import com.sensibol.lucidmusic.singstr.network.service.toBearerToken
import okhttp3.MultipartBody
import java.io.File
import javax.inject.Inject

internal class AppUserWebService @Inject constructor(
    private val userService: RetrofitUserWebService
) : UserWebService {

    override suspend fun generateAccessToken(provider: String, token: String): AuthToken = networkCall(
        { userService.generateAccessToken(GenerateAccessTokenRequestBody(provider, token)) },
        { it.data.token.toToken() }
    )

    override suspend fun getUser(authToken: AuthToken): User = networkCall(
        { userService.getUserProfile(authToken.toBearerToken()) },
        { it.data.user.toUser() }
    )

    override suspend fun getUserCovers(authToken: AuthToken): List<Attempt> = networkCall(
        { userService.getUserCovers(authToken.toBearerToken()) },
        { response -> response.data.submits.map { it.toCover() } }
    )

    override suspend fun updateUserDetails(
        authToken: AuthToken,
        name: String,
        lastName:String?,
        bio: String?,
        dob: String?,
        gender: String?,
        userHandle: String?,
        displayName: String?,
        city: String?,
        state: String?,
        singerType: String?
    ): Boolean = networkCall(
        {
            userService.updateUserDetails(
                authToken.toBearerToken(),
                UpdateUserDetailsRequestBody(name, lastName, null, null, userHandle, displayName, bio, gender, dob, city, state, singerType)
            )
        },
        { response -> response.success }
    )

    override suspend fun addUserPreferences(authToken: AuthToken, songSet: List<String>?, language: List<String>?): Boolean = networkCall(
        { userService.addUserPreferences(authToken.toBearerToken(), AddUserPreferenceRequestBody(language, songSet)) },
        { response -> response.success }
    )

    override suspend fun getDailyChallenge(): DailyChallenge = networkCall(
        { userService.getDailyChallenge() },
        { response -> response.data.challenge.toDailyChallenges() }
    )

    override suspend fun getUserStats(authToken: AuthToken): UserStats = networkCall(
        { userService.getUserStats(authToken.toBearerToken()) },
        { response -> response.data.stats.toUserStats() }
    )

    override suspend fun getOtherUserProfile(authToken: AuthToken, otherUserId: String): OtherUserProfile = networkCall(
        { userService.getOtherUserProfile(authToken.toBearerToken(), otherUserId) },
        { response -> response.data.data.toOtherUserProfileData() }
    )

    override suspend fun getOtherUserSubmits(authToken: AuthToken, otherUserId: String): Submits = networkCall(
        { userService.getOtherUserSubmits(authToken.toBearerToken(), otherUserId) },
        { response -> response.data.toOtherUserSubmit() }
    )

    override suspend fun subscribeNotification(authToken: AuthToken, subscriberId: String): Boolean = networkCall(
        {
            userService.subscribeNotification(
                authToken.toBearerToken(),
                SubscribeUnSubscribeNotificationRequestBody(subscriberId, "PushNotification")
            )
        },
        { response -> response.success }
    )

    override suspend fun unsubscribeNotification(authToken: AuthToken, subscriberId: String): Boolean = networkCall(
        {
            userService.unsubscribeNotification(
                authToken.toBearerToken(),
                SubscribeUnSubscribeNotificationRequestBody(subscriberId, "PushNotification")
            )
        },
        { response -> response.success }
    )

    override suspend fun addSubscriber(authToken: AuthToken, subscriberId: String): Boolean = networkCall(
        { userService.addSubscriber(authToken.toBearerToken(), AddDeleteSubscriberRequestBody(subscriberId)) },
        { response -> response.success }
    )

    override suspend fun deleteSubscriber(authToken: AuthToken, subscriberId: String): Boolean = networkCall(
        { userService.deleteSubscriber(authToken.toBearerToken(), AddDeleteSubscriberRequestBody(subscriberId)) },
        { response -> response.success }
    )

    override suspend fun reportUser(authToken: AuthToken, message: String, reason: List<String>, reportedFor: String): Boolean = networkCall(
        { userService.reportUser(authToken.toBearerToken(), ReportRequestBody(message, reason, reportedFor)) },
        { response -> response.success }
    )

    override suspend fun reportCover(authToken: AuthToken, message: String, reason: List<String>, reportedFor: String): Boolean = networkCall(
        { userService.reportCover(authToken.toBearerToken(), ReportRequestBody(message, reason, reportedFor)) },
        { response -> response.success }
    )

    override suspend fun reportLesson(authToken: AuthToken, message: String, reason: List<String>, reportedFor: String): Boolean = networkCall(
        { userService.reportLesson(authToken.toBearerToken(), ReportRequestBody(message, reason, reportedFor)) },
        { response -> response.success }
    )

    override suspend fun reportBug(authToken: AuthToken, message: String): Boolean = networkCall(
        { userService.reportBug(authToken.toBearerToken(), ReportBug(message)) },
        { response -> response.success }
    )

    /*override suspend fun getNotifications(authToken: AuthToken): List<Notifications> = networkCall(
        { userService.getNotifications(authToken.toBearerToken()) },
        { response -> response.data.notificationData.map { it.toNotifications() } }
    )*/

    override suspend fun getLeaderBoardUserList(authToken: AuthToken): List<LeaderboardUser> = networkCall(
        { userService.getLeaderBoardUserList(authToken.toBearerToken()) },
        { response -> response.data.userlist.map { it.toLeaderUser() } }
    )

    override suspend fun getLeaderBoardUserRank(authToken: AuthToken): Int = networkCall(
        { userService.getLeaderUserRank(authToken.toBearerToken()) },
        { response -> response.data.rank }
    )

    override suspend fun getUserSubscription(authToken: AuthToken): ProSubscription = networkCall(
        { userService.getUserSubscription(authToken.toBearerToken()) },
        { response -> response.data.subscription.toSubscription() }
    )

    override suspend fun updateUserSubscription(authToken: AuthToken): Boolean = networkCall(
        { userService.updateUserSubscription(authToken.toBearerToken(), UpdateUserSubscriptionRequestBody()) },
        { response -> response.success }
    )

    override suspend fun getUserReviewRemaining(authToken: AuthToken, userId: String): UserReviewAccount = networkCall(
        { userService.getUserReviewRemaining(authToken.toBearerToken(), userId) },
        { response -> response.data.account.toReviewAccount() }
    )

    override suspend fun isAppExpired(appVersionCode: Int): Boolean = networkCall(
        { userService.isAppExpired(appVersionCode) },
        { response -> response.data.isExpired }
    )

    override suspend fun isUserHandleAvailable(authToken: AuthToken, userHandle: String): Boolean = networkCall(
        { userService.isUserHandleAvailable(authToken.toBearerToken(), userHandle) },
        { response -> response.data.isAvailable }
    )

    override suspend fun updateUserOnBoardStatus(authToken: AuthToken, status: Boolean): Boolean? = networkCall(
        { userService.updateUserOnBoardStatus(authToken.toBearerToken(), UpdateUserOnboardStatusRequestBody(status)) },
        { response -> response.data.status }
    )

    override suspend fun getUserSubscriptionPlans(authToken: AuthToken): List<SubscriptionPlan> = networkCall(
        { userService.getSubscriptionPlan(authToken.toBearerToken()) },
        { response -> response.data.products.map { it.toSubscriptionPlan() } }
    )

    override suspend fun initialisePayment(authToken: AuthToken, productId: String): String = networkCall(
        { userService.initiatePayment(authToken.toBearerToken(), InAppPurchaseRequestBody(productId)) },
        { response -> response.data.token }
    )

    override suspend fun verifyPurchase(authToken: AuthToken, receipt: String, signature: String): Boolean = networkCall(
        { userService.inAppPurchaseVerify(authToken.toBearerToken(), InAppPurchaseVerifyRequestBody("Android", receipt, signature)) },
        { response -> response.success }
    )

    override suspend fun appWalkThrough(authToken: AuthToken): List<AppWalkThroughSlide> = networkCall(
        { userService.appWalkThroughImages(authToken.toBearerToken()) },
        { response -> response.data.walkthrough.map { it.toWalkThrough() } }
    )

    override suspend fun updateProfilePic(authToken: AuthToken, file: File): Boolean = networkCall(
        {
            userService.updateProfilePic(
                authToken.toBearerToken(),
                MultipartBody.Part.createFormData("image", file.name, UploadRequestBody(file, "image"))
            )
        },
        { response -> response.success }
    )

    override suspend fun fetchCorouselList(authToken: AuthToken): List<CarouselBanner> = networkCall(
        { userService.fetchCorouselList(authToken.toBearerToken()) },
        { response -> response.data.carousel.map { it.toCorousel() } }
    )

    override suspend fun getAllConceptList(authToken: AuthToken, nextPageToken: String?): AllConceptData = networkCall(
        {userService.getAllConceptList(authToken.toBearerToken(),nextPageToken)},
        {response -> response.toAllConceptData() }
    )

    override suspend fun generateAccessPhoneToken(provider: String, token: String,firstName:String): AuthToken = networkCall(
        { userService.generateAccessPhoneToken(GenerateAccessTokenPhoneRequestBody(provider, token,firstName)) },
        { it.data.token.toToken() }
    )

}