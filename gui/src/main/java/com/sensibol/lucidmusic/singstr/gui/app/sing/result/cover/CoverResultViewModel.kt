package com.sensibol.lucidmusic.singstr.gui.app.sing.result.cover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.CoverSubmitResult
import com.sensibol.lucidmusic.singstr.domain.model.SimpleAnalysis
import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.usecase.GetSimpleAnalysisUseCase
import com.sensibol.lucidmusic.singstr.usecase.SubmitCoverScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
internal class CoverResultViewModel @Inject constructor(
    private val submitCoverScoreUseCase: SubmitCoverScoreUseCase,
    private val getSimpleAnalysisUseCase: GetSimpleAnalysisUseCase
) : BaseViewModel() {

    private val _coverSubmitResult: MutableLiveData<CoverSubmitResult> by lazy { MutableLiveData() }
    internal val coverSubmitResult: LiveData<CoverSubmitResult> = _coverSubmitResult

    private val _simpleAnalysis: MutableLiveData<SimpleAnalysis> by lazy { MutableLiveData() }
    internal val simpleAnalysis: LiveData<SimpleAnalysis> = _simpleAnalysis

    internal fun submitCoverScore(singScore: SingScore, songMini: SongMini) {
        Timber.d("submitCoverScore: $singScore")
        launchUseCases {
            val coverSubmitResult = submitCoverScoreUseCase(singScore, songMini)
            val simpleAnalysis = getSimpleAnalysisUseCase(coverSubmitResult.attemptId)
            _simpleAnalysis.postValue(simpleAnalysis)
            _coverSubmitResult.postValue(coverSubmitResult)
        }
    }

}