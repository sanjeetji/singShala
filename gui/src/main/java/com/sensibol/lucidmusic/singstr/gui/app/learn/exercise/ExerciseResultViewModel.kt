package com.sensibol.lucidmusic.singstr.gui.app.learn.exercise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sensibol.android.base.gui.viewmodel.BaseViewModel
import com.sensibol.lucidmusic.singstr.domain.model.Exercise
import com.sensibol.lucidmusic.singstr.domain.model.ExerciseScore
import com.sensibol.lucidmusic.singstr.domain.model.Lesson
import com.sensibol.lucidmusic.singstr.usecase.ComputeBoostedScoreUseCase
import com.sensibol.lucidmusic.singstr.usecase.PrepareExerciseArgsUseCase
import com.sensibol.lucidmusic.singstr.usecase.SubmitExerciseScoreUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class ExerciseResultViewModel @Inject constructor(
    private val prepareExerciseArgsUseCase: PrepareExerciseArgsUseCase,
    private val submitExerciseScoreUseCase: SubmitExerciseScoreUseCase,
    private val computeBoostedScoreUseCase: ComputeBoostedScoreUseCase
) : BaseViewModel() {

    private val _transferProgress: MutableLiveData<Float> by lazy { MutableLiveData() }
    internal val transferProgress: MutableLiveData<Float> = _transferProgress

    internal fun submitExerciseScore(exerciseScore: ExerciseScore) {
        Timber.d("submitExerciseScore: $exerciseScore")
        launchUseCases {
            submitExerciseScoreUseCase(exerciseScore)
        }
    }

    private val _exerciseArgs: MutableLiveData<PrepareExerciseArgsUseCase.ExerciseArgs> by lazy { MutableLiveData() }
    internal val exerciseArgs: LiveData<PrepareExerciseArgsUseCase.ExerciseArgs> = _exerciseArgs

    @Deprecated("unnecessary lessonId needed by backend")
    internal fun prepareExerciseArgs(lesson:Lesson, exercise: Exercise) {
        Timber.v("prepareArgs: IN")
        launchUseCases {
            Timber.d("prepareArgs: preparing SDK args for ${exercise.name} to create cover")
            val args = prepareExerciseArgsUseCase(lesson, exercise) { _transferProgress.postValue(it) }
            _exerciseArgs.postValue(args)
            Timber.d("prepareArgs: createCoverArgs=$args")
        }
        Timber.v("prepareArgs: OUT")
    }

    private val _boostedExerciseScore: MutableLiveData<ExerciseScore> by lazy { MutableLiveData() }
    internal val boostedExerciseScore: LiveData<ExerciseScore> = _boostedExerciseScore

    internal fun computeBoostedScore(exerciseScore: ExerciseScore) {
        Timber.d("computeBoostedScore: IN")
        Timber.d("computeBoostedScore: exerciseScore $exerciseScore")

        launchUseCases {
            val boostedTuneScore = computeBoostedScoreUseCase(exerciseScore.tuneScore)
            val boostedTimingScore = computeBoostedScoreUseCase(exerciseScore.timingScore)
            val boostedTotalScore = (boostedTuneScore + boostedTimingScore) * 0.5f
            _boostedExerciseScore.postValue(
                exerciseScore.copy(
                    tuneScore = boostedTuneScore,
                    timingScore = boostedTimingScore,
                    totalScore = boostedTotalScore
                )
            )
        }

    }

}