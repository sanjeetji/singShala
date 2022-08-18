package com.sensibol.lucidmusic.singstr.gui.app.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.SubscriptionPlan
import com.sensibol.lucidmusic.singstr.usecase.GetInAppPurchaseUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetSubscriptionPlanUseCase
import com.sensibol.lucidmusic.singstr.usecase.VerifyPurchaseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProSubscriptionPlanViewModel @Inject constructor(
    private val getSubscriptionPlanUseCase: GetSubscriptionPlanUseCase,
    private val getInAppPurchaseUseCase: GetInAppPurchaseUseCase,
    private val verifyPurchaseUseCase: VerifyPurchaseUseCase
) : BaseViewModel() {

    private val _subscriptionPlans: MutableLiveData<List<SubscriptionPlan>> by lazy { MutableLiveData() }
    internal val subscriptionPlan: LiveData<List<SubscriptionPlan>> = _subscriptionPlans

    internal fun fetchSubscriptionPlans() {
        launchUseCases {
            _subscriptionPlans.postValue(getSubscriptionPlanUseCase())
        }
    }

    private val _purchaseList: MutableLiveData<String> by lazy { MutableLiveData() }
    internal val purchaseList: LiveData<String> = _purchaseList

    internal fun initialisePayment(productId: String) {
        launchUseCases {
            _purchaseList.postValue(getInAppPurchaseUseCase(productId))
        }
    }

    private val _verifyPurchase: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    internal val verifyPurchase: LiveData<Boolean> = _verifyPurchase

    internal fun verifyInAppPurchase(receipt: String, signature: String) {
        launchUseCases {
            _verifyPurchase.postValue(verifyPurchaseUseCase(receipt, signature))
        }
    }
}