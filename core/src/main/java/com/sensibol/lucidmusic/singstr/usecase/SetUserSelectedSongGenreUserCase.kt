package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class SetUserSelectedSongGenreUserCase @Inject constructor(
    private val userWebService: UserWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(songType: List<String>?, language: List<String>?): Boolean {
        val authToken = appDatabase.getAuthToken()
        return userWebService.addUserPreferences(authToken, songType, language)
    }
}