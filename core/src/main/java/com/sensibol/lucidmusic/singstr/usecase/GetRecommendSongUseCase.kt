package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.domain.webservice.SingWebService
import javax.inject.Inject

class GetRecommendSongUseCase @Inject constructor(
    private val singWebService: SingWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(): List<SongMini> {
        val authToken = appDatabase.getAuthToken()
        return singWebService.getRecommendSongs(authToken)
    }
}