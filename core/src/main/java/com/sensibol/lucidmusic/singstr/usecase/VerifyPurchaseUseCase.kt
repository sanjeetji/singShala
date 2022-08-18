package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class VerifyPurchaseUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    private val userWebService: UserWebService
) {
    suspend operator fun invoke(receipt: String, signature: String): Boolean = userWebService.verifyPurchase(appDatabase.getAuthToken(), receipt, signature)
}