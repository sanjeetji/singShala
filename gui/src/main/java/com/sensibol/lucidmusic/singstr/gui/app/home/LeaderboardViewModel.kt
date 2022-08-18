package com.sensibol.lucidmusic.singstr.gui.app.onboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.LeaderboardUser
import com.sensibol.lucidmusic.singstr.usecase.GetLeaderBoardUserRankUseCase
import com.sensibol.lucidmusic.singstr.usecase.GetLeaderUserListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class LeaderboardViewModel @Inject constructor(
    private val getLeaderUserListUseCase: GetLeaderUserListUseCase,
    private val getLeaderBoardUserRankUseCase: GetLeaderBoardUserRankUseCase
) : BaseViewModel() {

    private val _leaderUserList: MutableLiveData<List<LeaderboardUser>> by lazy { MutableLiveData() }
    internal val leaderUserList: LiveData<List<LeaderboardUser>> = _leaderUserList

    internal fun fetchLeaderUserList() {
        launchUseCases {
            _leaderUserList.postValue(getLeaderUserListUseCase())
        }
    }

    private val _leaderUserRank: MutableLiveData<Int> by lazy { MutableLiveData() }
    internal val leaderUserRank: LiveData<Int> = _leaderUserRank

    fun getUserRank() {
        launchUseCases {
            _leaderUserRank.postValue(getLeaderBoardUserRankUseCase())
        }
    }

}