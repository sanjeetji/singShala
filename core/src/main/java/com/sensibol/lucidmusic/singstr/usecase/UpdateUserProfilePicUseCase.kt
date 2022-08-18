package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class UpdateUserProfilePicUseCase @Inject constructor(
    private val userWebService: UserWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(file: File) {
        Timber.v("UpdateUserProfilePicUseCase invoke: IN")
        userWebService.updateProfilePic(appDatabase.getAuthToken(), file)
        Timber.v("UpdateUserProfilePicUseCase invoke: OUT")
    }

}