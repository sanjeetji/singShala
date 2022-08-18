package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.CheckTeacher
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import javax.inject.Inject

class CheckTeacherUseCase @Inject constructor(
    private val userWebService: NodeJSUserWebService,
    private val appDatabase: AppDatabase,
) {

    suspend operator fun invoke(userId: String): CheckTeacher{
        return userWebService.checkTeacher(appDatabase.getAuthToken(), userId)
    }
}