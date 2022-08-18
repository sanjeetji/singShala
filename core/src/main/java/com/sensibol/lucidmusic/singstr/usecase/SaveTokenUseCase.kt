package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.CheckUserExists
import com.sensibol.lucidmusic.singstr.domain.model.Singup
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import javax.inject.Inject

class SaveTokenUseCase @Inject constructor(
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(authToken: AuthToken) {
        appDatabase.setAuthToken(authToken)
    }

}