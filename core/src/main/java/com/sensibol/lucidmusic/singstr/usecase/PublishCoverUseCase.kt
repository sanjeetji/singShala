package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.webservice.CoverWebService
import javax.inject.Inject

class PublishCoverUseCase @Inject constructor(
    private val appDatabase: AppDatabase,
    private val coverWebService: CoverWebService
) {

    suspend operator fun invoke(attemptId: String, caption: String, thumbnailTimeMS: Int) =
        coverWebService.publishCover(appDatabase.getAuthToken(), attemptId, caption, thumbnailTimeMS)

}