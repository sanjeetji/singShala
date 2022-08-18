package com.sensibol.lucidmusic.singstr.domain

import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.domain.webservice.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FakeUserWebServiceModule {

    @Provides
    @Singleton
    internal fun provideUserWebService(): UserWebService =
        object : UserWebService {

            override suspend fun generateAccessToken(provider: String, token: String): AuthToken = AuthTokenFixture.newAuthToken()

            override suspend fun getUser(authToken: AuthToken): User = UserFixture.newUser()

            override suspend fun getUserCovers(authToken: AuthToken): List<Attempt> = CoverFixture.newCovers()
            override suspend fun updateUserDetails(
                authToken: AuthToken,
                name: String,
                lastName: String?,
                bio: String?,
                dob: String?,
                gender: String?,
                userHandle: String?,
                displayName: String?,
                city: String?,
                state: String?,
                singerType: String?
            ): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun addUserPreferences(authToken: AuthToken, songSet: List<String>?, language: List<String>?): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun getDailyChallenge(): DailyChallenge {
                TODO("Not yet implemented")
            }

            override suspend fun getUserStats(authToken: AuthToken): UserStats {
                TODO("Not yet implemented")
            }

            override suspend fun addSubscriber(authToken: AuthToken, subscriberId: String): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun deleteSubscriber(authToken: AuthToken, subscriberId: String): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun getOtherUserProfile(authToken: AuthToken, otherUserId: String): OtherUserProfile {
                TODO("Not yet implemented")
            }

            override suspend fun getOtherUserSubmits(authToken: AuthToken, otherUserId: String): Submits {
                TODO("Not yet implemented")
            }

            override suspend fun subscribeNotification(authToken: AuthToken, subscriberId: String): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun unsubscribeNotification(authToken: AuthToken, subscriberId: String): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun reportUser(authToken: AuthToken, message: String, reason: List<String>, reportedFor: String): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun reportCover(authToken: AuthToken, message: String, reason: List<String>, reportedFor: String): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun reportLesson(authToken: AuthToken, message: String, reason: List<String>, reportedFor: String): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun reportBug(authToken: AuthToken, message: String): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun getLeaderBoardUserList(authToken: AuthToken): List<LeaderboardUser> {
                TODO("Not yet implemented")
            }

            override suspend fun getLeaderBoardUserRank(authToken: AuthToken): Int {
                TODO("Not yet implemented")
            }


            override suspend fun getUserSubscription(authToken: AuthToken): ProSubscription {
                TODO("Not yet implemented")
            }

            override suspend fun updateUserSubscription(authToken: AuthToken): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun getUserReviewRemaining(authToken: AuthToken, userId: String): UserReviewAccount {
                TODO("Not yet implemented")
            }

            override suspend fun isAppExpired(appVersionCode: Int): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun isUserHandleAvailable(authToken: AuthToken, userHandle: String): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun updateUserOnBoardStatus(authToken: AuthToken, status: Boolean): Boolean? {
                TODO("Not yet implemented")
            }

            override suspend fun getUserSubscriptionPlans(authToken: AuthToken): List<SubscriptionPlan> {
                TODO("Not yet implemented")
            }

            override suspend fun initialisePayment(authToken: AuthToken, productId: String): String {
                TODO("Not yet implemented")
            }

            override suspend fun verifyPurchase(authToken: AuthToken, receipt: String, signature: String): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun appWalkThrough(authToken: AuthToken): List<AppWalkThroughSlide> {
                TODO("Not yet implemented")
            }

            override suspend fun updateProfilePic(authToken: AuthToken, file: File): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun fetchCorouselList(authToken: AuthToken): List<CarouselBanner> {
                TODO("Not yet implemented")
            }

            override suspend fun getAllConceptList(authToken: AuthToken, nextPageToken: String?): AllConceptData {
                TODO("Not yet implemented")
            }

            override suspend fun generateAccessPhoneToken(provider: String, token: String, firstName: String): AuthToken {
                TODO("Not yet implemented")
            }

        }
}

