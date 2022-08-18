package com.sensibol.lucidmusic.singstr.gui.app.learn.lessonList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConceptLessonListViewModel @Inject constructor(
    private val getLessonByConceptUseCase: GetLessonByConceptUseCase,
    private val addAllToMyListUseCase: AddAllToMyListUseCase
) : BaseViewModel() {

    private val _conceptInfo: MutableLiveData<ConceptInfo> by lazy { MutableLiveData<ConceptInfo>() }
    internal val conceptInfo: LiveData<ConceptInfo> = _conceptInfo

    internal fun loadConceptInfo(conceptId: String) {
        launchUseCases {
            _conceptInfo.postValue(getLessonByConceptUseCase(conceptId))
        }
    }

    private val _saveAllSuccess: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    internal val saveAllSuccess: LiveData<String> = _saveAllSuccess

    internal fun saveAllToMyList(lessonIds: List<String>){
        launchUseCases {
            _saveAllSuccess.postValue(addAllToMyListUseCase(lessonIds))
        }
    }

}