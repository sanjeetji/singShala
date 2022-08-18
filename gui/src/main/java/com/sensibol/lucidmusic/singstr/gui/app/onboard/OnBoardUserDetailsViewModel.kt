package com.sensibol.lucidmusic.singstr.gui.app.onboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.convertDatePatternWebEngage
import com.sensibol.lucidmusic.singstr.usecase.UpdateUserDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class OnBoardUserDetailsViewModel @Inject constructor(
    private val userDetailsUseCase: UpdateUserDetailsUseCase
) : BaseViewModel() {

    private val _success: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    internal val success: LiveData<Boolean> = _success

    internal fun updateUserDetail(name: String, dob: String?, gender: String?) {
        if (gender != null) {
            Analytics.setUserProperty(Analytics.UserProperty.UserGender(gender))
        }
        if (dob != null) {
            Analytics.setUserProperty(Analytics.UserProperty.UserBirthDate(convertDatePatternWebEngage(dob)))
        }
        Analytics.setUserProperty(Analytics.UserProperty.UserName(name))
        launchUseCases {
            _success.postValue(userDetailsUseCase(name, null, dob, gender, null, null, null, null, null))
        }
    }

}