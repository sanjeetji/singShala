package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.SubmitsWithStatics
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import javax.inject.Inject

class GetUserSubmitsWithStaticsUseCase @Inject constructor(
    private val nodeJSUserWebService: NodeJSUserWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(userId: String?) : SubmitsWithStatics{
       return nodeJSUserWebService.getUserSubmits(appDatabase.getAuthToken(), userId)
    }
}