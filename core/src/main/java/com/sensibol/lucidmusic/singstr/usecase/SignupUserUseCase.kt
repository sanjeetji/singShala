package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.Singup
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import javax.inject.Inject

class SignupUserUseCase @Inject constructor(
    private val nodeJSUserWebService: NodeJSUserWebService
) {
    suspend operator fun invoke(loginType: Int, socialId: String, firstName: String,
                                lastName: String,
                                userHandle: String,
                                sex: String,
                                contactNumber: String,
                                profileImg: String,
                                dob: String,
                                displayName: String,
                                singerType: String) : Singup{
        return nodeJSUserWebService.singupUser(loginType,socialId,firstName,
            lastName,userHandle,sex,contactNumber,profileImg,dob,displayName, singerType)
    }
}