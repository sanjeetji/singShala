package com.sensibol.lucidmusic.singstr.gui.app.analysis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.DetailedAnalysis
import com.sensibol.lucidmusic.singstr.domain.model.SimpleAnalysis
import com.sensibol.lucidmusic.singstr.domain.model.UserReviewAccount
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class AnalysisViewModel @Inject constructor(
    private val getSimpleAnalysisUseCase: GetSimpleAnalysisUseCase,
    private val getUserReviewAccountUseCase: GetUserReviewAccountUseCase,
    private val getDetailAnalysisUseCase: GetDetailAnalysisUseCase,
    private val getSongPreviewUrlUseCase: GetSongPreviewUrlUseCase,
    private val getCoverVideoUrlUseCase: GetCoverVideoUrlUseCase
) : BaseViewModel() {

    private val _simpleAnalysis: MutableLiveData<SimpleAnalysis> by lazy { MutableLiveData() }
    internal val simpleAnalysis: LiveData<SimpleAnalysis> = _simpleAnalysis

    internal fun loadSimpleAnalysis(attemptId: String) {
        Timber.d("loadSimpleAnalysis: IN ")
        launchUseCases {
            _simpleAnalysis.postValue(getSimpleAnalysisUseCase(attemptId))
        }
    }

    private val _userReviewAccount: MutableLiveData<UserReviewAccount> by lazy { MutableLiveData() }
    internal val userReviewAccount: LiveData<UserReviewAccount> = _userReviewAccount

    internal fun getUserReviewAccount(attemptId: String) {
        Timber.d("getUserReviewAccount: IN ")
        launchUseCases {
            _userReviewAccount.postValue(getUserReviewAccountUseCase(attemptId))
        }
    }

    private val _detailAnalysis: MutableLiveData<DetailedAnalysis> by lazy { MutableLiveData() }
    internal val detailAnalysis: LiveData<DetailedAnalysis> = _detailAnalysis

    internal fun loadDetailAnalysis(attemptId: String) {
        Timber.d("loadDetailAnalysis: IN ")
        launchUseCases {
            val detailAnalysis = getDetailAnalysisUseCase(attemptId)
            _detailAnalysis.postValue(DetailedAnalysis(detailAnalysis.detailedCoupletReviews.filter {
                1 < it.lyrics.trim().split(" ").size
            }))
        }
    }

    private val _previewUrl: MutableLiveData<String> by lazy { MutableLiveData() }
    internal val previewUrl: LiveData<String> = _previewUrl

    internal fun getSongPreviewUrl(songId: String) {
        launchUseCases {
            _previewUrl.postValue(getSongPreviewUrlUseCase(songId))
        }
    }

    private val _feedVideoUrl: MutableLiveData<String> by lazy { MutableLiveData() }
    internal val feedVideoUrl: LiveData<String> = _feedVideoUrl

    internal fun getFeedVideoUrl(attemptId: String) {
        launchUseCases {
            _feedVideoUrl.postValue(getCoverVideoUrlUseCase(attemptId))
        }
    }
}