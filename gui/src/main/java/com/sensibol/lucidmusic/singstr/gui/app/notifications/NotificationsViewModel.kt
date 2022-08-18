package com.sensibol.lucidmusic.singstr.gui.app.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.Feed
import com.sensibol.lucidmusic.singstr.domain.model.Notifications
import com.sensibol.lucidmusic.singstr.domain.model.NotificationsList
import com.sensibol.lucidmusic.singstr.usecase.GetNotificationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class NotificationsViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
) : BaseViewModel() {

    public val notificationsList = mutableListOf<NotificationsList>()
    private val _notifications: MutableLiveData<NotificationsList> by lazy { MutableLiveData() }
    internal val notifications: LiveData<NotificationsList> = _notifications

    private var nextPageToken: String ? = null


    internal fun loadNotifications() {
        launchUseCases {
          val notification = getNotificationsUseCase(nextPageToken)
            if(notification.notifications.isEmpty())
                Timber.d("Notification list is empty")
            _notifications.postValue(notification)
            notificationsList.add(notification)
            nextPageToken = notification.nextPageToken
        }
    }

}