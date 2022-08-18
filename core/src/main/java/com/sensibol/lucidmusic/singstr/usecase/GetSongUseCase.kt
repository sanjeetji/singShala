package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.webservice.SingWebService
import javax.inject.Inject


class GetSongUseCase @Inject constructor(
    private val singWebService: SingWebService
) {
    suspend operator fun invoke(songId: String) =
        singWebService.getSong(songId)
}