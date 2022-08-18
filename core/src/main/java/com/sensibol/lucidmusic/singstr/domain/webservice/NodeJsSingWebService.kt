package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.Genre
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.domain.model.SongMini

interface NodeJsSingWebService {

    suspend fun getTrendingSongs(authToken: AuthToken, page: Int?): List<SongMini>

    suspend fun updateDraftForStaging(authToken: AuthToken, attemptId: String): String
}