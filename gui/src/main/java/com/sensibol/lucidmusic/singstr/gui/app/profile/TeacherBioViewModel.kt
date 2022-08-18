package com.sensibol.lucidmusic.singstr.gui.app.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.TeacherDetails
import com.sensibol.lucidmusic.singstr.usecase.GetTeacherDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TeacherBioViewModel @Inject constructor(
    private val getTeacherDetailUseCase: GetTeacherDetailUseCase
): BaseViewModel(){

    private val _teacherDetails: MutableLiveData<TeacherDetails> by lazy { MutableLiveData<TeacherDetails>() }
    internal val teacherDetails: LiveData<TeacherDetails> = _teacherDetails

    internal fun loadTeacherDetails(teacherId: String?) {
        launchUseCases {
            val  teacherDetails = getTeacherDetailUseCase.invoke(teacherId)
            _teacherDetails.postValue(teacherDetails)
        }
    }
}