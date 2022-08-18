package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.AcademyContent
import com.sensibol.lucidmusic.singstr.usecase.GetLearnContentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TeacherLessonViewModel @Inject constructor(
    private val getLearnContentUseCase: GetLearnContentUseCase
): BaseViewModel(){

    var listSize: Int = 0

    private val _lessonGroup: MutableLiveData<List<AcademyContent.LessonGroup>> by lazy { MutableLiveData<List<AcademyContent.LessonGroup>>() }
    internal val lessonGroup: LiveData<List<AcademyContent.LessonGroup>> = _lessonGroup

    internal fun loadLessonGroup() {
        launchUseCases {
            val lessons = getLearnContentUseCase()
            listSize = lessons.lessonGroups.size
            _lessonGroup.postValue(lessons.lessonGroups)
        }
    }
}