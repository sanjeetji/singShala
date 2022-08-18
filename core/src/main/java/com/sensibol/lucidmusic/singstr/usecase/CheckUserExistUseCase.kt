package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.CheckUserExists
import com.sensibol.lucidmusic.singstr.domain.model.Singup
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import timber.log.Timber
import javax.inject.Inject

class CheckUserExistUseCase @Inject constructor(
    private val nodeJSUserWebService: NodeJSUserWebService,
) {
    suspend operator fun invoke(checkValue: String): CheckUserExists {
        return nodeJSUserWebService.getUserExist(checkValue)
    }
}