package com.sensibol.lucidmusic.singstr.gui.app.payment.claim

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.usecase.ClaimFreeSubscriptionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class ClaimFreeSubscriptionViewModel @Inject constructor(
    private val claimFreeSubscriptionUseCase: ClaimFreeSubscriptionUseCase
) : BaseViewModel() {

    private val _claimMsg: MutableLiveData<String> by lazy { MutableLiveData() }
    internal val claimMsg : LiveData<String> = _claimMsg

    internal fun claimFreeSubscription(code: String){
        launchUseCases {
            _claimMsg.postValue(claimFreeSubscriptionUseCase(code))
        }
    }
}