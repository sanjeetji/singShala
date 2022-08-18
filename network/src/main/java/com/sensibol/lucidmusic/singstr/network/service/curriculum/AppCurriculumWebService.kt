package com.sensibol.lucidmusic.singstr.network.service.curriculum

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.ExerciseScore
import com.sensibol.lucidmusic.singstr.domain.webservice.CurriculumWebService
import com.sensibol.lucidmusic.singstr.network.service.networkRequest
import com.sensibol.lucidmusic.singstr.network.service.toBearerToken
import javax.inject.Inject

internal class AppCurriculumWebService @Inject constructor(
    private val curriculumWebService: RetrofitCurriculumWebService,
) : CurriculumWebService {

    override suspend fun submitExerciseScore(authToken: AuthToken, exerciseScore: ExerciseScore): Unit = networkRequest {
        curriculumWebService.submitExerciseScore(authToken.toBearerToken(), exerciseScore.toSubmitExerciseScoreRequestBody())
    }

}