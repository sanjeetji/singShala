package com.sensibol.lucidmusic.singstr.gui.app.learn.mylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.MyLessonMini
import com.sensibol.lucidmusic.singstr.usecase.GetSavedAllMyLessons
import com.sensibol.lucidmusic.singstr.usecase.RemoveFromMyListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MyListViewModel @Inject constructor(
    private val getSavedAllMyLessons: GetSavedAllMyLessons,
    private val removeFromMyListUseCase: RemoveFromMyListUseCase

) : BaseViewModel() {


    private val _myLesson: MutableLiveData<List<MyLessonMini>> by lazy { MutableLiveData<List<MyLessonMini>>() }

    internal val myLesson: LiveData<List<MyLessonMini>> = _myLesson

    internal fun getMyLesson() {
        launchUseCases {
            _myLesson.postValue(getSavedAllMyLessons())
        }
    }

    private val _removeFromMyList: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val removeFromMyList: LiveData<Boolean> = _removeFromMyList

    internal fun removeFromMyList(lessonId: String) {
        launchUseCases {
            _removeFromMyList.postValue(removeFromMyListUseCase(lessonId))
        }
    }
    

}