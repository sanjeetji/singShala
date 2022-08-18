package com.sensibol.lucidmusic.singstr.gui.app.sing.result.practice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.usecase.ComputeBoostedScoreUseCase
import com.sensibol.lucidmusic.singstr.usecase.PrepareCreateCoverArgsUseCase
import com.sensibol.lucidmusic.singstr.usecase.PrepareSongPracticeArgsUseCase
import com.sensibol.lucidmusic.singstr.usecase.SubmitPracticeScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class PracticeResultViewModel @Inject constructor(
    private val submitPracticeScoreUseCase: SubmitPracticeScoreUseCase,
    private val prepareCreateCoverArgsUseCase: PrepareCreateCoverArgsUseCase,
    private val prepareSongPracticeArgsUseCase: PrepareSongPracticeArgsUseCase,
    private val computeBoostedScoreUseCase: ComputeBoostedScoreUseCase
) : BaseViewModel() {

    internal fun submitPracticeScore(singScore: SingScore, songMini: SongMini) {
        Timber.d("submitPracticeScore: $singScore")
        launchUseCases {
            submitPracticeScoreUseCase(singScore, songMini)
        }
    }

    private val _songPracticeArgs: MutableLiveData<PrepareSongPracticeArgsUseCase.PracticeArgs> by lazy { MutableLiveData() }
    internal val songPracticeArgs: LiveData<PrepareSongPracticeArgsUseCase.PracticeArgs> = _songPracticeArgs

    internal fun prepareSongPracticeArgs(songId: String) {
        Timber.v("prepareSongPracticeArgs: IN")
        launchUseCases {
            Timber.d("prepareSongPracticeArgs: preparing SDK args for $songId to song practice")
            val args = prepareSongPracticeArgsUseCase(songId) { _transferProgress.postValue(it) }
            _songPracticeArgs.postValue(args)
            Timber.d("prepareSongPracticeArgs: createCoverArgs=$args")
        }
        Timber.v("prepareSongPracticeArgs: OUT")
    }

    private val _createCoverArgs: MutableLiveData<PrepareCreateCoverArgsUseCase.CreateCoverArgs> by lazy { MutableLiveData() }
    internal val createCoverArgs: LiveData<PrepareCreateCoverArgsUseCase.CreateCoverArgs> = _createCoverArgs

    internal fun prepareCreateCoverArgs(songId: String) {
        Timber.v("prepareCreateCoverArgs: IN")
        launchUseCases {
            Timber.d("prepareCreateCoverArgs: preparing SDK args for $songId to create cover")
            val args = prepareCreateCoverArgsUseCase(songId) { _transferProgress.postValue(it) }
            _createCoverArgs.postValue(args)
            Timber.d("prepareCreateCoverArgs: createCoverArgs=$args")
        }
        Timber.v("prepareCreateCoverArgs: OUT")
    }

    private val _boostedsingScore: MutableLiveData<SingScore> by lazy { MutableLiveData() }
    internal val boostedSingScore: LiveData<SingScore> = _boostedsingScore

    internal fun computeBoostedScore(singScore: SingScore) {
        launchUseCases {
            val boostedTuneScore = computeBoostedScoreUseCase(singScore.tuneScore)
            val boostedTimingScore = computeBoostedScoreUseCase(singScore.timingScore)
            val boostedTotalScore = (boostedTuneScore + boostedTimingScore) * 0.5f
            _boostedsingScore.postValue(
                singScore.copy(
                    tuneScore = boostedTuneScore,
                    timingScore = boostedTimingScore,
                    totalScore = boostedTotalScore
                )
            )
        }
    }

    private val _transferProgress: MutableLiveData<Float> by lazy { MutableLiveData() }
    internal val transferProgress: MutableLiveData<Float> = _transferProgress

}