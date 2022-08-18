package com.sensibol.lucidmusic.singstr.domain.webservice

import java.io.File

typealias OnTransferProgress = (bytesTransferred: Long, totalBytes: Long) -> Unit

interface FileDownloadWebService {

    suspend fun downloadFile(url: String, file: File, onTransferProgress: OnTransferProgress)

}
