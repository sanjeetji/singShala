package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.domain.webservice.CoverWebService
import javax.inject.Inject

class SubmitPracticeScoreUseCase @Inject constructor(
    private val coverWebService: CoverWebService,
    private val database: AppDatabase
) {
    suspend operator fun invoke(singScore: SingScore, songMini: SongMini) {
        coverWebService.submitPracticeScore(database.getAuthToken(), singScore, songMini)
    }
}