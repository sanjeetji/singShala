package com.sensibol.lucidmusic.singstr.gui.app.profile.self.followersfollowing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FollowingUserViewModel @Inject constructor(
    private val getFollowingUserUseCase: GetFollowingUserUseCase,
    private val deleteSubscriberUseCase: DeleteSubscriberUseCase,
    private val addSubscriberUseCase: AddSubscriberUseCase
) : BaseViewModel() {

    private val _followingUser: MutableLiveData<List<FollowingUser>> by lazy { MutableLiveData<List<FollowingUser>>() }
    internal val followingUser: LiveData<List<FollowingUser>> = _followingUser

    internal fun loadFollowingUserData(userId: String) {
        launchUseCases {
            _followingUser.postValue(getFollowingUserUseCase(userId))
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