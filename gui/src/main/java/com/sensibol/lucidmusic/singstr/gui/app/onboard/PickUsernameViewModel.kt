package com.sensibol.lucidmusic.singstr.gui.app.onboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.CheckUserExists
import com.sensibol.lucidmusic.singstr.usecase.CheckUserExistUseCase
import com.sensibol.lucidmusic.singstr.usecase.UpdateUserDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PickUsernameViewModel @Inject constructor(
    private val updateUserDetailsUseCase: UpdateUserDetailsUseCase,
    private val checkUserExistUseCase: CheckUserExistUseCase,

) : BaseViewModel(){

    private val _updateUser: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val updateUser: LiveData<Boolean> = _updateUser

    internal fun updateUserDetail(firstName: String, userHandle: String) {
        launchUseCases {
            _updateUser.postValue(updateUserDetailsUseCase(
                firstName,null, null, null, null, userHandle
            ))
        }
    }

    private val _userExits: MutableLiveData<CheckUserExists> by lazy { MutableLiveData() }
    internal val userExits: LiveData<CheckUserExists> get() = _userExits

    internal fun checkUserExits(checkValue: String) {
        launchUseCases {
            _userExits.postValue(checkUserExistUseCase(checkValue))
        }
    }
}