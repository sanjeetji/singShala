package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.LearnWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSLearnWebService
import javax.inject.Inject

class AddAllToMyListUseCase @Inject constructor(
    private val learnWebService: NodeJSLearnWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(lessonIds: List<String>): String {
        val authToken = appDatabase.getAuthToken()
        return learnWebService.addAllToMyList(authToken, lessonIds)
    }
}