package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.webservice.ContentWebService
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class PrepareCreateCoverArgsUseCase @Inject constructor(
    private val downloadContentUseCase: DownloadContentUseCase,
    private val contentWebService: ContentWebService,
    private val getMixFilePathUseCase: GetMixFilePathUseCase
) {

    data class CreateCoverArgs(
        val metadataFile: File,
        val mediaFile: File,
        val mixFile: File
    )

    suspend operator fun invoke(songId: String, progress: OnProgress): CreateCoverArgs {
        Timber.v("invoke: IN")

        val contentUrls = contentWebService.getSongContentUrls(songId)
        Timber.d("invoke: contentUrls=$contentUrls")

        val contentPaths = downloadContentUseCase(songId, contentUrls, progress)
        Timber.d("invoke: contentPaths=$contentPaths")

        return CreateCoverArgs(
            metadataFile = contentPaths.metadataFile,
            mediaFile = contentPaths.mediaFile,
            mixFile = getMixFilePathUseCase()
        ).also {
            Timber.v("invoke: OUT")
        }
    }
}