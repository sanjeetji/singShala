package com.sensibol.lucidmusic.singstr.network.service.streak

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.CheckStreak
import com.sensibol.lucidmusic.singstr.domain.model.StreakInfo
import com.sensibol.lucidmusic.singstr.domain.webservice.StreakWebService
import com.sensibol.lucidmusic.singstr.network.service.networkCall
import com.sensibol.lucidmusic.singstr.network.service.toBearerToken
import javax.inject.Inject

internal class AppStreakWebService @Inject constructor(
    private val retrofitStreakWebService: RetrofitStreakWebService
) : StreakWebService {

    override suspend fun checkLoginStreak(authToken: AuthToken): CheckStreak = networkCall(
        { retrofitStreakWebService.checkStreak(authToken.toBearerToken()) },
        { it.toCheckStreak() }
    )

    override suspend fun getStreakInfo(authToken: AuthToken): StreakInfo  = networkCall(
        { retrofitStreakWebService.getStreakInfo(authToken.toBearerToken()) },
        { response -> response.data.toStreakInfo()}
    )

}