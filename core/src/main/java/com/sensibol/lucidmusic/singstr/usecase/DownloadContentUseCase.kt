package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.android.base.md5
import com.sensibol.lucidmusic.singstr.domain.AppFileSystem
import com.sensibol.lucidmusic.singstr.domain.DomainFailure
import com.sensibol.lucidmusic.singstr.domain.model.ContentUrls
import com.sensibol.lucidmusic.singstr.domain.webservice.FileDownloadWebService
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * Download and save the media and metadata files for a song
 */
class DownloadContentUseCase @Inject constructor(
    private val appFileSystem: AppFileSystem,
    private val fileDownloadWebService: FileDownloadWebService,
) {

    data class ContentFiles(
        val mediaFile: File,
        val metadataFile: File
    )

    suspend operator fun invoke(id: String, contentUrls: ContentUrls, progress: OnProgress): ContentFiles {
        Timber.v("invoke: IN")

        val metadataSizeFraction = 0.01f

        val metadataFile = appFileSystem.getMetadataFile(id, contentUrls.metadataHash).apply {
            if (!isDownloadedFileOkay(this, contentUrls.metadataHash)) {
                Timber.d("invoke: metadata file not okay")
                fileDownloadWebService.downloadFile(contentUrls.metadataUrl, this) { transferred, total ->
                    val f = transferred.toFloat() / total
                    progress(f * metadataSizeFraction)
                }
                if (!isDownloadedFileOkay(this, contentUrls.metadataHash)) {
                    throw DomainFailure.MetadataFileError("Metadata file hash mismatch")
                }
            }
        }

        val mediaFile = appFileSystem.getMediaFile(id, contentUrls.mediaHash).apply {
            if (!isDownloadedFileOkay(this, contentUrls.mediaHash)) {
                Timber.d("invoke: media file not okay")
                fileDownloadWebService.downloadFile(contentUrls.mediaUrl, this) { transferred, total ->
                    val f = transferred.toFloat() / total
                    progress(metadataSizeFraction + ((1.0f - metadataSizeFraction) * f))
                }
                if (!isDownloadedFileOkay(this, contentUrls.mediaHash)) {
                    throw DomainFailure.MediaFileError("Media file hash mismatch")
                }
            }
        }

        return ContentFiles(mediaFile, metadataFile).also {
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
            Timber.e("isDownloadedFileOkay: Hash mismatch: file:${file.absolutePath} expected: $hash, calculated: $fileHash")
            val fileToBeDeleted = File(file.absolutePath + ".d")
            file.renameTo(fileToBeDeleted)
            fileToBeDeleted.delete()
        } else {
            Timber.d("isDownloadedFileOkay: File does not exists: file:${file.absolutePath}")
        }
        return false
    }
}