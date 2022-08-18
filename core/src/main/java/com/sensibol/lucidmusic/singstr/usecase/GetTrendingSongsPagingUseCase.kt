package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.SearchData
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJsSingWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.SingWebService
import javax.inject.Inject

class GetTrendingSongsPagingUseCase @Inject constructor(
    private val singWebService: NodeJsSingWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(page: Int): List<SongMini> =
        singWebService.getTrendingSongs(appDatabase.getAuthToken(), page)
}