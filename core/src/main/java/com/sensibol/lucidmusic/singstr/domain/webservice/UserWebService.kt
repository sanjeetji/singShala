package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.CarouselBanner
import com.sensibol.lucidmusic.singstr.domain.model.*
import java.io.File


interface UserWebService {

    suspend fun generateAccessToken(provider: String, token: String): AuthToken

    suspend fun getUser(authToken: AuthToken): User

    suspend fun getUserCovers(authToken: AuthToken): List<Attempt>

    suspend fun updateUserDetails(authToken: AuthToken, name: String, lastName: String?, bio: String?, dob: String?, gender: String?, userHandle: String?, displayName: String?, city: String?, state: String?, singerType: String?): Boolean

    suspend fun addUserPreferences(authToken: AuthToken, songSet: List<String>?, language: List<String>?): Boolean

    suspend fun getDailyChallenge(): DailyChallenge

    suspend fun getUserStats(authToken: AuthToken): UserStats

    suspend fun addSubscriber(authToken: AuthToken, subscriberId: String): Boolean

    suspend fun deleteSubscriber(authToken: AuthToken, subscriberId: String): Boolean

    suspend fun getOtherUserProfile(authToken: AuthToken, otherUserId: String): OtherUserProfile

    suspend fun getOtherUserSubmits(authToken: AuthToken, otherUserId: String): Submits

    suspend fun subscribeNotification(authToken: AuthToken, subscriberId: String): Boolean

    suspend fun unsubscribeNotification(authToken: AuthToken, subscriberId: String): Boolean

    suspend fun reportUser(authToken: AuthToken, message: String, reason: List<String>, reportedFor: String): Boolean

    suspend fun reportCover(authToken: AuthToken, message: String, reason: List<String>, reportedFor: String): Boolean

    suspend fun reportLesson(authToken: AuthToken, message: String, reason: List<String>, reportedFor: String): Boolean

    suspend fun reportBug(authToken: AuthToken, message: String): Boolean

    suspend fun getLeaderBoardUserList(authToken: AuthToken): List<LeaderboardUser>

    suspend fun getLeaderBoardUserRank(authToken: AuthToken): Int

//    suspend fun getNotifications(authToken: AuthToken): List<Notifications>

    suspend fun getUserSubscription(authToken: AuthToken): ProSubscription

    suspend fun updateUserSubscription(authToken: AuthToken): Boolean

    suspend fun getUserReviewRemaining(authToken: AuthToken, userId: String): UserReviewAccount

    suspend fun isAppExpired(appVersionCode: Int): Boolean

    suspend fun isUserHandleAvailable(authToken: AuthToken, userHandle: String): Boolean

    suspend fun updateUserOnBoardStatus(authToken: AuthToken, status: Boolean): Boolean?

    suspend fun getUserSubscriptionPlans(authToken: AuthToken): List<SubscriptionPlan>

    suspend fun initialisePayment(authToken: AuthToken, productId: String): String

    suspend fun verifyPurchase(authToken: AuthToken, receipt: String, signature: String): Boolean

    suspend fun appWalkThrough(authToken: AuthToken): List<AppWalkThroughSlide>

    suspend fun updateProfilePic(authToken: AuthToken, file: File): Boolean

    suspend fun fetchCorouselList(authToken: AuthToken): List<CarouselBanner>

    suspend fun getAllConceptList(authToken: AuthToken,nextPageToken:String?): AllConceptData

    suspend fun generateAccessPhoneToken(provider: String, token: String,firstName:String): AuthToken

}