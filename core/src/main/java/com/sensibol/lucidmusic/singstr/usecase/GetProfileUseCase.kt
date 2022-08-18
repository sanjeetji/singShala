package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.User
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import timber.log.Timber
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(
    private val userWebService: UserWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(): User {
        Timber.v("invoke: IN")
        val authToken = appDatabase.getAuthToken()
        Timber.d("invoke: authToken=$authToken")

        val user = userWebService.getUser(authToken)
        Timber.d("invoke: user=$user")

        return user.also {
            Timber.v("invoke: OUT")
        }
    }
}