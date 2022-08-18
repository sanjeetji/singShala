package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject


class GetDailyChallengeUseCase @Inject constructor(
    private val userWebService: UserWebService
) {
    suspend operator fun invoke() = userWebService.getDailyChallenge()
}