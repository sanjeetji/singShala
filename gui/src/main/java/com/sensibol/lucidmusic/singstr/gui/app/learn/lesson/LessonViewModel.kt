package com.sensibol.lucidmusic.singstr.gui.app.learn.lesson

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.Lesson
import com.sensibol.lucidmusic.singstr.domain.model.TeacherDetails
import com.sensibol.lucidmusic.singstr.domain.model.TeacherSubscribeUnsbscribe
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class LessonViewModel @Inject constructor(
    private val getLessonUseCase: GetLessonUseCase,
    private val sendLessonWatchedDurationUseCase: SendLessonWatchedDurationUseCase,
    private val reportCoverUseCase: ReportCoverUseCase,
    private val addToMyListUseCase: AddToMyListUseCase,
    private val reportLessonUseCase: ReportLessonUseCase,
    private val getTeacherDetailUseCase: GetTeacherDetailUseCase,
    private val addSubscriberUseCase: AddSubscriberUseCase,
    private val deleteSubscriberUseCase: DeleteSubscriberUseCase,
    private val checkSubscriberUseCase: DeleteSubscriberUseCase,
    private val getFollowingUserUseCase: GetFollowingUserUseCase,
) : BaseViewModel() {

    private val _lesson: MutableLiveData<Lesson> by lazy { MutableLiveData<Lesson>() }
    internal val lesson: LiveData<Lesson> = _lesson

    internal fun getLesson(lessonId: String) {
        launchUseCases {
            _lesson.postValue(getLessonUseCase(lessonId))
        }
    }

    private val _reportLessonItems: MutableLiveData<List<String>> by lazy { MutableLiveData() }
    internal val reportLessonItems: LiveData<List<String>> = _reportLessonItems

    internal fun reportLessonItems(reason: List<String>) {
        launchUseCases {
            _reportLessonItems.postValue(reason)
        }
    }

    private val _progressCheck: MutableLiveData<Boolean> by lazy { MutableLiveData() }
    internal val progressCheck: LiveData<Boolean> = _progressCheck

    internal fun progressCheck() {
        launchUseCases {
            _progressCheck.postValue(true)
        }
    }

    private val _lessonWatched: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val lessonWatched: LiveData<Boolean> = _lessonWatched

    internal fun lessonWatched(lessonId: String, durationMS: Long) {
        Timber.d("lessonWatched: lesson $lessonId watched for $durationMS")
        launchUseCases {
            _lessonWatched.postValue(sendLessonWatchedDurationUseCase(lessonId, durationMS))
        }
    }

    private val _reportLesson: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val reportLesson: LiveData<Boolean> = _reportLesson

    internal fun reportLesson(message: String, reason: List<String>, reportedFor: String) {
        launchUseCases {
            _reportLesson.postValue(reportLessonUseCase(message, reason, reportedFor))
        }
    }


    private val _addToMyList: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val isAddedToMyList: LiveData<Boolean> = _addToMyList

    internal fun addToMyList(lessonId: String) {
        launchUseCases {
            _addToMyList.postValue(addToMyListUseCase(lessonId))
        }
    }

    private val _teacherDetails: MutableLiveData<TeacherDetails> by lazy { MutableLiveData<TeacherDetails>() }
    internal val teacherDetails: LiveData<TeacherDetails> = _teacherDetails

    internal fun loadTeacherDetails(teacherId: String?) {
        launchUseCases {
            val teacherDetails = getTeacherDetailUseCase.invoke(teacherId)
            _teacherDetails.postValue(teacherDetails)
        }
    }

//    private val _isSubscribed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
//    internal val isSubscribed: LiveData<Boolean> = _isSubscribed

//    internal fun subscribeTeacher(teacherId: String) {
//        launchUseCases {
//            _isSubscribed.postValue(addSubscribeTeacherUseCase(teacherId))
//        }
//    }
//
//    internal fun unSubscribeTeacher(teacherId: String) {
//        launchUseCases {
//            _isSubscribed.postValue(deleteSubscriberTeacherCase(teacherId))
//        }
//    }

    private val _isSubscribed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val isSubscribed: LiveData<Boolean> = _isSubscribed

    internal fun subscribeUser(subscriberId: String) {
        if (subscriberId.isNotBlank())
            launchUseCases {
                _isSubscribed.postValue(addSubscriberUseCase(subscriberId))
            }
    }

    private val _isUnSubscribed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val isUnSubscribed: LiveData<Boolean> = _isUnSubscribed

    internal fun deleteSubscribeUser(subscriberId: String) {
        if (subscriberId.isNotBlank())
            launchUseCases {
                _isUnSubscribed.postValue(deleteSubscriberUseCase(subscriberId))
            }
    }

    private val _isAlreadySubscribed: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    internal val isAlreadySubscribed: LiveData<Boolean> = _isAlreadySubscribed

    internal fun checkIsAlreadySubscribe(subscriberId: String) {
        if (subscriberId.isNotBlank())
            launchUseCases {
                val followings = getFollowingUserUseCase(subscriberId)
                var isAlreadyFollowing = false
                followings.forEach {
                    if(it._id == subscriberId){
                        isAlreadyFollowing = true
                        return@forEach
                    }
                }
                _isAlreadySubscribed.postValue(isAlreadyFollowing)
            }
    }
}