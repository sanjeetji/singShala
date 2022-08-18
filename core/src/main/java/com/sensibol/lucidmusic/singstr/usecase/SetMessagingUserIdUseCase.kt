package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import timber.log.Timber
import javax.inject.Inject

class SetMessagingUserIdUseCase @Inject constructor(
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(userId: String) : Boolean{
        Timber.d("Messaging UserId: ${appDatabase.getUserId()}")
        return if(appDatabase.getUserId() == null){
            appDatabase.setUserId(userId)
            false
        }else
            true
    }
}