@Module
@InstallIn(SingletonComponent::class)
object FakeCoverWebServiceModule {

    @Provides
    @Singleton
    internal fun provideCoverWebService(): CoverWebService =
        object : CoverWebService {

            override suspend fun submitPracticeScore(authToken: AuthToken, singScore: SingScore, songMini: SongMini): Boolean {
                TODO("Not yet implemented")
            }

            override suspend fun submitCoverScore(authToken: AuthToken, singScore: SingScore, songMini: SongMini): CoverSubmitResult {
                TODO("Not yet implemented")
            }

            override suspend fun uploadCover(
                authToken: AuthToken,
                attemptId: String,
                singScore: SingScore,
                onTransferProgress: OnTransferProgress
            ) {
                TODO("Not yet implemented")
            }

            override suspend fun publishCover(authToken: AuthToken, attemptId: String, caption: String, thumbnailTimeMS: Int) {
                TODO("Not yet implemented")
            }

            override suspend fun deleteDraft(authToken: AuthToken, attemptId: String) {
                TODO("Not yet implemented")
            }

        }
}


@Module
@InstallIn(SingletonComponent::class)
object FakeContentWebServiceModule {

    @Provides
    @Singleton
    internal fun provideContentWebService(): ContentWebService =
        object : ContentWebService {

            override suspend fun getSongContentUrls(songId: String): ContentUrls =
                ContentUrlsFixture.newContentUrls()

            override suspend fun getExerciseContent(lessonId: String, exerciseId: String): ContentUrls {
                TODO("Not yet implemented")
            }

            override suspend fun getSongPracticeContentUrls(songId: String): ContentUrls {
                TODO("Not yet implemented")
            }
        }
}


@Module
@InstallIn(SingletonComponent::class)
object FakeFileDownloadWebServiceModule {


    @Provides
    @Singleton
    internal fun provideFileDownloadWebService(): FileDownloadWebService =
        object : FileDownloadWebService {

            override suspend fun downloadFile(url: String, file: File, onTransferProgress: OnTransferProgress) {}
        }

}


@Module
@InstallIn(SingletonComponent::class)
object FakeFileTransferWebServiceModule {


    @Provides
    @Singleton
    internal fun provideFileTransferWebService(): FileTransferWebService =
        object : FileTransferWebService {
            override suspend fun uploadFile(url: String, file: File, onTransferProgress: OnTransferProgress) {}

        }
}


@Module
@InstallIn(SingletonComponent::class)
object FakeSingWebServiceModule {


    @Provides
    @Singleton
    internal fun provideSingWebService(): SingWebService =
        object : SingWebService {

            override suspend fun getSongs(genre: Genre?, difficulty: Song.Difficulty?, query: String?): List<SongMini> {
                TODO("Not yet implemented")
            }

            override suspend fun getSong(songId: String): Song {
                TODO("Not yet implemented")
            }

            override suspend fun getSongPreviewUrl(songId: String): String {
                TODO("Not yet implemented")
            }

            override suspend fun getRecommendSongs(authToken: AuthToken): List<SongMini> {
                TODO("Not yet implemented")
            }

            override suspend fun getTrendingSongs(authToken: AuthToken, genre: Genre?): List<SongMini> {
                TODO("Not yet implemented")
            }

            override suspend fun getDetailAnalysis(authToken: AuthToken, attemptId: String): DetailedAnalysis {
                TODO("Not yet implemented")
            }

            override suspend fun getSimpleAnalysis(authToken: AuthToken, attemptId: String): SimpleAnalysis {
                TODO("Not yet implemented")
            }

            override suspend fun getSearchResults(authToken: AuthToken, keyword: String, lookup: String, page: String): SearchData {
                TODO("Not yet implemented")
            }

            override suspend fun getGenres(): List<Genre> {
                TODO("Not yet implemented")
            }

        }
}
