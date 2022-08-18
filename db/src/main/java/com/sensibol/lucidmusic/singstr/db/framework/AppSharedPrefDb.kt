package com.sensibol.lucidmusic.singstr.db.framework

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.sensibol.lucidmusic.singstr.domain.DatabaseFailure
import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject

internal class AppSharedPrefDb @Inject constructor(
    private val context: Context
) : AppDatabase {

    private enum class Keys {
        AccessToken,
        IsOnBoardingComplete,
        FCMToken,
        UserId
    }

    companion object {
        const val SHARED_PREF_NAME = "AppSharedPrefDb"
    }

    override suspend fun getAuthToken(): AuthToken =
        try {
            JSONObject(requireNotNull(sharedPref.getString(Keys.AccessToken.name, null))).let {
                AuthToken(
                    accessToken = it.getString("accessToken"),
                    expiryTime = it.getString("expiryTime"),
                    tokenType = it.getString("tokenType"),
                )
            }
        } catch (e: Exception) {
            Timber.e("getAccessToken: error accessing access token")
            throw DatabaseFailure.UserNotLoggedIn()
        }

    override suspend fun setAuthToken(authToken: AuthToken) {
        sharedPref.edit().putString(
            Keys.AccessToken.name,
            JSONObject().apply {
                put("accessToken", authToken.accessToken)
                put("expiryTime", authToken.expiryTime)
                put("tokenType", authToken.tokenType)
            }.toString()

        ).apply()
    }

    override suspend fun deleteAuthToken() {
        sharedPref.edit().remove(
            Keys.AccessToken.name
        ).commit()
    }

    override suspend fun isOnBoardingComplete(): Boolean =
        sharedPref.getBoolean(Keys.IsOnBoardingComplete.name, false)

    override suspend fun setOnBoardingComplete(isOnBoardingComplete: Boolean) {
        sharedPref.edit().putBoolean(Keys.IsOnBoardingComplete.name, isOnBoardingComplete).apply()
    }

    private val sharedPref: SharedPreferences get() = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)

    override suspend fun setFCMToken(token: String?) {
        sharedPref.edit().putString(
            Keys.FCMToken.name,
            token
        ).apply()
    }

    override suspend fun getFCMToken(): String? =
        try {
            requireNotNull(sharedPref.getString(Keys.FCMToken.name, null))
        } catch (e: Exception) {
            Timber.e("getFCMToken: error accessing FCM token")
            throw DatabaseFailure.UserNotLoggedIn()
        }

    override suspend fun getUserId(): String? =
        sharedPref.getString(Keys.UserId.name, null)

    override suspend fun setUserId(userId: String) {
        sharedPref.edit().putString(Keys.UserId.name, userId).apply()
    }
}