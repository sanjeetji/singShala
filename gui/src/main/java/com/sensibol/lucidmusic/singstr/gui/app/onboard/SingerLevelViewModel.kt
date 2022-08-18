package com.sensibol.lucidmusic.singstr.gui.app.onboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.usecase.UpdateUserDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class SingerLevelViewModel @Inject constructor(
    private val updateUserDetailsUseCase: UpdateUserDetailsUseCase
) : BaseViewModel(){

    var singerType: String = ""
    private val _updateUser: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val updateUser: LiveData<Boolean> = _updateUser

    internal fun updateUserDetail(name: String) {
        launchUseCases {
            _updateUser.postValue(updateUserDetailsUseCase(
                name, null, null, null,null, null, null, null,
                null, singerType))
        }
    }

}