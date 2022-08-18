package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.ContentUrls
import com.sensibol.lucidmusic.singstr.domain.model.ContestData
import dagger.Provides

interface ContestWebService {

    suspend fun getContestInfo(authToken: AuthToken) : List<ContestData>

    suspend fun getContestInfoById(authToken: AuthToken, contestId: String): ContestData

}