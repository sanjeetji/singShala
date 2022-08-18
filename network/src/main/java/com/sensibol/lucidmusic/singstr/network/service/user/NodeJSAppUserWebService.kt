package com.sensibol.lucidmusic.singstr.network.service.user

import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import com.sensibol.lucidmusic.singstr.network.data.response.toFollowerUser
import com.sensibol.lucidmusic.singstr.network.data.response.toFollowingUser
import com.sensibol.lucidmusic.singstr.network.service.networkCall
import com.sensibol.lucidmusic.singstr.network.service.toBearerToken
import javax.inject.Inject

internal class NodeJSAppUserWebService @Inject constructor(
    private val webservice: NodeJSRetrofitUserWebService
) : NodeJSUserWebService {

    override suspend fun claimFreeSubscription(authToken: AuthToken, code: String): String = networkCall(
        { webservice.claimFreeSubscription(authToken.toBearerToken(), code) },
        { it.data }
    )

    override suspend fun getFollowingUser(authToken: AuthToken, userId: String): List<FollowingUser> = networkCall(
        { webservice.getFollowingUser(authToken.toBearerToken(), userId) },
        { response -> response.data.map { it.toFollowingUser() } }
    )

    override suspend fun getFollowerUser(authToken: AuthToken, userId: String): List<FollowersUser> = networkCall(
        { webservice.getFollowerUser(authToken.toBearerToken(), userId) },
        { response -> response.data.map { it.toFollowerUser() } }
    )

    override suspend fun deleteUserProfile(authToken: AuthToken): String = networkCall(
        { webservice.deleteUserProfile(authToken.toBearerToken()) },
        { response -> response.data }
    )

    override suspend fun getNotifications(authToken: AuthToken, pageToken: String?): NotificationsList = networkCall(
        { webservice.getNotifications(authToken.toBearerToken(), pageToken) },
        { response -> response.data.toNotificationsList() }
    )

    override suspend fun getUserSubmits(authToken: AuthToken, userId: String?): SubmitsWithStatics = networkCall(
        { webservice.getUserSubmits(authToken.toBearerToken(), userId) },
        { response -> response.toOtherUserSubmitWithStatics() }
    )

    override suspend fun getCoverOrDraftScore(authToken: AuthToken, userId: String, type: String): List<NodeDraft> = networkCall(
        { webservice.getCoverOrDraftScore(authToken.toBearerToken(), GetDraftOrCoverRequestBody(userId, type)) },
        { response -> response.data.map { it.toNodeDraft() } }
    )

    override suspend fun getDownloadVideoUrl(attemptId: String): DownloadVideoUrl = networkCall(
        { webservice.getDownloadVideoUrl(GetDownloadVideoUrlBody(attemptId)) },
        { response -> response.toDownloadVideoUrl() }
    )

    override suspend fun singupUser(
        loginType: Int, socialId: String, firstName: String,
        lastName: String,
        userHandle: String,
        sex: String,
        contactNumber: String,
        profileImg: String,
        dob: String,
        displayName: String,
        singerType: String
    ): Singup = networkCall(
        {
            webservice.signupUser(
                SignupUserRequestBody(
                    loginType,
                    socialId,
                    firstName,
                    lastName,
                    userHandle,
                    sex,
                    contactNumber,
                    profileImg,
                    dob,
                    displayName,
                    singerType
                )
            )
        },
        { response -> response.toSingup() }
    )

    override suspend fun getUserExist(checkValue: String): CheckUserExists = networkCall(
        { webservice.checkExistUser(CheckUserExistRequestBody(checkValue)) },
        { response -> response.toCheckUserExists() }
    )

    override suspend fun getUserHandle(firstName: String, lastName: String): UserHandle = networkCall(
        { webservice.userHandle(UserHandleRequestBody(firstName, lastName)) },
        { response -> response.toUserHandle() }
    )

    override suspend fun checkTeacher(authToken: AuthToken, userId: String): CheckTeacher = networkCall(
        { webservice.checkTeacher(authToken.toBearerToken(), userId) },
        { response -> response.toCheckTeacher() }
    )

}