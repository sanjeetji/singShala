package com.sensibol.lucidmusic.singstr.gui.app.profile.other

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.AcademyContent
import com.sensibol.lucidmusic.singstr.usecase.GetLearnContentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OtherTeacherLessonViewModel @Inject constructor(
    private val getLearnContentUseCase: GetLearnContentUseCase
): BaseViewModel(){

    private val _lessonGroup: MutableLiveData<List<AcademyContent.LessonGroup>> by lazy { MutableLiveData<List<AcademyContent.LessonGroup>>() }
    internal val lessonGroup: LiveData<List<AcademyContent.LessonGroup>> = _lessonGroup

    internal fun loadLessonGroup() {
        launchUseCases {
            _lessonGroup.postValue(getLearnContentUseCase().lessonGroups)
        }
    }
}