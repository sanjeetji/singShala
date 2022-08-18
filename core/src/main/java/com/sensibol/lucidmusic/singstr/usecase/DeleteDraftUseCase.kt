package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.CoverWebService
import javax.inject.Inject

class DeleteDraftUseCase @Inject constructor(
    private val coverWebService: CoverWebService,
    private val appDatabase: AppDatabase,
) {
    suspend operator fun invoke(attemptId: String) = coverWebService.deleteDraft(appDatabase.getAuthToken(), attemptId)
}