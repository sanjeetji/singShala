package com.sensibol.lucidmusic.singstr.gui.app.analysis

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.SimpleAnalysis
import com.sensibol.lucidmusic.singstr.domain.model.UserReviewAccount
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class AnalysisVsPublishViewModel @Inject constructor(
    private val getSimpleAnalysisUseCase: GetSimpleAnalysisUseCase,
    private val getUserReviewAccountUseCase: GetUserReviewAccountUseCase,
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

}