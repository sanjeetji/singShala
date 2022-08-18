package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.NodeDraft
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJSUserWebService
import javax.inject.Inject

class GetNodeDraftUseCase @Inject constructor(
    private val userWebService: NodeJSUserWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(userId: String): List<NodeDraft> {
        val authToken = appDatabase.getAuthToken()
        return userWebService.getCoverOrDraftScore(authToken, userId, "draft")
    }
}