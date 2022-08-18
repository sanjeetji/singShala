package com.sensibol.lucidmusic.singstr.gui.app.learn.exercise

import android.animation.ObjectAnimator
import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.animation.doOnEnd
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd.*
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.karaoke.Doorway
import com.sensibol.lucidmusic.singstr.domain.model.ExerciseScore
import com.sensibol.lucidmusic.singstr.domain.model.ProSubscription
import com.sensibol.lucidmusic.singstr.gui.BuildConfig
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentExerciseResultBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.usecase.PrepareExerciseArgsUseCase
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt

@AndroidEntryPoint
class ExerciseResultFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_exercise_result
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentExerciseResultBinding::inflate
    override val binding: FragmentExerciseResultBinding get() = super.binding as FragmentExerciseResultBinding

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private val viewModel: ExerciseResultViewModel by viewModels()

    private val args: ExerciseResultFragmentArgs by navArgs()

    private lateinit var exerciseArgs: PrepareExerciseArgsUseCase.ExerciseArgs

    private var mInterstitialAd: AdManagerInterstitialAd? = null

    private var isProUser = false

    private val goodScoreMsgList = listOf(
        "Great work!", "Amazing job!", "You've got it!", "Nailing it!", "Kahaan the Ustaad?",
        "What a star!", "Kya baat hai!", "Hot damn!", "Yeh hui na baat!", "Sundar. Ati sundar!"
    )

    private val averageScoreMsgList = listOf(
        "Not bad, buddy.", "Pretty good!", "Good effort!", "Almost there!", "Getting there!",
        "Chalega!", "So close!", "Good Try!", "Nice!", "Not bad, ji!"
    )

    private val badScoreMsgList = listOf(
        "Yikes!", "One more go?", "Not quite there yet.", "Needs more work!", "Haule haule jo jayenge better!",
        "That wasn't easy.", "Persistence is key", "Koi baat nehi.", "Oops! Try again!", "Oh no :("
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(requireContext()) { initializationStatus ->
            Timber.d("CHECK this Ad AdMob Sdk Initialize $initializationStatus")
        }
        loadInterstitialAd()
    }

    override fun onInitView() {
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(exerciseArgs, ::startExerciseInterface)
            observe(boostedExerciseScore, ::onBoostedScore)
            submitExerciseScore(args.exerciseScore)
        }

        singstrViewModel.apply {
            failure(failure, ::handleFailure)
            observe(userSubscription, ::setUserSubscription)
            getUserSubscription()
        }

        binding.apply {
            tvFinalScore.visibility = GONE
            tvEarnedXps.visibility = GONE
            val totalScore = args.exerciseScore.totalScore.roundToInt()
            val scoreAnimator = ObjectAnimator.ofInt(pbFinalScore, "progress", totalScore)
            scoreAnimator.apply {
                duration = 700
                addListener(
                    doOnEnd {
                        tvFinalScore.visibility = VISIBLE
                        tvEarnedXps.visibility = VISIBLE
                    }
                )
            }
            scoreAnimator.start()
//            pbFinalScore.progress = totalScore
            tvExerciseTitle.text = args.exercise.name
            tvFinalScore.text = totalScore.toString()

            val earnedXpText = SpannableStringBuilder(
                "You earned +$totalScore XP"
            )
            earnedXpText.setSpan(
                ForegroundColorSpan(Color.parseColor("#5DB427")),
                11,
                earnedXpText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            tvEarnedXps.text = earnedXpText

            tvFinalScoreMsg.text = getFinalScoreMsg(totalScore)

            tvBtnPracticeAgain.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.ExerciseReAttemptEvent(
                        Analytics.Event.Param.LessonId(args.lesson.id),
                        Analytics.Event.Param.LessonOwner(args.lesson.teacher.name),
                        Analytics.Event.Param.LessonCategory(args.lesson.type),
                        Analytics.Event.Param.LessonStatus(args.lesson.subscriptionType),
                        Analytics.Event.Param.ExerciseId(args.exercise.name),
                    )
                )

                if (isProUser)
                    startPracticeAgain()
                else
                    showInterstitialAd()
            }

            ibClose.setOnClickListener {
                if (args.exerciseLaunchFromDeeplink)
                    findNavController().navigate(ExerciseResultFragmentDirections.toLessonFragment(args.lesson.id))
                else
                    findNavController().popBackStack(R.id.lessonFragment, false)
            }

            tvBtnFinish.setOnClickListener {
                if (args.exerciseLaunchFromDeeplink)
                    findNavController().navigate(ExerciseResultFragmentDirections.toLessonFragment(args.lesson.id))
                else
                    findNavController().popBackStack(R.id.lessonFragment, false)
            }
        }
    }

    private fun startExerciseInterface(exerciseArgs: PrepareExerciseArgsUseCase.ExerciseArgs) {
        Timber.d("startSingingInterface: $exerciseArgs")

        this.exerciseArgs = exerciseArgs
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
                        exerciseId = args.exercise.id,
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
//                    val totalScore = getFloatExtra(Doorway.KEY_EXTRA_FLOAT_TOTAL_SCORE, Float.NaN) * 100f
//                    val tuneScore = getFloatExtra(Doorway.KEY_EXTRA_FLOAT_TUNE_SCORE, Float.NaN) * 100f
//                    val timingScore = getFloatExtra(Doorway.KEY_EXTRA_FLOAT_TIMING_SCORE, Float.NaN) * 100f
//                    val expressionScore = getFloatExtra(Doorway.KEY_EXTRA_FLOAT_EXPRESSION_SCORE, Float.NaN) * 100f
//                    val lessonScore = getFloatExtra(Doorway.KEY_EXTRA_FLOAT_LESSON_SCORE, Float.NaN) * 100f
//
//                    // FIXME - send actual BTPE of user
//                    findNavController().navigate(
//                        R.id.exerciseResultFrag,
//                        ExerciseLauncherFragmentArgs(args.exercise, args.lesson).toBundle(),
//                        NavOptions.Builder().setPopUpTo(R.id.exerciseResultFrag, true).build()
//                    )
//                    return@registerForActivityResult
                }
            }
        }
        var message = "Singing Cancelled"
