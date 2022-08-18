package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.webservice.UserWebService
import javax.inject.Inject

class IsForcedUpdateNeededUseCase @Inject constructor(
    private val userWebService: UserWebService,
) {
    suspend operator fun invoke(appVersionCode: Int): Boolean = userWebService.isAppExpired(appVersionCode)

}