package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.webservice.FeedWebService
import javax.inject.Inject

class GetCoverVideoUrlUseCase @Inject constructor(
    private val feedWebService: FeedWebService
) {
    suspend operator fun invoke(coverId: String): String {
        return feedWebService.getCoverVideoUrl(coverId)
    }
}