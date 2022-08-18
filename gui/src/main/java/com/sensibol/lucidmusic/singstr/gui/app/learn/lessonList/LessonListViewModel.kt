package com.sensibol.lucidmusic.singstr.gui.app.learn.lessonList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LessonListViewModel @Inject constructor(
    private val getNewLessonsUseCase: GetNewLessonsUseCase,
    private val getLessonByTuneUseCase: GetLessonByTuneUseCase,
    private val getLessonByTimingUseCase: GetLessonByTimingUseCase,
    private val addAllToMyListUseCase: AddAllToMyListUseCase
) : BaseViewModel() {

    private val _lessons: MutableLiveData<List<LessonMini>> by lazy { MutableLiveData<List<LessonMini>>() }
    internal val lessons: LiveData<List<LessonMini>> = _lessons

    internal fun loadNewLessons() {
        launchUseCases {
            _lessons.postValue(getNewLessonsUseCase())
        }
    }

    internal fun loadTuneLessons() {
        launchUseCases {
            _lessons.postValue(getLessonByTuneUseCase())
        }
    }

    internal fun loadTimingLessons() {
        launchUseCases {
            _lessons.postValue(getLessonByTimingUseCase())
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