package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class UpdateUserDetailsUseCase @Inject constructor(
    private val userWebService: UserWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(
        name: String,
        lastName: String? = null,
        bio: String? = null,
        dob: String? = null,
        gender: String? = null,
        userHandle: String? = null,
        displayName: String? = null ,
        city: String? = null ,
        state: String? = null,
        singerType: String? = null,

        ): Boolean {
        val authToken = appDatabase.getAuthToken()
        return userWebService.updateUserDetails(authToken, name, lastName, bio, dob, gender, userHandle, displayName, city, state, singerType)
    }
}