package com.sensibol.lucidmusic.singstr.gui.app.player

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.DailyChallenge
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.usecase.GetDailyChallengeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class EarnXpViewModel @Inject constructor(
    private val getDailyChallengeUseCase: GetDailyChallengeUseCase
) : BaseViewModel() {

    private val _dailyChallenge: MutableLiveData<DailyChallenge> by lazy { MutableLiveData<DailyChallenge>() }
    internal val dailyChallenge: LiveData<DailyChallenge> = _dailyChallenge

    internal fun loadDailyChallenge() {
        launchUseCases {
            _dailyChallenge.postValue(getDailyChallengeUseCase())
        }
    }
}