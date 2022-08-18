package com.sensibol.lucidmusic.singstr.gui.app.profile.self

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.Attempt
import com.sensibol.lucidmusic.singstr.domain.model.UserStats
import com.sensibol.lucidmusic.singstr.usecase.SubscribeNotificationUseCase
import com.sensibol.lucidmusic.singstr.usecase.UnsubscribeNotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class NotificationSettingViewModel @Inject constructor(
    private val subscribeNotificationUseCase: SubscribeNotificationUseCase,
    private val unsubscribeNotificationUseCase: UnsubscribeNotificationUseCase
) : BaseViewModel() {

    private val _isSubscribed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    internal val isSubscribed: LiveData<Boolean> get() = _isSubscribed

    internal fun subscribeNotification(subscription: String) {
        Timber.v("subscribeNotification: IN")
        launchUseCases {
            _isSubscribed.postValue(subscribeNotificationUseCase(subscription))
        }
        Timber.v("subscribeNotification: OUT")
    }

    private val _isUnsubscribed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    internal val isUnsubscribed: LiveData<Boolean> get() = _isUnsubscribed

    internal fun unsubscribeNotification(subscription: String) {
        Timber.v("unsubscribeNotification: IN")
        launchUseCases {
            _isUnsubscribed.postValue(unsubscribeNotificationUseCase(subscription))
        }
        Timber.v("unsubscribeNotification: OUT")
    }

}