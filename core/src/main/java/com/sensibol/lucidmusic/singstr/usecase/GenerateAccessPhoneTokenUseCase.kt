package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.CheckUserExists
import com.sensibol.lucidmusic.singstr.domain.model.Singup
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import timber.log.Timber
import javax.inject.Inject

class GenerateAccessPhoneTokenUseCase @Inject constructor(
    private val userWebService: UserWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(provider:String,token:String,firstName: String) :AuthToken{
        Timber.e("check value for phone token: provider=$provider, toke=$token,firstName=$firstName")
        val authToken = userWebService.generateAccessPhoneToken(provider, token, firstName)
        Timber.e("check phone token: authToken=$authToken")
//        appDatabase.setAuthToken(authToken)

        return  authToken

//        val authToken = userWebService.generateAccessToken(provider, token)
//        val authToken = AuthToken(firbaseToken, "Bearer", "2023-02-06T12:47:47Z")
//        appDatabase.setAuthToken(authToken)
    }

}