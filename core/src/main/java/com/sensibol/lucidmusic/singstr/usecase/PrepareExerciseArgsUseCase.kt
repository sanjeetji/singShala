package com.sensibol.lucidmusic.singstr.usecase

import com.sensibol.lucidmusic.singstr.domain.model.Exercise
import com.sensibol.lucidmusic.singstr.domain.model.Lesson
import com.sensibol.lucidmusic.singstr.domain.webservice.ContentWebService
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class PrepareExerciseArgsUseCase @Inject constructor(
    private val downloadContentUseCase: DownloadContentUseCase,
    private val contentWebService: ContentWebService,
    private val getMixFilePathUseCase: GetMixFilePathUseCase
) {

    data class ExerciseArgs(
        val exercise: Exercise,
        val metadataFile: File,
        val mediaFile: File,
    )

    @Deprecated("unnecessary lessonId needed by backend")
    suspend operator fun invoke(lesson:Lesson, exercise: Exercise, progress: OnProgress): ExerciseArgs {
        Timber.v("invoke: IN")

        val contentUrls = contentWebService.getExerciseContent(lesson.id, exercise.id)
        Timber.d("invoke: contentUrls=$contentUrls")

        val contentPaths = downloadContentUseCase(exercise.id, contentUrls, progress)
        Timber.d("invoke: contentPaths=$contentPaths")

        return ExerciseArgs(
            exercise = exercise,
            metadataFile = contentPaths.metadataFile,
            mediaFile = contentPaths.mediaFile,
        ).also {
            Timber.v("invoke: OUT")
        }
    }
}