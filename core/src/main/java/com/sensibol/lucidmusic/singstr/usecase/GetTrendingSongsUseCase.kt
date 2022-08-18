package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.Genre
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.domain.webservice.SingWebService
import javax.inject.Inject

class GetTrendingSongsUseCase @Inject constructor(
    private val songWebService: SingWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(genre: Genre? = null): List<SongMini> = songWebService.getTrendingSongs(appDatabase.getAuthToken(), genre)
}