package com.sensibol.lucidmusic.singstr.network.service.contest

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.ContestData
import com.sensibol.lucidmusic.singstr.domain.webservice.ContestWebService
import com.sensibol.lucidmusic.singstr.network.data.response.ContestDataResponse
import com.sensibol.lucidmusic.singstr.network.data.response.toContestData
import com.sensibol.lucidmusic.singstr.network.data.response.toContestDataById
import com.sensibol.lucidmusic.singstr.network.service.networkCall
import com.sensibol.lucidmusic.singstr.network.service.toBearerToken
import javax.inject.Inject

internal class AppContestWebService @Inject constructor(
    private val contestWebService: RetrofitContestWebService
) : ContestWebService {

    override suspend fun getContestInfo(authToken: AuthToken): List<ContestData> = networkCall({
        contestWebService.getContestData(authToken.toBearerToken())
    }){response -> response.data.map { it.toContestData() } }

    override suspend fun getContestInfoById(authToken: AuthToken, contestId: String): ContestData = networkCall({
        contestWebService.getContestDataById(authToken.toBearerToken(),contestId)
    }){response -> response.data.toContestDataById() }



}