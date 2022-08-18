package com.sensibol.lucidmusic.singstr.gui.app.contest

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class ContestViewModel @Inject constructor(
    private val getContestInfoUseCase: GetContestInfoUseCase,
    private val getContestInfoByIdUseCase: GetContestInfoByIdUseCase
) : BaseViewModel() {


    private val _contestData: MutableLiveData<ContestData> by lazy { MutableLiveData() }
    internal val contestData:LiveData<ContestData> = _contestData
    internal fun getContestData(){
        launchUseCases {
            _contestData.postValue(getContestInfoByIdUseCase())
        }
    }


    internal fun getContestInfo(){
        launchUseCases {
            val contestInfo = getContestInfoUseCase.invoke()
        }
    }

    internal fun getContestInfoById(){
        launchUseCases {
            val contestInfo = getContestInfoByIdUseCase.invoke()
        }
    }


}