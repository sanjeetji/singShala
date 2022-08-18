package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.DetailedAnalysis
import com.sensibol.lucidmusic.singstr.domain.model.SimpleAnalysis
import com.sensibol.lucidmusic.singstr.domain.webservice.SingWebService
import javax.inject.Inject

class GetDetailAnalysisUseCase @Inject constructor(
    private val songWebService: SingWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(attemptId: String): DetailedAnalysis = songWebService.getDetailAnalysis(appDatabase.getAuthToken(), attemptId)
}