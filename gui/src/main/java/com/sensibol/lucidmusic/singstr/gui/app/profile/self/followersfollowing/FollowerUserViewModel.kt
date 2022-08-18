package com.sensibol.lucidmusic.singstr.gui.app.profile.self.followersfollowing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FollowerUserViewModel @Inject constructor(
    private val getFollowerUserUseCase: GetFollowerUserUseCase,
    private val deleteSubscriberUseCase: DeleteSubscriberUseCase,
    private val addSubscriberUseCase: AddSubscriberUseCase
) : BaseViewModel() {

    private val _followerUser: MutableLiveData<List<FollowersUser>> by lazy { MutableLiveData<List<FollowersUser>>() }
    internal val followerUser: LiveData<List<FollowersUser>> = _followerUser

    internal fun loadFollowerUserData(userId: String) {
        launchUseCases {
            _followerUser.postValue(getFollowerUserUseCase(userId))
        }
    }

    private val _isSubscribed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val isSubscribed: LiveData<Boolean> = _isSubscribed

    internal fun subscribeUser(subscriberId: String) {
        launchUseCases {
            _isSubscribed.postValue(addSubscriberUseCase(subscriberId))
        }
    }

    internal fun unSubscribeUser(subscriberId: String) {
        launchUseCases {
            _isSubscribed.postValue(deleteSubscriberUseCase(subscriberId))
        }
    }


}