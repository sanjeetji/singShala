package com.sensibol.lucidmusic.singstr.gui.app.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class FeedViewModel @Inject constructor(
    private val addClapCountUseCase: AddClapCountUseCase,
    private val deleteCoverUseCase: DeleteCoverUseCase,
    private val reportCoverUseCase: ReportCoverUseCase,
    private val reportUserUseCase: ReportUserUseCase,
    private val sendCoverWatchedDurationUseCase: SendCoverWatchedDurationUseCase,
    private val getSimpleAnalysisUseCase: GetSimpleAnalysisUseCase,
    private val getFollowingUserUseCase: GetFollowingUserUseCase,
    private val addSubscriberUseCase: AddSubscriberUseCase,
    private val deleteSubscriberUseCase: DeleteSubscriberUseCase,
    private val getDownloadVideoUrlUseCase: GetDownloadVideoUrlUseCase,
    private val downloadVideoContentUseCase: DownloadVideoContentUseCase
) : BaseViewModel() {

    internal fun addClap(attemptId: String, clapCount: Int) {
        Timber.d("addClap: added $clapCount clap(s) to $attemptId,")
        launchUseCases {
            addClapCountUseCase(attemptId, clapCount)
        }
    }

    private val _deleteCover: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val deleteCover: LiveData<Boolean> = _deleteCover

    internal fun deleteCover(attemptId: String) {
        launchUseCases {
            _deleteCover.postValue(deleteCoverUseCase(attemptId))
        }
    }

    private val _reportCover: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val reportCover: LiveData<Boolean> = _reportCover

    internal fun reportCover(message: String, reason: List<String>, reportedFor: String) {
        launchUseCases {
            _reportCover.postValue(reportCoverUseCase(message, reason, reportedFor))
        }
    }

    private val _reportUser: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val reportUser: LiveData<Boolean> = _reportUser

    internal fun reportUser(message: String, reason: List<String>, reportedFor: String) {
        launchUseCases {
            _reportUser.postValue(reportUserUseCase(message, reason, reportedFor))
        }
    }

    internal fun sendCoverWatchedDurationMS(attemptId: String, watchedDurationMS: Long) {
        Timber.d("sendCoverWatchedDurationMS: attempt $attemptId watched for $watchedDurationMS ms")
        launchUseCases {
            sendCoverWatchedDurationUseCase(attemptId, watchedDurationMS)
        }
    }

    private val _simpleAnalysis: MutableLiveData<SimpleAnalysis> by lazy { MutableLiveData() }
    internal val simpleAnalysis: LiveData<SimpleAnalysis> = _simpleAnalysis

    internal fun loadSimpleAnalysis(attemptId: String) {
        Timber.d("loadSimpleAnalysis: IN ")
        launchUseCases {
            _simpleAnalysis.postValue(getSimpleAnalysisUseCase(attemptId))
        }
    }

    internal var followingUserList= listOf<String>()
//
//    private val _followingUser: MutableLiveData<List<FollowingUser>> by lazy { MutableLiveData() }
//    internal val followingUser: LiveData<List<FollowingUser>> = _followingUser

    internal fun loadFollowingUser(userId: String) {
        launchUseCases {
            val userList = getFollowingUserUseCase(userId)
            followingUserList = userList.map { it._id }
            Timber.d("userList size ${userList.size} followingUserList ${followingUserList.size} for user $userId")
        }
    }

    internal fun isFollowingUser(userId: String): Boolean{
        return (followingUserList.contains(userId))
    }

    private val _isSubscribed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val isSubscribed: LiveData<Boolean> = _isSubscribed

    internal fun subscribeUser(subscriberId: String) {
        launchUseCases {
            _isSubscribed.postValue(addSubscriberUseCase(subscriberId))
        }
    }

    private val _isUnSubscribed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val isUnSubscribed: LiveData<Boolean> = _isUnSubscribed

    internal fun unSubscribeUser(subscriberId: String) {
        launchUseCases {
            _isUnSubscribed.postValue(deleteSubscriberUseCase(subscriberId))
        }
    }

    private val _downloadVideoUrl: MutableLiveData<DownloadVideoUrl> by lazy { MutableLiveData<DownloadVideoUrl>() }
    internal val downloadVideoUrl: LiveData<DownloadVideoUrl> = _downloadVideoUrl

    internal fun getDownloadVideoUrl(attemptId: String){
        launchUseCases {
            _downloadVideoUrl.postValue(getDownloadVideoUrlUseCase(attemptId))
        }
    }

    private val _downloadFilePath: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    internal val downloadFilePath: LiveData<String> = _downloadFilePath

    internal fun downloadVideoUrl(attemptId: String){
        launchUseCases {
            val filePath = downloadVideoContentUseCase(attemptId)
            _downloadFilePath.postValue(filePath)
            Timber.d("Nikhil-> video path $filePath")
        }
    }
}