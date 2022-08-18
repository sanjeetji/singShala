package com.sensibol.lucidmusic.singstr.network.service

import com.sensibol.lucidmusic.singstr.domain.WebServiceFailure
import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.ContestData
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException

internal const val AUTH_HEADER: String = "Authorization"
//internal const val
// Constant For MEssaging
internal suspend fun <Response> networkRequest(
    apiCall: suspend () -> Response
): Response {
    return try {
        withContext(Dispatchers.IO) { apiCall() }
    } catch (e: Exception) {
        Timber.e(e)
        when {
            e is SocketTimeoutException -> throw WebServiceFailure.NetworkTimeOutFailure()
            e is JsonEncodingException || e is JsonDataException -> throw WebServiceFailure.NetworkDataFailure(e.toString())
            e is IOException && null != e.message && e.message!!.contains("NoNetworkFailure") -> throw WebServiceFailure.NoNetworkFailure()
            else -> throw  WebServiceFailure.UnknownNetworkFailure(e.toString())
        }
    }
}

internal suspend fun <Response, Model> networkCall(
    apiCall: suspend () -> Response,
    transform: (Response) -> Model,
): Model {
    return withContext(Dispatchers.Default) { transform(networkRequest(apiCall)) }
}

internal fun AuthToken.toBearerToken() = "Bearer ${this.accessToken}"

