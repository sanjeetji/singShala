package com.sensibol.lucidmusic.singstr.domain.webservice

import com.sensibol.lucidmusic.singstr.domain.model.*

interface CoverWebService {

    suspend fun submitPracticeScore(authToken: AuthToken, singScore: SingScore,  songMini: SongMini): Boolean

    suspend fun submitCoverScore(authToken: AuthToken, singScore: SingScore, songMini: SongMini): CoverSubmitResult

    suspend fun uploadCover(authToken: AuthToken, attemptId: String, singScore: SingScore, onTransferProgress: OnTransferProgress)

    suspend fun publishCover(authToken: AuthToken, attemptId: String, caption: String, thumbnailTimeMS: Int)

    suspend fun deleteDraft(authToken: AuthToken, attemptId: String)

}