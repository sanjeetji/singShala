package com.sensibol.lucidmusic.singstr.network.service.cover

import com.sensibol.android.base.md5
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.domain.webservice.CoverWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.FileTransferWebService
import com.sensibol.lucidmusic.singstr.domain.webservice.OnTransferProgress
import com.sensibol.lucidmusic.singstr.network.service.networkCall
import com.sensibol.lucidmusic.singstr.network.service.networkRequest
import com.sensibol.lucidmusic.singstr.network.service.toBearerToken
import timber.log.Timber
import java.io.File
import javax.inject.Inject

internal class AppCoverWebService @Inject constructor(
    private val coverService: RetrofitCoverWebService,
    private val fileTransferService: FileTransferWebService
) : CoverWebService {

    override suspend fun submitPracticeScore(authToken: AuthToken, singScore: SingScore, songMini: SongMini) = networkRequest {
        coverService.submitPracticeScore(authToken.toBearerToken(), singScore.toSubmitPracticeScoreRequestBody(songMini)).success
    }

    override suspend fun submitCoverScore(authToken: AuthToken, singScore: SingScore, songMini: SongMini): CoverSubmitResult = networkCall(
        { coverService.submitCoverScore(authToken.toBearerToken(), singScore.toSubmitCoverScoreRequestBody(songMini)) },
        { CoverSubmitResult(it.data.attemptId, 0) }
    )

    override suspend fun uploadCover(authToken: AuthToken, attemptId: String, singScore: SingScore, onTransferProgress: OnTransferProgress): Unit =
        networkRequest {
            Timber.d("uploadCover: IN")

            val mixFile = File(singScore.mixPath)
            val metaFile = File(singScore.metaPath)
            val rawRecFile = File(singScore.rawRecPath)

            val mediaContentLength = mixFile.length()
            val metaContentLength = metaFile.length()
            val rawRecordingContentLength = rawRecFile.length()

            val totalBytesToBeTransferred = mediaContentLength + metaContentLength + rawRecordingContentLength
            Timber.d("uploadCover: totalBytesToBeTransferred=$totalBytesToBeTransferred")

            val getPublishUrlRequestParams = GetPublishUrlRequestParams(
                attemptId = attemptId,
                mediaContentLength = mediaContentLength,
                mediaHash = mixFile.md5(),
                metaContentLength = metaContentLength,
                metaHash = metaFile.md5(),
                rawRecordingContentLength = rawRecordingContentLength,
                rawRecordingHash = rawRecFile.md5(),
            )
            val publishUrlResponse = networkRequest { coverService.getPublishUrl(authToken.toBearerToken(), getPublishUrlRequestParams.toQueryMap()) }
            Timber.d("uploadCover: $publishUrlResponse")

            var totalMediaBytesTransferred = 0L
            var totalMetadataBytesTransferred = 0L
            var totalRawBytesTransferred = 0L

            fileTransferService.uploadFile(publishUrlResponse.data.mediaUrl, mixFile) { bytesTransferred, _ ->
                totalMediaBytesTransferred = bytesTransferred
//                    Timber.d("uploadCover: media file bytesTransferred $totalMediaBytesTransferred")
                onTransferProgress(
                    totalMediaBytesTransferred + totalMetadataBytesTransferred + totalRawBytesTransferred,
                    totalBytesToBeTransferred
                )
            }
            Timber.d("uploadCover: media file uploaded!")

            fileTransferService.uploadFile(publishUrlResponse.data.metaUrl, metaFile) { bytesTransferred, _ ->
                totalMetadataBytesTransferred = bytesTransferred
//                    Timber.d("uploadCover: meta file bytesTransferred $totalMetadataBytesTransferred")
                onTransferProgress(
                    totalMediaBytesTransferred + totalMetadataBytesTransferred + totalRawBytesTransferred,
                    totalBytesToBeTransferred
                )
            }
            Timber.d("uploadCover: meta file uploaded!")

            fileTransferService.uploadFile(publishUrlResponse.data.rawRecordingUrl, rawRecFile) { bytesTransferred, _ ->
                totalRawBytesTransferred = bytesTransferred
//                    Timber.d("uploadCover: raw file bytesTransferred $totalRawBytesTransferred")
                onTransferProgress(
                    totalMediaBytesTransferred + totalMetadataBytesTransferred + totalRawBytesTransferred,
                    totalBytesToBeTransferred
                )
            }
            Timber.d("uploadCover: raw recording file uploaded!")

            coverService.verifyCover(authToken.toBearerToken(), VerifyCoverRequestBody(publishUrlResponse.data.token))
        }

    override suspend fun publishCover(authToken: AuthToken, attemptId: String, caption: String, thumbnailTimeMS: Int): Unit = networkRequest {
        coverService.setCoverCaption(authToken.toBearerToken(), SetCoverCaptionRequestBody(attemptId, caption))
        coverService.publishCover(authToken.toBearerToken(), PublishCoverRequestBody(attemptId))
        coverService.setCoverThumbnailTimeMS(authToken.toBearerToken(), SetCoverThumbnailTimeMSRequestBody(attemptId, thumbnailTimeMS))
    }

    override suspend fun deleteDraft(authToken: AuthToken, attemptId: String): Unit = networkRequest {
        coverService.deleteDraft(authToken.toBearerToken(), DeleteDraftRequestBody(attemptId))
    }

}