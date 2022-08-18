package com.sensibol.lucidmusic.singstr.gui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.gui.AppVersionCode
import com.sensibol.lucidmusic.singstr.usecase.CheckStreakUseCase
import com.sensibol.lucidmusic.singstr.usecase.IsForcedUpdateNeededUseCase
import com.sensibol.lucidmusic.singstr.usecase.IsUserLoggedInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import java.lang.StrictMath.max
import java.lang.System.currentTimeMillis
import javax.inject.Inject

const val MIN_SPLASH_DURATION_MS = 1_500L

@HiltViewModel
internal class SplashViewModel @Inject constructor(
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val isForcedUpdateNeededUseCase: IsForcedUpdateNeededUseCase,
    @AppVersionCode private val appVersionCode: Int
) : BaseViewModel() {

    private val _isUserLoggedIn: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    internal val isUserLoggedIn: LiveData<Boolean> = _isUserLoggedIn

    private val _isForceUpdateNeeded: MutableLiveData<Unit> by lazy { MutableLiveData() }
    internal val isForceUpdateNeeded: LiveData<Unit> get() = _isForceUpdateNeeded

    internal fun doApplicationStartup() {
        val startTime = currentTimeMillis()
        launchUseCases {
            if (isForcedUpdateNeededUseCase(appVersionCode)) {

                delay(max(MIN_SPLASH_DURATION_MS, currentTimeMillis() - startTime))
                _isForceUpdateNeeded.postValue(Unit)
            } else {
                val isUserLoggedIn = isUserLoggedInUseCase()

                delay(max(MIN_SPLASH_DURATION_MS, currentTimeMillis() - startTime))
                _isUserLoggedIn.postValue(isUserLoggedIn)
            }
        }
    }
}