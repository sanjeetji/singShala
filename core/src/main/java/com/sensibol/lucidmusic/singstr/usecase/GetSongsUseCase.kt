package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.model.Genre
import com.sensibol.lucidmusic.singstr.domain.model.Song
import com.sensibol.lucidmusic.singstr.domain.webservice.SingWebService
import javax.inject.Inject


class GetSongsUseCase @Inject constructor(
    private val singWebService: SingWebService
) {
    suspend operator fun invoke(genre: Genre? = null, difficulty: Song.Difficulty? = null, query: String? = null) =
        singWebService.getSongs(genre, difficulty, query)
}