package com.sensibol.lucidmusic.singstr.gui.app.sing.prepare.mode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class SingModeViewModel @Inject constructor(
    private val getSongUseCase: GetSongUseCase,
    private val getSongPreviewUrlUseCase: GetSongPreviewUrlUseCase,
    private val prepareCreateCoverArgsUseCase: PrepareCreateCoverArgsUseCase,
    private val prepareSongPracticeArgsUseCase: PrepareSongPracticeArgsUseCase,
    private val computeBoostedScoreUseCase: ComputeBoostedScoreUseCase
) : BaseViewModel() {

    private val _song: MutableLiveData<SongMini> by lazy { MutableLiveData() }
    internal val songMini: LiveData<SongMini> = _song

    internal fun loadSongMini(songId: String) {
        launchUseCases {
            _song.postValue(getSongUseCase(songId))
        }
    }

    private val _previewUrl: MutableLiveData<String> by lazy { MutableLiveData() }
    internal val previewUrl: LiveData<String> = _previewUrl

    internal fun loadSongPreviewUrl(songId: String) {
        launchUseCases {
            _previewUrl.postValue(getSongPreviewUrlUseCase(songId))
        }
    }

    private val _transferProgress: MutableLiveData<Float> by lazy { MutableLiveData() }
    internal val transferProgress: MutableLiveData<Float> = _transferProgress

    private val _createCoverArgs: MutableLiveData<PrepareCreateCoverArgsUseCase.CreateCoverArgs> by lazy { MutableLiveData<PrepareCreateCoverArgsUseCase.CreateCoverArgs>() }
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

    private val _songPracticeArgs: MutableLiveData<PrepareSongPracticeArgsUseCase.PracticeArgs> by lazy { MutableLiveData<PrepareSongPracticeArgsUseCase.PracticeArgs>() }
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