package com.sensibol.lucidmusic.singstr.gui.app.profile.self

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase,
    private val getCoversUseCase: GetCoverUseCase,
    private val getUserSubmitsWithStaticsUseCase: GetUserSubmitsWithStaticsUseCase,
    private val getUserStatsUseCase: GetUserStatsUseCase,
    private val deleteCoverUseCase: DeleteCoverUseCase,
    private val getLeaderBoardUserRankUseCase: GetLeaderBoardUserRankUseCase,
    private val getNodeDraftUseCase: GetNodeDraftUseCase,
    private val getCoverDraftsUseCase: GetCoverDraftsUseCase,
    private val checkTeacherUseCase: CheckTeacherUseCase
) : BaseViewModel() {

    private val _profileView: MutableLiveData<ProfileView> by lazy { MutableLiveData() }
    internal val profileView: LiveData<ProfileView> = _profileView

    internal fun getProfile() {
        Timber.v("getProfile: IN")
        launchUseCases {
            val user: User = getProfileUseCase()
            _profileView.postValue(user.toProfileView())
        }
        Timber.v("getProfile: OUT")
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

    private val _userStats: MutableLiveData<UserStats> by lazy { MutableLiveData() }
    internal val userStats: LiveData<UserStats> = _userStats

    internal fun getUserStats() {
        Timber.v("getUserStats: IN")
        launchUseCases {
            _userStats.postValue(getUserStatsUseCase())
        }
        Timber.v("getUserStats: OUT")
    }

    private val _deleteCover: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    internal val deleteCover: LiveData<Boolean> = _deleteCover

    internal fun deleteCover(attemptId: String) {
        launchUseCases {
            _deleteCover.postValue(deleteCoverUseCase(attemptId))
        }
    }

    private val _leaderUserRank: MutableLiveData<Int> by lazy { MutableLiveData() }
    internal val leaderUserRank: LiveData<Int> = _leaderUserRank

    internal fun getUserRank() {
        launchUseCases {
            _leaderUserRank.postValue(getLeaderBoardUserRankUseCase())
        }
    }

    private val _nodeDraft: MutableLiveData<List<NodeDraft>> by lazy { MutableLiveData() }
    internal val nodeDraft: LiveData<List<NodeDraft>> = _nodeDraft

    internal fun getNodeDraftList(userId: String) {
        launchUseCases {
            _nodeDraft.postValue(getNodeDraftUseCase(userId))
        }
    }

    private val _draftsPage: MutableLiveData<DraftsPage> by lazy { MutableLiveData<DraftsPage>() }
    internal val draftsPage: LiveData<DraftsPage> = _draftsPage

    internal fun getDrafts() {
        launchUseCases {
            _draftsPage.postValue(getCoverDraftsUseCase(null))
        }
    }

    private val _isTeacher: MutableLiveData<CheckTeacher> by lazy { MutableLiveData<CheckTeacher>() }
    internal val isTeacher: LiveData<CheckTeacher> = _isTeacher

    internal fun checkTeacher(userId: String) {
        launchUseCases {
            _isTeacher.postValue(checkTeacherUseCase(userId))
        }
    }

//    private val _teacherDetails: MutableLiveData<TeacherDetails> by lazy { MutableLiveData<TeacherDetails>() }
//    internal val teacherDetails: LiveData<TeacherDetails> = _teacherDetails
//
//    internal fun loadTeacherDetails(teacherId: String) {
//        launchUseCases {
//            _teacherDetails.postValue(getTeacherDetailUseCase(teacherId))
//        }
//    }

}