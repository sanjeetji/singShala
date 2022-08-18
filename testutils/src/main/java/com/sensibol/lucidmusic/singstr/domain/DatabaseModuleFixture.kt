package com.sensibol.lucidmusic.singstr.domain

import android.content.Context
import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.User
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModuleFixture {

    @Provides
    @Singleton
    internal fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        object : AppDatabase {

            private lateinit var authToken: AuthToken

            override suspend fun getAuthToken(): AuthToken = authToken

            override suspend fun setAuthToken(authToken: AuthToken) {
                this.authToken = authToken
            }

            override suspend fun deleteAuthToken() {
                TODO("Not yet implemented")
            }

            override suspend fun isOnBoardingComplete(): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun setOnBoardingComplete(isOnBoardingComplete: Boolean) {
                TODO("Not yet implemented")
            }

            override suspend fun getFCMToken(): String? {
                TODO("Not yet implemented")
            }

            override suspend fun setFCMToken(token: String?) {
                TODO("Not yet implemented")
            }

            override suspend fun getUserId(): String? {
                TODO("Not yet implemented")
            }

            override suspend fun setUserId(token: String) {
                TODO("Not yet implemented")
            }

        }
}