package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class GetInAppPurchaseUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    private val userWebService: UserWebService
) {
    suspend operator fun invoke(productId: String) = userWebService.initialisePayment(appDatabase.getAuthToken(), productId)
}