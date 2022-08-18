package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import javax.inject.Inject

class ClaimFreeSubscriptionUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    private val nodeJSUserWebService: NodeJSUserWebService
) {
    suspend operator fun invoke(code: String) : String{
        return nodeJSUserWebService.claimFreeSubscription(appDatabase.getAuthToken(), code)
    }
}