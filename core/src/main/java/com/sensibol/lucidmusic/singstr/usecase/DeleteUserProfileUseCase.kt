package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.DeleteUser
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class DeleteUserProfileUseCase @Inject constructor(
    private val userWebService: NodeJSUserWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(): String {
        val authToken = appDatabase.getAuthToken()
        return userWebService.deleteUserProfile(authToken)
    }
}