package com.sensibol.lucidmusic.singstr.gui.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.karaoke.Doorway
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.BuildConfig
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class SingstrViewModel @Inject constructor(
    private val getUserStatsUseCase: GetUserStatsUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getDailyChallengeUseCase: GetDailyChallengeUseCase,
    private val syncFCMTokenUseCase: SyncFCMTokenUseCase,
    private val subscribeNotificationUseCase: SubscribeNotificationUseCase,
    private val updateUserSubscriptionUseCase: UpdateUserSubscriptionUseCase,
    private val getUserSubscriptionUseCase: GetUserSubscriptionUseCase,
    private val setUserOnBoardedUseCase: SetUserOnBoardedUseCase,
    private val deleteAccessTokenUseCase: DeleteAccessTokenUseCase,
    private val updateUserDetailsUseCase: UpdateUserDetailsUseCase,
    private val checkTeacherUseCase: CheckTeacherUseCase
) : BaseViewModel() {

    private val _user: MutableLiveData<User> by lazy { MutableLiveData<User>() }
    internal val user: LiveData<User> = _user

    internal fun loadUserProfile() {
        Timber.v("loadUserProfile: IN")
        launchUseCases {
            val user: User = getProfileUseCase()
            Analytics.setWebEngageUserSystemProperty(user)
            _user.postValue(user)
        }
        Timber.v("loadUserProfile: OUT")
    }


    internal fun setUserOnBoarded(isUserOnBoarded: Boolean) {
        Timber.v("setUserOnBoarded: IN")
        launchUseCases {
            setUserOnBoardedUseCase(isUserOnBoarded)
            loadUserProfile()
        }
        Timber.v("setUserOnBoarded: OUT")
    }

    private val _userStats: MutableLiveData<UserStats> by lazy { MutableLiveData<UserStats>() }
    internal val userStats: LiveData<UserStats> = _userStats

    internal fun loadUserStats() {
        Timber.v("loadUserStats: IN")
        launchUseCases {
            val userStats = getUserStatsUseCase()
            Analytics.setUserProperty(Analytics.UserProperty.UserXp(userStats.totalXp))
            Analytics.setUserProperty(Analytics.UserProperty.UserLevel(userStats.level))
            Analytics.facebookLogEvent(
                Analytics.Event.FbMobileLevelAchievedEvent(
                    Analytics.Event.Param.UserLevel(userStats.level.toString())
                )
            )
            _userStats.postValue(userStats)
        }
        Timber.v("loadUserStats: OUT")
    }

    private val _dailyChallenge: MutableLiveData<DailyChallenge> by lazy { MutableLiveData<DailyChallenge>() }
    internal val dailyChallenge: LiveData<DailyChallenge> = _dailyChallenge

    internal fun loadDailyChallenge() {
        launchUseCases {
            _dailyChallenge.postValue(getDailyChallengeUseCase())
        }
    }

    private val _setFCMToken: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val setFCMToken: LiveData<Boolean> get() = _setFCMToken

    internal fun setFCMToken(token: String?) {
        launchUseCases {
            syncFCMTokenUseCase(token)
            _setFCMToken.postValue(true)
        }
    }

    private val _isSubscribed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val isSubscribed: LiveData<Boolean> get() = _isSubscribed

    internal fun subscribeNotification(subscription: String) {
        Timber.v("subscribeNotification: IN")
        launchUseCases {
            _isSubscribed.postValue(subscribeNotificationUseCase(subscription))
        }
        Timber.v("subscribeNotification: OUT")
    }

    private val _updateProSubscription: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val updateProSubscription: LiveData<Boolean> get() = _updateProSubscription

    internal fun updateUserSubscription() {
        Timber.v("updateUserSubscription: IN")
        launchUseCases {
            _updateProSubscription.postValue(updateUserSubscriptionUseCase())
        }
        Timber.v("updateUserSubscription: OUT")
    }

    private val _userSubscription: MutableLiveData<ProSubscription> by lazy { MutableLiveData<ProSubscription>() }
    internal val userSubscription: LiveData<ProSubscription> = _userSubscription

    internal fun getUserSubscription() {
        Timber.v("getUserSubscription: IN")
        launchUseCases {
            _userSubscription.postValue(getUserSubscriptionUseCase())
        }
        Timber.v("getUserSubscription: OUT")
    }

    private val _cameraFacing: MutableLiveData</*@Doorway.CameraFacing*/ Int> by lazy { MutableLiveData(Doorway.CameraFacing.FRONT) }
    internal val cameraFacing: LiveData</*@Doorway.CameraFacing*/ Int> = _cameraFacing

    internal fun switchCamera(face: Int) {
        _cameraFacing.postValue(face)
    }

    private val _isLogoutSuccessful: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    internal val isLogoutSuccessful: LiveData<Boolean> = _isLogoutSuccessful

    internal fun logout() {
        Timber.v("logout: IN")
        launchUseCases {
            deleteAccessTokenUseCase()
            _isLogoutSuccessful.postValue(true)
        }
        Timber.v("logout: OUT")
    }

    private val _isTeacher: MutableLiveData<CheckTeacher> by lazy { MutableLiveData() }
    internal val isTeacher: LiveData<CheckTeacher> = _isTeacher

    internal fun checkIsTeacher(userId: String) {
        Timber.v("checkIsTeacher: IN")
        launchUseCases {
            _isTeacher.postValue(checkTeacherUseCase(userId))
        }
        Timber.v("checkIsTeacher: OUT")
    }
}