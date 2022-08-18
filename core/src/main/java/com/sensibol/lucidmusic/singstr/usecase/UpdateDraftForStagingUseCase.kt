package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJsSingWebService
import javax.inject.Inject

class UpdateDraftForStagingUseCase @Inject constructor(
    private val singWebService: NodeJsSingWebService,
    private val appDatabase: AppDatabase
){
    suspend operator fun invoke(attemptId: String): String{
        return singWebService.updateDraftForStaging(appDatabase.getAuthToken(), attemptId)
    }
}