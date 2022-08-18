package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.*

interface NodeJSUserWebService {

    suspend fun claimFreeSubscription(authToken: AuthToken, code: String) : String

    suspend fun getFollowingUser(authToken: AuthToken, userId: String): List<FollowingUser>

    suspend fun getFollowerUser(authToken: AuthToken, userId: String): List<FollowersUser>

    suspend fun deleteUserProfile(authToken: AuthToken):String

    suspend fun getNotifications(authToken: AuthToken,pageToken: String?): NotificationsList

    suspend fun getUserSubmits(authToken: AuthToken, userId: String?): SubmitsWithStatics

    suspend fun getCoverOrDraftScore(authToken: AuthToken, userId: String, type: String): List<NodeDraft>

    suspend fun getDownloadVideoUrl(attemptId: String) : DownloadVideoUrl

    suspend fun singupUser(loginType: Int, socialId: String, firstName: String, lastName: String, userHandle: String, sex: String
                           , contactNumber: String, profileImg: String, dob: String
                           , displayName: String, singerType: String): Singup

    suspend fun getUserExist(checkValue:String): CheckUserExists

    suspend fun getUserHandle(firstName:String,lastName:String): UserHandle

    suspend fun checkTeacher(authToken: AuthToken, userId: String): CheckTeacher
}