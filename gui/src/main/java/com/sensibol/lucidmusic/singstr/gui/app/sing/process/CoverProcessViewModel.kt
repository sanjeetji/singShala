package com.sensibol.lucidmusic.singstr.gui.app.sing.process

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.usecase.UpdateDraftForStagingUseCase
import com.sensibol.lucidmusic.singstr.usecase.UploadCoverUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class CoverProcessViewModel @Inject constructor(
    private val uploadCoverUseCase: UploadCoverUseCase,
    private val updateDraftForStagingUseCase: UpdateDraftForStagingUseCase
) : BaseViewModel() {

    private val _transferProgress: MutableLiveData<Float> by lazy { MutableLiveData<Float>() }
    internal val transferProgress: MutableLiveData<Float> = _transferProgress

    private val _uploadSuccess: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val uploadSuccess: LiveData<Boolean> = _uploadSuccess

    internal fun uploadCover(attemptId: String, singScore: SingScore) {
        Timber.v("uploadCover: IN")
        launchUseCases {
            uploadCoverUseCase(attemptId, singScore) { _transferProgress.postValue(it) }
            _uploadSuccess.postValue(true)
        }
        Timber.v("uploadCover: OUT")
    }

    private val _updateForStaging: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    internal val updateForStaging: LiveData<String> = _updateForStaging

    internal fun updateDraftForStaging(attemptId: String) {
        Timber.v("updateDraftForStaging: IN")
        launchUseCases {
          _updateForStaging.postValue(updateDraftForStagingUseCase(attemptId))
        }
        Timber.v("updateDraftForStaging: OUT")
    }

}