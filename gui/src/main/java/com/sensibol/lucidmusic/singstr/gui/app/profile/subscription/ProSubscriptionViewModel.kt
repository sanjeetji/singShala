package com.sensibol.lucidmusic.singstr.gui.app.profile.subscription

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.ProSubscription
import com.sensibol.lucidmusic.singstr.domain.model.User
import com.sensibol.lucidmusic.singstr.usecase.GetProfileUseCase
import com.sensibol.lucidmusic.singstr.usecase.UpdateUserSubscriptionUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetUserSubscriptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class ProSubscriptionViewModel @Inject constructor(
    private val updateUserSubscriptionUseCase: UpdateUserSubscriptionUseCase,
    private val getUserSubscriptionUseCase: GetUserSubscriptionUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : BaseViewModel() {

    private val _updateProSubscription: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    internal val updateProSubscription: LiveData<Boolean> get() = _updateProSubscription

    private val _user: MutableLiveData<User> by lazy { MutableLiveData<User>() }
    internal val user: LiveData<User> = _user

    internal fun updateUserSubscription() {
        Timber.v("updateUserSubscription: IN")
        launchUseCases {
            _user.postValue(getProfileUseCase())
            _updateProSubscription.postValue(updateUserSubscriptionUseCase())
        }
        Timber.v("updateUserSubscription: OUT")
    }

    private val _getUserSubscription: MutableLiveData<ProSubscription> by lazy { MutableLiveData<ProSubscription>() }

    internal val getUserSubscription: LiveData<ProSubscription> get() = _getUserSubscription

    internal fun getUserSubscription() {
        Timber.v("getUserSubscription: IN")
        launchUseCases {
            _getUserSubscription.postValue(getUserSubscriptionUseCase())
        }
        Timber.v("getUserSubscription: OUT")
    }
}