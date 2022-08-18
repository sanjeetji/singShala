package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.webservice.ContentWebService
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class PrepareSongPracticeArgsUseCase @Inject constructor(
    private val downloadContentUseCase: DownloadContentUseCase,
    private val contentWebService: ContentWebService,
) {

    data class PracticeArgs(
        val metadataFile: File,
        val mediaFile: File,
    )

    suspend operator fun invoke(songId: String, progress: OnProgress): PracticeArgs {
        Timber.v("invoke: IN")

        val contentUrls = contentWebService.getSongPracticeContentUrls(songId)
        Timber.d("invoke: contentUrls=$contentUrls")

        val contentPaths = downloadContentUseCase(songId, contentUrls, progress)
        Timber.d("invoke: contentPaths=$contentPaths")

        return PracticeArgs(
            metadataFile = contentPaths.metadataFile,
            mediaFile = contentPaths.mediaFile,
        ).also {
            Timber.v("invoke: OUT")
        }
    }
}