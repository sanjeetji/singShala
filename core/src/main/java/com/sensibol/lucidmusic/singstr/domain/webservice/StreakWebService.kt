package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.CheckStreak
import com.sensibol.lucidmusic.singstr.domain.model.StreakInfo

interface StreakWebService {

    suspend fun checkLoginStreak(authToken: AuthToken) : CheckStreak

    suspend fun getStreakInfo(authToken: AuthToken) : StreakInfo
}