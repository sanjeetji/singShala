package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.SearchData
import com.sensibol.lucidmusic.singstr.domain.model.SimpleAnalysis
import com.sensibol.lucidmusic.singstr.domain.webservice.SingWebService
import javax.inject.Inject

class GetSearchUseCase @Inject constructor(
    private val songWebService: SingWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(keyword: String, lookup: String, page: String): SearchData =
        songWebService.getSearchResults(appDatabase.getAuthToken(), keyword, lookup, page)
}