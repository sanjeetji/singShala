package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.Attempt
import com.sensibol.lucidmusic.singstr.domain.model.Submits
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import timber.log.Timber
import javax.inject.Inject

class GetOtherUserSubmitsUseCase @Inject constructor(
    private val userWebService: UserWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(otherUserId: String): Submits {
        Timber.v("invoke: IN")
        val authToken = appDatabase.getAuthToken()
        Timber.d("invoke: authToken=$authToken")

        val user = userWebService.getOtherUserSubmits(authToken, otherUserId)
        Timber.d("invoke: user=$user")

        return user.also {
            Timber.v("invoke: OUT")
        }
    }
}