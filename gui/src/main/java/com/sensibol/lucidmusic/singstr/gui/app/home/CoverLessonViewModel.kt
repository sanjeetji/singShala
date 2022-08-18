package com.sensibol.lucidmusic.singstr.gui.app.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CoverLessonViewModel @Inject constructor(
    private val getLearnContentUseCase: GetLearnContentUseCase
) : BaseViewModel() {

    private val _academyContent: MutableLiveData<AcademyContent> by lazy { MutableLiveData<AcademyContent>() }
    internal val academyContent: LiveData<AcademyContent> = _academyContent

    internal fun loadLearnContent() {
        launchUseCases {
            _academyContent.postValue(getLearnContentUseCase())
        }
    }

}