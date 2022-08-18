package com.sensibol.lucidmusic.singstr.gui.app.sing.result.cover

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.CoverSubmitResult
import com.sensibol.lucidmusic.singstr.domain.model.SimpleAnalysis
import com.sensibol.lucidmusic.singstr.domain.model.names
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.util.ProgressBarAnimation
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentCoverResultBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.isFakeSinging
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.math.roundToInt


@AndroidEntryPoint
class CoverResultFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_cover_result
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentCoverResultBinding::inflate
    override val binding: FragmentCoverResultBinding get() = super.binding as FragmentCoverResultBinding

    private val args: CoverResultFragmentArgs by navArgs()

    private val viewModel: CoverResultViewModel by viewModels()

    private var averageScore : Int = 0

    private lateinit var simpleAnalysis: SimpleAnalysis

    override fun onInitView() {
        binding.apply {
            llFinalScore.visibility = GONE
            llDifficultyMultiplier.visibility = INVISIBLE
            rlXpEarnedBg.visibility = GONE
            vXpEarnedBgLarge.visibility = GONE
            tvXpCreditMessage.visibility = GONE
            btnShareScore.visibility = GONE
            tvWhyScoreLow.visibility = GONE
            btnNext.visibility = GONE
            tvXpMultiplierMessage.visibility = GONE

            val tuneScore = args.singScore.tuneScore.roundToInt()
            val timingScore = args.singScore.timingScore.roundToInt()
//            val averageScore = ((tuneScore + timingScore) * 0.5).roundToInt()
            averageScore = args.singScore.totalScore.roundToInt()

            pbTiming.startAnimation(
                ProgressBarAnimation(pbTiming, 0, timingScore).apply {
                    duration = 1500
                    interpolator = DecelerateInterpolator()
                }
            )

            pbTuning.startAnimation(
                ProgressBarAnimation(pbTuning, 0, tuneScore).apply {
                    duration = 1500
                    interpolator = DecelerateInterpolator()
                }
            )

            tvTimingScore.text = timingScore.toString()
            tvTuningScore.text = tuneScore.toString()

            pbFinal.progress = timingScore
            tvFinalScore.text = averageScore.toString()

            if (isFakeSinging) {
                btnNext.apply {
                    visibility = VISIBLE
                    setOnClickListener {
                        findNavController()
                            .navigate(
                                CoverResultFragmentDirections.toCoverProcessFragment(
                                    args.songMini,
                                    args.singScore,
                                    "fakeAttemptId"
                                )
                            )
                    }
                }

            }

        }
        viewModel.apply {
            observe(failure, ::handleFailure)
            observe(coverSubmitResult, ::onCoverSubmitResult)
            observe(simpleAnalysis, ::onSimpleAnalysis)
            submitCoverScore(args.singScore, args.songMini)
        }
    }

    private fun onSimpleAnalysis(simpleAnalysis: SimpleAnalysis){
        this.simpleAnalysis = simpleAnalysis
    }

    private fun onCoverSubmitResult(coverSubmitResult: CoverSubmitResult) {
        Timber.d("onCoverSubmitResult: $coverSubmitResult")

        Analytics.logEvent(
            Analytics.Event.CompletedCoverEvent(
                Analytics.Event.Param.GenreId("NA"),
                Analytics.Event.Param.ArtistId(args.songMini.artists.names),
                Analytics.Event.Param.SongId(args.songMini.title),
                Analytics.Event.Param.TotalScore(averageScore),
                Analytics.Event.Param.TuneScore(args.singScore.tuneScore.roundToInt()),
                Analytics.Event.Param.TimeScore(args.singScore.timingScore.roundToInt()),
                Analytics.Event.Param.RightLines(simpleAnalysis.linesDonePerfectly),
                Analytics.Event.Param.WrongLines(simpleAnalysis.linesNeedsImprovement)
            )
        )

        binding.apply {
            btnNext.alpha = 0.0f
            btnNext.visibility = VISIBLE
            btnNext.animate().alpha(1.0f)
            btnNext.setOnClickListener {
                btnNext.visibility = INVISIBLE
                showFinalScore(coverSubmitResult)
            }
        }
    }

    private fun showFinalScore(coverSubmitResult: CoverSubmitResult) {

// TODO - move this call inside onInitView
        val tuneScore = args.singScore.tuneScore.roundToInt()
        val timingScore = args.singScore.timingScore.roundToInt()
//        val averageScore = ((tuneScore + timingScore) * 0.5).roundToInt()
        val averageScore = args.singScore.totalScore.roundToInt()

        binding.apply {
            llCommonScore.animate().alpha(0f)

            llFinalScore.visibility = VISIBLE
            llFinalScore.alpha = 0f
            llFinalScore.animate().alpha(1f)

            val duration = 1_200L

            rlXpEarnedBg.apply {
                visibility = VISIBLE
                translationY = 128f
                alpha = 0f
                scaleX = 0.65f
                scaleY = 0.65f
                animate()
                    .translationY(0f)
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(duration)
                    .withEndAction {

                        llDifficultyMultiplier.apply {

                            val xpEarned = when (args.songMini.difficulty.toLowerCase()) {
                                "hard" -> {
                                    tvX.text = "x3"
                                    tvDifficulty.text = "Hard"
                                    tvXpMultiplierMessage.apply {
                                        text = getString(R.string.your_xp_has_muliplied, "tripled")
                                        visibility = VISIBLE
                                    }
                                    averageScore * 3
                                }
                                "medium" -> {
                                    tvX.text = "x2"
                                    tvDifficulty.text = "Medium"
                                    tvXpMultiplierMessage.apply {
                                        text = getString(R.string.your_xp_has_muliplied, "doubled")
                                        visibility = VISIBLE
                                    }
                                    averageScore * 2
                                }
                                else -> {
                                    tvX.text = "x1"
                                    tvDifficulty.text = "Easy"
                                    tvXpMultiplierMessage.visibility = INVISIBLE
                                    averageScore
                                }
                            }

                            visibility = VISIBLE
                            alpha = 0f
                            scaleX = 2.5f
                            scaleY = 2.5f
                            animate()
                                .alpha(1f)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setInterpolator(BounceInterpolator())
                                .setDuration(duration)
                                .withEndAction {

                                    ValueAnimator.ofInt(averageScore, xpEarned).apply {
                                        interpolator = DecelerateInterpolator()
                                        setDuration(duration)
                                        addUpdateListener {
                                            tvXPEarned.text = it.animatedValue.toString()
                                        }
                                        start()
                                    }

                                    btnNext.alpha = 0.0f
                                    btnNext.visibility = VISIBLE
                                    btnNext.animate().alpha(1.0f)
                                    tvXpCreditMessage.visibility = VISIBLE
                                    btnShareScore.visibility = VISIBLE
                                    tvWhyScoreLow.visibility = VISIBLE

                                    Analytics.logEvent(
                                        Analytics.Event.EarnedXPEvent(
                                            Analytics.Event.Param.EarnXPValue((tvXPEarned.text.toString()).toInt()),
                                            Analytics.Event.Param.EarnXPAction("cover created")
                                        )
                                    )

                                    Analytics.facebookLogEvent(
                                        Analytics.Event.UnlockAchievementEvent(
                                            Analytics.Event.Param.EarnXPValue(tvXPEarned.text.toString().toInt()),
                                            Analytics.Event.Param.EarnXPAction("cover created")
                                        )
                                    )
                                    btnNext.setOnClickListener {
                                        findNavController()
                                            .navigate(
                                                CoverResultFragmentDirections.toCoverProcessFragment(
                                                    args.songMini,
                                                    args.singScore,
                                                    coverSubmitResult.attemptId
                                                )
                                            )
                                    }

                                    btnShareScore.setOnClickListener {
                                        findNavController().navigate(
                                            CoverResultFragmentDirections.toScoreCardFragment(
                                                args.songMini, args.singScore
                                            )
                                        )

                                    }

                                    tvWhyScoreLow.setOnClickListener {
                                        //TODO define shre login for score
                                        val totalEarnedXp = tvXPEarned.text.toString()
//                                        Toast.makeText(requireContext(), "My Score Low: $totalEarnedXp", Toast.LENGTH_LONG).show()
                                    }

                                }
                        }

                    }
                    .withStartAction {
                        ValueAnimator.ofInt(0, averageScore).apply {
                            interpolator = DecelerateInterpolator()
                            setDuration(duration)
                            addUpdateListener { tvXPEarned.text = it.animatedValue.toString() }
                            start()
                        }
                    }
            }

            vXpEarnedBgLarge.apply {
                visibility = VISIBLE
                translationY = 256f
                alpha = 0f
                scaleX = 0.65f
                scaleY = 0.65f
                animate()
                    .translationY(0f)
                    .alpha(0.4f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(duration)
            }
        }
    }
}