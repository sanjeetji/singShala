package com.sensibol.lucidmusic.singstr.domain.webservice

import java.io.File

interface FileTransferWebService {


    suspend fun uploadFile(url: String, file: File, onTransferProgress: OnTransferProgress)
}