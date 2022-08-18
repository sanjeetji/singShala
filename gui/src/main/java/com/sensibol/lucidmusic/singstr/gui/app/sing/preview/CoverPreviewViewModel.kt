package com.sensibol.lucidmusic.singstr.gui.app.sing.preview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.usecase.ComputeBoostedScoreUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetCoverVideoUrlUseCase
import com.sensibol.lucidmusic.singstr.usecase.PrepareCreateCoverArgsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class CoverPreviewViewModel @Inject constructor(
    private val prepareCreateCoverArgsUseCase: PrepareCreateCoverArgsUseCase,
    private val computeBoostedScoreUseCase: ComputeBoostedScoreUseCase,
    private val getCoverVideoUrlUseCase: GetCoverVideoUrlUseCase
) : BaseViewModel() {

    private val _coverUrl: MutableLiveData<String> by lazy { MutableLiveData() }
    internal val coverUrl: LiveData<String> = _coverUrl

    internal fun loadVideoUrl(coverId: String) {
        launchUseCases {
            _coverUrl.postValue(getCoverVideoUrlUseCase(coverId))
        }
    }

    private val _transferProgress: MutableLiveData<Float> by lazy { MutableLiveData() }
    internal val transferProgress: MutableLiveData<Float> get() = _transferProgress

    private val _createCoverArgs: MutableLiveData<PrepareCreateCoverArgsUseCase.CreateCoverArgs> by lazy { MutableLiveData() }
    internal val createCoverArgs: LiveData<PrepareCreateCoverArgsUseCase.CreateCoverArgs> get() = _createCoverArgs

    internal fun onCreateCoverSelected(songId: String) {
        Timber.v("onCreateCoverSelected: IN")
        launchUseCases {
            Timber.d("onCreateCoverSelected: preparing SDK args for $songId to create cover")
            val args = prepareCreateCoverArgsUseCase(songId) { _transferProgress.postValue(it) }
            _createCoverArgs.postValue(args)
            Timber.d("onCreateCoverSelected: createCoverArgs=$args")
        }
        Timber.v("onCreateCoverSelected: OUT")
    }

    private val _boostedSingScore: MutableLiveData<SingScore> by lazy { MutableLiveData() }
    internal val boostedSingScore: LiveData<SingScore> = _boostedSingScore

    internal fun computeBoostedScore(singScore: SingScore) {
        launchUseCases {
            val boostedTuneScore = computeBoostedScoreUseCase(singScore.tuneScore)
            val boostedTimingScore = computeBoostedScoreUseCase(singScore.timingScore)
            val boostedTotalScore = (boostedTuneScore + boostedTimingScore) * 0.5f
            _boostedSingScore.postValue(
                singScore.copy(
                    tuneScore = boostedTuneScore,
                    timingScore = boostedTimingScore,
                    totalScore = boostedTotalScore
                )
            )
        }

    }
}