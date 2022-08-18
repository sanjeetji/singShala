package com.sensibol.lucidmusic.singstr.network.service.file

import com.sensibol.lucidmusic.singstr.domain.webservice.FileDownloadWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.OnTransferProgress
import com.sensibol.lucidmusic.singstr.network.framework.ProgressResponseBody
import com.sensibol.lucidmusic.singstr.network.service.networkRequest
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import okio.buffer
import okio.sink
import java.io.File
import javax.inject.Inject

internal class OkFileDownloadWebService @Inject constructor() : FileDownloadWebService {

    override suspend fun downloadFile(url: String, file: File, onTransferProgress: OnTransferProgress) {
        networkRequest {
            OkHttpClient.Builder().addNetworkInterceptor(Interceptor { chain ->
                val response: Response = chain.proceed(chain.request())
                response.newBuilder().body(response.body?.let {
                    ProgressResponseBody(it) { bytesTransferred, totalBytes -> onTransferProgress(bytesTransferred, totalBytes) }
                }).build()
            }).build()
                .newCall(Request.Builder().url(url).build()).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    response.body?.let {
                        file.sink().buffer().apply {
                            writeAll(it.source())
                            close()
                        }
                    }
                }
        }
    }
}