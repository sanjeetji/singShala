package com.sensibol.lucidmusic.singstr.gui.app.profile.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.CoverView
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.toCoverView
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class OtherUserProfileViewModel @Inject constructor(
    private val otherUserProfileUseCase: GetOtherUserProfileUseCase,
    private val addSubscriberUseCase: AddSubscriberUseCase,
    private val deleteSubscriberUseCase: DeleteSubscriberUseCase,
    private val otherUserSubmitsUseCase: GetOtherUserSubmitsUseCase,
    private val reportUserUseCase: ReportUserUseCase,
    private val userSubmitsUseCase: GetUserSubmitsWithStaticsUseCase,
    private val checkTeacherUseCase: CheckTeacherUseCase

) : BaseViewModel() {

    private val _otherUserProfile: MutableLiveData<OtherUserProfile> by lazy { MutableLiveData<OtherUserProfile>() }
    internal val otherUserProfile: LiveData<OtherUserProfile> = _otherUserProfile

    private val _coverViews: MutableLiveData<List<CoverView>> by lazy { MutableLiveData<List<CoverView>>() }
    internal val coverViews: LiveData<List<CoverView>> get() = _coverViews

    internal fun loadOtherUserProfile(otherUserId: String) {
        launchUseCases {
            val otherUserProfile: OtherUserProfile = otherUserProfileUseCase(otherUserId)
            _otherUserProfile.postValue(otherUserProfile)
        }
    }

    private val _otherUserSubmits: MutableLiveData<Submits> by lazy { MutableLiveData<Submits>() }
    internal val otherUserSubmits: LiveData<Submits> = _otherUserSubmits

    internal fun loadOtherUserSubmits(otherUserId: String) {
        launchUseCases {
//            val submits: Submits = otherUserSubmitsUseCase(otherUserId)
//            _otherUserSubmits.postValue(submits)
//            val attempts: List<Attempt> = submits.attempt
//            _coverViews.postValue(attempts.map { it.toCoverView() })
            val submits: SubmitsWithStatics = userSubmitsUseCase(otherUserId)
            _coverViews.postValue(
                submits.attempt.map { it.toCoverView() }
            )
        }
    }

    private val _isSubscribed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val isSubscribed: LiveData<Boolean> = _isSubscribed

    internal fun subscribeUser(subscriberId: String) {
        launchUseCases {
            _isSubscribed.postValue(addSubscriberUseCase(subscriberId))
        }
    }

    internal fun deleteSubscribeUser(subscriberId: String) {
        launchUseCases {
            _isSubscribed.postValue(deleteSubscriberUseCase(subscriberId))
        }
    }

    private val _reportUser: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    internal val reportUser: LiveData<Boolean> = _reportUser

    internal fun reportUser(message: String, reason: List<String>, reportedFor: String) {
        launchUseCases {
            _reportUser.postValue(reportUserUseCase(message, reason, reportedFor))
        }
    }

    private val _isTeacher: MutableLiveData<CheckTeacher> by lazy { MutableLiveData<CheckTeacher>() }
    internal val isTeacher: LiveData<CheckTeacher> = _isTeacher

    internal fun checkTeacher(userId: String) {
        launchUseCases {
            _isTeacher.postValue(checkTeacherUseCase(userId))
        }
    }
}