//        if (null != skiResultCode) message += " Error code [$skiResultCode]"
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    }

    private fun onBoostedScore(exerciseScore: ExerciseScore) {
        Timber.d("onBoostedScore: $exerciseScore")
        Analytics.logEvent(
            Analytics.Event.ExerciseCompleteEvent(
                Analytics.Event.Param.LessonId(args.lesson.title),
                Analytics.Event.Param.LessonOwner(args.lesson.teacher.name),
                Analytics.Event.Param.LessonCategory(args.lesson.type),
                Analytics.Event.Param.LessonStatus(args.lesson.subscriptionType),
                Analytics.Event.Param.ExerciseId(args.exercise.name),
                Analytics.Event.Param.TuneScore(exerciseScore.tuneScore.roundToInt()),
                Analytics.Event.Param.TimeScore(exerciseScore.timingScore.roundToInt())
            )
        )
        findNavController().navigate(
            R.id.exerciseResultFrag,
            ExerciseResultFragmentArgs(args.exercise, args.lesson, exerciseScore).toBundle(),
            NavOptions.Builder().setPopUpTo(R.id.exerciseResultFrag, true).build()
        )
    }

    private fun setUserSubscription(proSubscription: ProSubscription) {
        isProUser = proSubscription.subscribed
    }

    private fun getFinalScoreMsg(totalScore: Int): String {
        val random = (0..9).random()
        return when {
            totalScore > 70 -> goodScoreMsgList[random]
            totalScore > 40 -> averageScoreMsgList[random]
            else -> badScoreMsgList[random]
        }
    }

    private fun startPracticeAgain() {
        viewModel.prepareExerciseArgs(args.lesson, args.exercise)
    }

    private fun loadInterstitialAd() {
        val adRequest = AdManagerAdRequest.Builder().build()

        load(
            requireContext(),
            BuildConfig.APP_INTERSTITIAL_AD_KEY,
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.d(adError.message)
                    mInterstitialAd = null
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"
                    Timber.d("Ad onAdFailedToLoad() with error $error")
                }

                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    mInterstitialAd = interstitialAd
                    Timber.d("CHECK this Ad was loaded.")
                }
            }
        )
    }

    private fun showInterstitialAd() {
        // Show the ad if it's ready. Otherwise log and start practice mode.
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null
                    Timber.d("Ad was dismissed.")
                    startPracticeAgain()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    mInterstitialAd = null
                    Timber.d("Ad failed to show.")
                }

                override fun onAdShowedFullScreenContent() {
                    Timber.d("Ad showed fullscreen content.")
                }
            }

            mInterstitialAd?.show(requireActivity())
        } else {
            Timber.d("Ad did not load")
            startPracticeAgain()
        }
    }
}