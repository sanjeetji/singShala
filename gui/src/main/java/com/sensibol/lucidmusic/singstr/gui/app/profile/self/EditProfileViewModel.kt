package com.sensibol.lucidmusic.singstr.gui.app.profile.self

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.usecase.IsUserHandleAvailableUseCase
import com.sensibol.lucidmusic.singstr.usecase.UpdateUserDetailsUseCase
import com.sensibol.lucidmusic.singstr.usecase.UpdateUserProfilePicUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
internal class EditProfileViewModel @Inject constructor(
    private val userDetailsUseCase: UpdateUserDetailsUseCase,
    private val isUserHandleAvailableUseCase: IsUserHandleAvailableUseCase,
    private val updateUserProfilePicUseCase: UpdateUserProfilePicUseCase
) : BaseViewModel() {

    private val _updateUser: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val updateUser: LiveData<Boolean> = _updateUser

    internal fun updateUserDetail(name: String, bio: String?, dob: String?, gender: String?, displayName: String?, city: String?, state: String?) {
        launchUseCases {
            _updateUser.postValue(userDetailsUseCase(name, "", bio, dob, gender, displayName, "",  city, state))
        }
    }

    private val _isUserHandleAvailable: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    internal val isUserHandleAvailable: LiveData<Boolean> = _isUserHandleAvailable

    internal fun checkUserHandleAvailability(name: String) {
        launchUseCases {
            _isUserHandleAvailable.postValue(isUserHandleAvailableUseCase(name))
        }
    }

    private val _updateProfilePic: MutableLiveData<Unit> by lazy { MutableLiveData<Unit>() }
    internal val updateProfilePic: LiveData<Unit> = _updateProfilePic

    internal fun updateProfilePic(file: File) {
        launchUseCases {
            _updateProfilePic.postValue(updateUserProfilePicUseCase(file))
        }
    }
}