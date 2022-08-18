package com.sensibol.lucidmusic.singstr.gui.app.learn.exercise

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.karaoke.Doorway
import com.sensibol.lucidmusic.singstr.domain.model.Exercise
import com.sensibol.lucidmusic.singstr.domain.model.ExerciseFromId
import com.sensibol.lucidmusic.singstr.domain.model.ExerciseScore
import com.sensibol.lucidmusic.singstr.domain.model.Lesson
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentExerciseLauncherBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.isFakeSinging
import com.sensibol.lucidmusic.singstr.usecase.PrepareExerciseArgsUseCase
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class ExerciseLauncherFragment : BaseFragment() {

    override val layoutResId: Int get() = R.layout.fragment_exercise_launcher
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentExerciseLauncherBinding::inflate
    override val binding: FragmentExerciseLauncherBinding get() = super.binding as FragmentExerciseLauncherBinding

    private val viewModel: ExerciseLauncherViewModel by viewModels()

    private val args: ExerciseLauncherFragmentArgs by navArgs()

    private lateinit var lesson: Lesson
    private lateinit var exercise: Exercise
    private var isComingFromDeeplink = false

    override fun onInitViewModel() {
        super.onInitViewModel()
        if (args.exercise == null) {
            isComingFromDeeplink = true
            args.exerciseId?.let { viewModel.getExerciseFromId(it) }
        }
    }

    override fun onInitView() {
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(exerciseArgs, ::startExerciseInterface)
            observe(boostedExerciseScore, ::onBoostedScore)
            observe(exerciseFromId, ::onExerciseFromId)
            observe(lesson, ::onLessonFromId)
            observe(transferProgress) {
                (it * 100).roundToInt().let {
                    binding.pbDownloadProgress.progress = it
                    binding.pbDownloadProgress.visibility = if (it == 100) View.INVISIBLE else View.VISIBLE
                }
            }
        }
        binding.apply {
            ibStart.visibility = if (isComingFromDeeplink) GONE else VISIBLE

            if (!isComingFromDeeplink) {
                exercise = args.exercise!!
                lesson = args.lesson!!
            }

            args.exercise?.apply {
                tvTitle.text = name
                tvDescription.text = subtitle
            }

            ibStart.setOnClickListener {
                if (isFakeSinging) {
                    fakeSinging()
                } else {
                    Analytics.logEvent(
                        Analytics.Event.ExerciseAttemptEvent(
                            Analytics.Event.Param.LessonId(lesson.id),
                            Analytics.Event.Param.LessonOwner(lesson.teacher.name),
                            Analytics.Event.Param.LessonCategory(lesson.type),
                            Analytics.Event.Param.LessonStatus(lesson.subscriptionType),
                            Analytics.Event.Param.ExerciseId(exercise.name),
                        )
                    )
                    pbDownloadProgress.visibility = VISIBLE
                    viewModel.prepareExerciseArgs(lesson, exercise)
                }
            }

            ibClose.setOnClickListener {
                if (isComingFromDeeplink) {
                    findNavController().navigate(ExerciseLauncherFragmentDirections.toLessonFragment(lesson.id))
                } else
                    findNavController().popBackStack(R.id.lessonFragment, false)
            }
        }
    }

    private fun onBoostedScore(exerciseScore: ExerciseScore) {
        Timber.d("onBoostedScore: $exerciseScore")
        Analytics.logEvent(
            Analytics.Event.ExerciseCompleteEvent(
                Analytics.Event.Param.LessonId(lesson!!.title),
                Analytics.Event.Param.LessonOwner(lesson!!.teacher.name),
                Analytics.Event.Param.LessonCategory(lesson!!.type),
                Analytics.Event.Param.LessonStatus(lesson!!.subscriptionType),
                Analytics.Event.Param.ExerciseId(exercise!!.name),
                Analytics.Event.Param.TuneScore(exerciseScore.tuneScore.roundToInt()),
                Analytics.Event.Param.TimeScore(exerciseScore.timingScore.roundToInt())
            )

        )
        findNavController().navigate(
            ExerciseLauncherFragmentDirections.toExerciseResultFrag(exercise!!, lesson!!, exerciseScore)
        )
    }

    private fun onExerciseFromId(exerciseFromId: ExerciseFromId) {
        Timber.d("exerciseFromId lessonId: ${exerciseFromId.lessonId} exercise list size: ${exerciseFromId.exercise.size}")
        viewModel.getLesson(exerciseFromId.lessonId)
        binding.pbDownloadProgress.visibility = VISIBLE
    }

    private fun onLessonFromId(lessonFromId: Lesson) {
        Timber.d("lessonId: ${lessonFromId.id}")
        lesson = lessonFromId
        exercise = lesson.exercises.find { it.id == args.exerciseId }!!
        binding.tvTitle.text = exercise.name
        binding.tvDescription.text = exercise.subtitle
        binding.ibStart.visibility = VISIBLE
        binding.pbDownloadProgress.visibility = GONE
    }

    private fun startExerciseInterface(exerciseArgs: PrepareExerciseArgsUseCase.ExerciseArgs) {
        Timber.d("startSingingInterface: $exerciseArgs")

        Timber.d("starting singing interface for contentPaths: $exerciseArgs")
        val intentBuilder = exerciseArgs.let {
            Doorway.getExerciseIntentBuilder()
                .setMediaPath(it.mediaFile.absolutePath)
                .setMetadataPath(it.metadataFile.absolutePath)
                .setSongId(it.exercise.id)
        }
        getSingResult.launch(intentBuilder.build(context))
    }

    private val getSingResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        Timber.d("getSingResult: result")

        var skiResultCode: Int? = null
        if (Activity.RESULT_OK == it.resultCode) {
            it.data?.apply {
                skiResultCode = getIntExtra(Doorway.KEY_EXTRA_SKI_RESULT_CODE, Doorway.SKI_RESULT_CODE_UNKNOWN_ERROR)
                val isContentExpiryWarning = getBooleanExtra(Doorway.KEY_EXTRA_BOOLEAN_IS_CONTENT_EXPIRY_WARNING, false)
                Timber.d("onActivityResult: isContentExpiryWarning=$isContentExpiryWarning")

                if (Doorway.SKI_RESULT_CODE_SING_COMPLETE == skiResultCode) {
                    val songDurationMS = getIntExtra(Doorway.KEY_EXTRA_INT_SONG_DURATION_MILLISEC, -1)
                    val totalRecDurationMS = getIntExtra(Doorway.KEY_EXTRA_INT_TOTAL_RECORDING_DURATION_MILLISEC, -1)
                    val singableRecDurationMS = getIntExtra(Doorway.KEY_EXTRA_INT_SINGABLE_RECORDING_DURATION_MILLISEC, -1)

                    val tuneScore = getFloatExtra(Doorway.KEY_EXTRA_FLOAT_TUNE_SCORE, Float.NaN) * 100f
                    val timingScore = getFloatExtra(Doorway.KEY_EXTRA_FLOAT_TIMING_SCORE, Float.NaN) * 100f

                    val reviewData = getStringExtra(Doorway.KEY_EXTRA_STRING_REVIEW_DATA) ?: ""

                    val exerciseScore = ExerciseScore(
                        exerciseId = exercise.id,
                        songDurationMS = songDurationMS,
                        totalRecDurationMS = totalRecDurationMS,
                        singableRecDurationMS = singableRecDurationMS,
                        totalScore = (tuneScore + timingScore) * 0.5f,
                        tuneScore = tuneScore,
                        timingScore = timingScore,
                        reviewData = reviewData
                    )
                    Timber.d("getSingResult : exerciseScore $exerciseScore")
                    viewModel.computeBoostedScore(exerciseScore)
                    return@registerForActivityResult
                }
            }
        }
        var message = "Something went wrong!, please try again"
//        if (null != skiResultCode) message += " Error code [$skiResultCode]"
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    }

    private val fakeSinging: () -> Unit = {
        val tempFile = File(requireContext().filesDir, "temp")
        if (!tempFile.exists()) {
            FileWriter(tempFile).use { it.write("temp") }
        }
        viewModel.computeBoostedScore(
            ExerciseScore(
                exerciseId = exercise.id,
                songDurationMS = -1,
                totalRecDurationMS = -1,
                singableRecDurationMS = -1,
                totalScore = 90f,
                tuneScore = 100f,
                timingScore = 80f,
                reviewData = "{}"
            )
        )
    }
}