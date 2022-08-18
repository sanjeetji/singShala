package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.domain.webservice.CoverWebService
import timber.log.Timber
import javax.inject.Inject

class UploadCoverUseCase @Inject constructor(
    private val coverWebService: CoverWebService,
    private val appDatabase: AppDatabase
) {
    suspend operator fun invoke(attemptId: String, singScore: SingScore, onProgress: OnProgress) {
        Timber.v("invoke: IN")
        coverWebService.uploadCover(appDatabase.getAuthToken(), attemptId, singScore) { n, d -> onProgress(n.toFloat() / d) }
        Timber.v("invoke: OUT")
    }

}