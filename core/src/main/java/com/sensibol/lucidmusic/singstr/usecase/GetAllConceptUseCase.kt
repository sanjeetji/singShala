package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.AllConceptData
import com.sensibol.lucidmusic.singstr.domain.model.McqQuestion
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class GetAllConceptUseCase @Inject constructor(
    private val userWebService: UserWebService,
    private val appDatabase: AppDatabase,
)
{
    suspend operator fun invoke(nextPageToken: String? = null): AllConceptData =
        userWebService.getAllConceptList(appDatabase.getAuthToken(),nextPageToken)

}