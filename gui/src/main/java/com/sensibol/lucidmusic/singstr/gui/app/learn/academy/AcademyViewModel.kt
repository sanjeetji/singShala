package com.sensibol.lucidmusic.singstr.gui.app.learn.academy

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AcademyViewModel @Inject constructor(
    private val getLearnContentUseCase: GetLearnContentUseCase,
    private val getNewLessonsUseCase: GetNewLessonsUseCase,
    private val getAcademyQuestionUseCase: GetAcademyQuestionUseCase,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val getTriviaLessonUseCase: GetTriviaLessonUseCase,
    private val getLessonTagsUseCase: GetLessonTagsUseCase,
    private val getLearnLessonsWrtTagsUseCase: GetLearnLessonsWrtTagsUseCase
) : BaseViewModel() {

    private val _academyContent: MutableLiveData<AcademyContent> by lazy { MutableLiveData<AcademyContent>() }
    internal val academyContent: LiveData<AcademyContent> = _academyContent

    private val _newLessons: MutableLiveData<List<LessonMini>> by lazy { MutableLiveData<List<LessonMini>>() }
    internal val newLessons: LiveData<List<LessonMini>> = _newLessons

    private val _timeLessons: MutableLiveData<List<LessonMini>> by lazy { MutableLiveData<List<LessonMini>>() }
    internal val timeLessons: LiveData<List<LessonMini>> = _timeLessons

    private val _tuneLessons: MutableLiveData<List<LessonMini>> by lazy { MutableLiveData<List<LessonMini>>() }
    internal val tuneLessons: LiveData<List<LessonMini>> = _tuneLessons

    internal fun loadLearnContent() {
        launchUseCases {
            _academyContent.postValue(getLearnContentUseCase())
        }
    }
    internal fun loadNewLessonsContent() {
        launchUseCases {
            _newLessons.postValue(getNewLessonsUseCase())
        }
    }
    private val _academyQuestion: MutableLiveData<McqQuestion> by lazy { MutableLiveData<McqQuestion>() }
    internal val academyQuestion: LiveData<McqQuestion> = _academyQuestion

    internal fun loadAcademyQuestion(previousId: String? = null) {
        launchUseCases {
            _academyQuestion.postValue(getAcademyQuestionUseCase(previousId))
        }
    }

    private val _submitAnswer: MutableLiveData<AnswerSubmitData> by lazy { MutableLiveData<AnswerSubmitData>() }
    internal val submitAnswer: LiveData<AnswerSubmitData> = _submitAnswer

    internal lateinit var questionId: String

    internal fun submitAnswer(questionId: String, answerId: String) {
        launchUseCases {
            _submitAnswer.postValue(submitAnswerUseCase(questionId, answerId))
        }
    }

    private val _triviaLesson: MutableLiveData<LessonMini> by lazy { MutableLiveData<LessonMini>() }

    internal val triviaLesson: LiveData<LessonMini> = _triviaLesson

    internal fun getTriviaLesson(lessonId: String) {
        launchUseCases {
            _triviaLesson.postValue(getTriviaLessonUseCase(lessonId))
        }
    }
    private val _tags: MutableLiveData<List<LessonTags>> by lazy { MutableLiveData<List<LessonTags>>() }
    internal val tags: LiveData<List<LessonTags>> = _tags

    internal fun loadTags() {
        launchUseCases {
            _tags.postValue(getLessonTagsUseCase())
        }
    }
    private val _tagLessons: MutableLiveData<List<LessonMini>> by lazy { MutableLiveData<List<LessonMini>>() }
    internal val tagLessons: LiveData<List<LessonMini>> = _tagLessons


    internal fun loadTagLessonsUseCase(lessonTags: List<String>? = null) {
        launchUseCases {
            _tagLessons.postValue(getLearnLessonsWrtTagsUseCase(lessonTags))
        }
    }

}