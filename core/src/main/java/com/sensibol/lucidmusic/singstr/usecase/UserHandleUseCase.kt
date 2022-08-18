package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.CheckUserExists
import com.sensibol.lucidmusic.singstr.domain.model.Singup
import com.sensibol.lucidmusic.singstr.domain.model.UserHandle
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import javax.inject.Inject

class UserHandleUseCase @Inject constructor(
    private val nodeJSUserWebService: NodeJSUserWebService
) {
    suspend operator fun invoke(firstName:String,lastName:String) : UserHandle{
        return nodeJSUserWebService.getUserHandle(firstName,lastName)
    }
}