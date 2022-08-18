package com.sensibol.lucidmusic.singstr.domain

import java.io.File

interface AppFileSystem {

    fun getMediaFile(id: String, hash: String): File

    fun getMetadataFile(id: String, hash: String): File

    fun getUniqueMixFile(): File

    fun getVideDownloadFile(): File

    suspend fun getCacheSizeBytes(): Long

    suspend fun deleteCache()

}