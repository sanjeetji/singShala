package com.sensibol.lucidmusic.singstr.gui.app.sing.publish

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.SimpleAnalysis
import com.sensibol.lucidmusic.singstr.domain.model.SingScore
import com.sensibol.lucidmusic.singstr.usecase.GetSimpleAnalysisUseCase
import com.sensibol.lucidmusic.singstr.usecase.PublishCoverUseCase
import com.sensibol.lucidmusic.singstr.usecase.UploadCoverUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class CoverPublishViewModel @Inject constructor(
    private val publishCoverUseCase: PublishCoverUseCase,
    private val getSimpleAnalysisUseCase: GetSimpleAnalysisUseCase
) : BaseViewModel() {

    private val _simpleAnalysis: MutableLiveData<SimpleAnalysis> by lazy { MutableLiveData() }
    internal val simpleAnalysis: LiveData<SimpleAnalysis> = _simpleAnalysis

    internal fun loadSimpleAnalysis(attemptId: String) {
        Timber.d("loadSimpleAnalysis: IN ")
        launchUseCases {
            _simpleAnalysis.postValue(getSimpleAnalysisUseCase(attemptId))
        }
    }

    private val _publishSuccess: MutableLiveData<SimpleAnalysis> by lazy { MutableLiveData() }
    internal val publishSuccess: LiveData<SimpleAnalysis> = _publishSuccess

    internal fun publishCover(attemptId: String, caption: String, thumbnailTimeMS: Int) {
        Timber.v("publishCover: IN")
        launchUseCases {
            publishCoverUseCase(attemptId, caption, thumbnailTimeMS)
            _publishSuccess.postValue(getSimpleAnalysisUseCase(attemptId))
        }
        Timber.v("publishCover: OUT  $caption")
    }

}