package com.sensibol.lucidmusic.singstr.network.service.file

import com.sensibol.lucidmusic.singstr.domain.webservice.FileTransferWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.OnTransferProgress
import com.sensibol.lucidmusic.singstr.network.framework.ProgressRequestBody
import com.sensibol.lucidmusic.singstr.network.service.networkRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

internal class AppFileTransferWebService @Inject constructor(
    private val fileTransferService: RetrofitFileTransferWebService,
) : FileTransferWebService {

    override suspend fun uploadFile(url: String, file: File, onTransferProgress: OnTransferProgress) {
        networkRequest {
            fileTransferService.uploadFile(url, ProgressRequestBody(file.asRequestBody("application/octet-stream".toMediaType()), onTransferProgress))
        }
    }
}