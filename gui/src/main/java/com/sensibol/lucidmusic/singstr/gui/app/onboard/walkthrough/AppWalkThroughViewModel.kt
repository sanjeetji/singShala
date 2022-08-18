package com.sensibol.lucidmusic.singstr.gui.app.onboard.walkthrough

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.AppWalkThroughSlide
import com.sensibol.lucidmusic.singstr.usecase.GetWalkThroughSlidesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppWalkThroughViewModel @Inject constructor(
    private val getWalkThroughSlidesUseCase: GetWalkThroughSlidesUseCase
) : BaseViewModel() {

    private val _walkThroughSlides: MutableLiveData<List<AppWalkThroughSlide>> by lazy { MutableLiveData() }
    internal val walkThroughSlides: LiveData<List<AppWalkThroughSlide>> = _walkThroughSlides

    internal fun fetchWalkThroughSlide() {
        launchUseCases {
            _walkThroughSlides.postValue(getWalkThroughSlidesUseCase())
        }
    }
}