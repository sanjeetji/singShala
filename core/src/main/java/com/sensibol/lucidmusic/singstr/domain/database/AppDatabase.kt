package com.sensibol.lucidmusic.singstr.domain.database

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken


interface AppDatabase {

    suspend fun getAuthToken(): AuthToken
    suspend fun setAuthToken(authToken: AuthToken)
    suspend fun deleteAuthToken()

    suspend fun isOnBoardingComplete(): Boolean
    suspend fun setOnBoardingComplete(isOnBoardingComplete: Boolean)

    suspend fun getFCMToken(): String?
    suspend fun setFCMToken(token: String?)

    suspend fun getUserId(): String?
    suspend fun setUserId(userId: String)
}