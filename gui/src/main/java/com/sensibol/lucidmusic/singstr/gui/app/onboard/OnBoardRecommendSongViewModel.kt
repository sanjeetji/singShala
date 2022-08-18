package com.sensibol.lucidmusic.singstr.gui.app.onboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.SongMini
import com.sensibol.lucidmusic.singstr.usecase.GetRecommendSongUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnBoardRecommendSongViewModel @Inject constructor(
    private val getRecommendSongUseCase: GetRecommendSongUseCase
) : BaseViewModel() {
    private val _recommendSong: MutableLiveData<List<SongMini>> by lazy { MutableLiveData<List<SongMini>>() }
    internal val recommendSong: LiveData<List<SongMini>> = _recommendSong

    internal fun loadRecommendSong() {
        launchUseCases {
            _recommendSong.postValue(getRecommendSongUseCase().take(2))
        }
    }
}