package com.sensibol.lucidmusic.singstr.network.service.sing

import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.Genre
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.domain.webservice.NodeJsSingWebService
import com.sensibol.lucidmusic.singstr.network.data.response.toSongMini
import com.sensibol.lucidmusic.singstr.network.service.networkCall
import com.sensibol.lucidmusic.singstr.network.service.toBearerToken
import java.util.*
import javax.inject.Inject

internal class NodeJsAppSingWebService @Inject constructor(
    private val singService: NodeJsRetrofitSingWebService,
) : NodeJsSingWebService {
    override suspend fun getTrendingSongs(authToken: AuthToken, page: Int?): List<SongMini> = networkCall(
        { singService.getTrendingSongs(authToken.toBearerToken(), page) },
        { response -> response.data.map { it.toSongMini() } }
    )

    override suspend fun updateDraftForStaging(authToken: AuthToken, attemptId: String): String = networkCall(
        {singService.updateDraftForStaging(authToken.toBearerToken(), UpdateDraftForStagingRequestBody(attemptId))},
        {response -> response.data}
    )
}