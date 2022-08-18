package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.android.base.md5
import com.sensibol.lucidmusic.singstr.domain.AppFileSystem
import com.sensibol.lucidmusic.singstr.domain.DomainFailure
import com.sensibol.lucidmusic.singstr.domain.model.ContentUrls
import com.sensibol.lucidmusic.singstr.domain.webservice.FileDownloadWebService
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class DownloadVideoUseCase @Inject constructor(
    private val appFileSystem: AppFileSystem,
    private val fileDownloadWebService: FileDownloadWebService,
) {

    data class VideoContentFiles(
        val videoFile: File
    )

    suspend operator fun invoke(videoUrl: String): VideoContentFiles{
        Timber.v("invoke: IN")

        val metadataSizeFraction = 0.01f

        val videoFile = appFileSystem.getVideDownloadFile().apply {
//            if (!isDownloadedFileOkay(this, videoUrl)) {
                Timber.d("invoke: media file not okay")
                fileDownloadWebService.downloadFile(videoUrl, this) { transferred, total ->
                    val f = transferred.toFloat() / total
//                    progress(metadataSizeFraction + ((1.0f - metadataSizeFraction) * f))
//                }
            }
        }

        return VideoContentFiles(videoFile).also {
            Timber.v("invoke: OUT")
        }
    }

    private suspend fun isDownloadedFileOkay(file: File, hash: String): Boolean {
        if (file.exists()) {
            val fileHash = file.md5()
            if (fileHash.equals(hash, ignoreCase = true)) {
                Timber.d("isDownloadedFileOkay: Hash okay: file:${file.absolutePath}")
                return true
            }
            Timber.e("isDownloadedFileOkay: Hash mismatch: file:${file.absolutePath} expected: $hash, actual: $fileHash")
            val fileToBeDeleted = File(file.absolutePath + ".d")
            file.renameTo(fileToBeDeleted)
            fileToBeDeleted.delete()
        } else {
            Timber.d("isDownloadedFileOkay: File does not exists: file:${file.absolutePath}")
        }
        return false
    }
}