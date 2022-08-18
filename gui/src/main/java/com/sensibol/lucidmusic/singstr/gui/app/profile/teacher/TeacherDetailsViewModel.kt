package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.CoverView
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.toCoverView
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TeacherDetailsViewModel @Inject constructor(
    private val getTeacherUseCase: GetTeacherUseCase,
    private val getTeacherDetailUseCase: GetTeacherDetailUseCase,
    private val addSubscribeTeacherUseCase: AddSubscribeTeacherUseCase,
    private val deleteSubscriberTeacherCase: DeleteSubscriberTeacherCase,
    private val otherUserProfileUseCase: GetOtherUserProfileUseCase,
    private val getLeaderBoardUserRankUseCase: GetLeaderBoardUserRankUseCase,
    private val getUserSubmitsWithStaticsUseCase: GetUserSubmitsWithStaticsUseCase,
    private val deleteCoverUseCase: DeleteCoverUseCase,
    private val getNodeDraftUseCase: GetNodeDraftUseCase,
    private val getCoverDraftsUseCase: GetCoverDraftsUseCase,
    private val checkTeacherUseCase: CheckTeacherUseCase
) : BaseViewModel() {

    private val _teacherDetails: MutableLiveData<TeacherDetails> by lazy { MutableLiveData<TeacherDetails>() }
    internal val teacherDetails: LiveData<TeacherDetails> = _teacherDetails

    internal fun loadTeacherDetails(teacherId: String) {
        launchUseCases {
            val  teacherDetails = getTeacherUseCase.invoke(teacherId)
            _teacherDetails.postValue(teacherDetails)
        }
    }

    private val _teacherAllDetails: MutableLiveData<TeacherDetails> by lazy { MutableLiveData<TeacherDetails>() }
    internal val teacherAllDetails: LiveData<TeacherDetails> = _teacherAllDetails

    internal fun loadTeacherAllDetails(teacherId: String?) {
        launchUseCases {
            val  teacherDetails = getTeacherDetailUseCase.invoke(teacherId)
            _teacherAllDetails.postValue(teacherDetails)
        }
    }

    private val _isSubscribed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val isSubscribed: LiveData<Boolean> = _isSubscribed

    internal fun subscribeTeacher(teacherId: String) {
        launchUseCases {
            _isSubscribed.postValue(addSubscribeTeacherUseCase(teacherId))
        }
    }

    internal fun unSubscribeTeacher(teacherId: String) {
        launchUseCases {
            _isSubscribed.postValue(deleteSubscriberTeacherCase(teacherId))
        }
    }

    private val _teacherProfile: MutableLiveData<OtherUserProfile> by lazy { MutableLiveData<OtherUserProfile>() }
    internal val teacherProfile: LiveData<OtherUserProfile> = _teacherProfile

    internal fun loadTeacherProfile(otherUserId: String) {
        launchUseCases {
            val otherUserProfile: OtherUserProfile = otherUserProfileUseCase(otherUserId)
            _teacherProfile.postValue(otherUserProfile)
        }
    }

    private val _leaderTeacherRank: MutableLiveData<Int> by lazy { MutableLiveData() }
    internal val leaderTeacherRank: LiveData<Int> = _leaderTeacherRank

    internal fun getTeacherRank() {
        launchUseCases {
            _leaderTeacherRank.postValue(getLeaderBoardUserRankUseCase())
        }
    }

    private val _coverViews: MutableLiveData<List<CoverView>> by lazy { MutableLiveData() }
    internal val coverViews: LiveData<List<CoverView>> = _coverViews

    internal fun getCovers() {
        Timber.v("getCovers: IN")
        launchUseCases {
            val attempts: List<AttemptWithStatics> = getUserSubmitsWithStaticsUseCase(null).attempt
            _coverViews.postValue(attempts.map { it.toCoverView() })
        }
        Timber.v("getCovers: OUT")
    }

    private val _deleteCover: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    internal val deleteCover: LiveData<Boolean> = _deleteCover

    internal fun deleteCover(attemptId: String) {
        launchUseCases {
            _deleteCover.postValue(deleteCoverUseCase(attemptId))
        }
    }

    private val _draftList: MutableLiveData<List<NodeDraft>> by lazy { MutableLiveData() }
    internal val draftList: LiveData<List<NodeDraft>> = _draftList

    internal fun getUserDraftList(userId: String) {
        launchUseCases {
            _draftList.postValue(getNodeDraftUseCase(userId))
        }
    }

}