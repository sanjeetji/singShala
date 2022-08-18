package com.sensibol.lucidmusic.singstr.gui.app.analysis

import android.graphics.Color
import android.media.MediaPlayer
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.convertDatePatternDetailAnalysis
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentDetailedAnalysisBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class DetailedAnalysisFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_detailed_analysis
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentDetailedAnalysisBinding::inflate
    override val binding: FragmentDetailedAnalysisBinding get() = super.binding as FragmentDetailedAnalysisBinding

    @Inject
    lateinit var detailedCoupletReviewAdapter: DetailedCoupletReviewAdapter

    @Inject
    lateinit var simpleCoupletReviewAdapter: SimpleCoupletReviewAdapter

    private val viewModel: AnalysisViewModel by viewModels()

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private val args: DetailedAnalysisFragmentArgs by navArgs()

    //    private var previewMediaPlayer: MediaPlayer? = null
    private var correctMediaPlayer: MediaPlayer? = null
    private var list: List<DetailedCoupletReview>? = null
    private var isUserPro: Boolean = false
    private lateinit var simpleAnalysis: SimpleAnalysis

    override fun onInitView() {
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(simpleAnalysis, ::showSimpleAnalysisData)
            observe(detailAnalysis, ::showDetailAnalysisData)
            observe(userReviewAccount, ::showUserReviewAccount)
            observe(previewUrl, ::setupCorrectionMedia)
            observe(feedVideoUrl, ::setupUserMedia)
            loadSimpleAnalysis(args.attemptId)
        }

        singstrViewModel.apply {
            failure(failure, ::handleFailure)
            observe(userSubscription, ::showUserSubscription)
            getUserSubscription()
        }

        Timber.e("=============== Song id iss ::"+args.attemptId)

        if (args.userMediaFilePath.isNullOrEmpty()) viewModel.getFeedVideoUrl(args.attemptId)
        else binding.vvReviewVideo.setVideoPath(args.userMediaFilePath)

        binding.apply {

            rvMiniDetails.apply {
                isNestedScrollingEnabled = false
                layoutManager = LinearLayoutManager(context)
                adapter = simpleCoupletReviewAdapter
            }

            rvDetailList.apply {
                isNestedScrollingEnabled = false
                layoutManager = LinearLayoutManager(context);
                adapter = detailedCoupletReviewAdapter
            }

            rgReview.setOnCheckedChangeListener { _, id ->
                filterReview(
                    list!!, review = when (id) {
                        rbGood.id -> "good"
                        rbAvg.id -> "average"
                        rbPoor.id -> "bad"
                        else -> "All"
                    }
                )

            }

            tvGoToHomePage.setOnClickListener {
                findNavController().navigate(DetailedAnalysisFragmentDirections.toHomeFragment())
            }
            ivBack.setOnClickListener {
                clSimpleAnalysis.visibility = VISIBLE
                ivClose.visibility = VISIBLE
                tvCountLeft.visibility = VISIBLE
                tvTimeWatched.visibility = VISIBLE
                tvXPMessage.visibility = VISIBLE

                ivBack.visibility = INVISIBLE
                llcDetailList.visibility = GONE
                llcSimpleAnalysis.alpha = 1f
            }
            ivClose.setOnClickListener {
                findNavController().popBackStack()
//                findNavController().navigateUp()
            }

            detailedCoupletReviewAdapter.onPreviewClickListener = { tvPreview: TextView, startTimePosition: Int, endTimePosition: Int ->
                if (correctMediaPlayer?.isPlaying!!) {
                    correctMediaPlayer?.pause()
                }
                if (vvReviewVideo.isPlaying) {
                    vvReviewVideo.pause()
                    tvPreview.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_small, 0, 0, 0)
                } else {
                    vvReviewVideo.seekTo(startTimePosition)
                    vvReviewVideo.start()
                }
                Analytics.logEvent(
                    Analytics.Event.DetailedAnalysisInteractionEvent(
                        Analytics.Event.Param.AnalysisClick("preview"),
                        Analytics.Event.Param.RightLines(simpleAnalysis.linesDonePerfectly),
                        Analytics.Event.Param.WrongLines(simpleAnalysis.linesNeedsImprovement),
                        Analytics.Event.Param.CoverId(args.attemptId)
                    )
                )
            }

            detailedCoupletReviewAdapter.onCorrectionClickListener = { tvCorrection: TextView, startTimePosition: Int, endTimePosition: Int ->
                if (vvReviewVideo.isPlaying) {
                    vvReviewVideo.pause()
                }
                if (correctMediaPlayer?.isPlaying!!) {
                    correctMediaPlayer?.pause()
                    tvCorrection.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_small, 0, 0, 0)
                } else {
                    correctMediaPlayer?.seekTo(startTimePosition)
                    correctMediaPlayer?.start()
                }
                Analytics.logEvent(
                    Analytics.Event.DetailedAnalysisInteractionEvent(
                        Analytics.Event.Param.AnalysisClick("correction"),
                        Analytics.Event.Param.RightLines(simpleAnalysis.linesDonePerfectly),
                        Analytics.Event.Param.WrongLines(simpleAnalysis.linesNeedsImprovement),
                        Analytics.Event.Param.CoverId(args.attemptId)
                    )
                )
            }

            tvBecomePro.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.CheckProDetailsEvent(
                        Analytics.Event.Param.ProUserId("NA")
                    )
                )
                findNavController().navigate(DetailedAnalysisFragmentDirections.toProSubscriptionPlanFragment())
            }
            binding.tvViewFullDetail.setOnClickListener {
                llcSimpleAnalysis.alpha = 0.25f
                pbLoading.visibility = VISIBLE
                viewModel.loadDetailAnalysis(args.attemptId)
            }
        }
    }

    private fun setupUserMedia(url: String) {
        binding.apply {
            vvReviewVideo.seekTo(2000)
            vvReviewVideo.setVideoPath(url)
            vvReviewVideo.setOnPreparedListener { mediaPlayer ->

            }
        }
    }

    private fun setupCorrectionMedia(url: String) {
        correctMediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                // TODO - enable UI only after MediaPlayer is prepared
            }
            prepareAsync()
        }
    }

    private fun showDetailAnalysisData(detailedAnalysis: DetailedAnalysis) {
        binding.apply {
            clSimpleAnalysis.visibility = GONE
            llcDetailList.visibility = VISIBLE
            pbLoading.visibility = GONE

            if (isUserPro) {
                ivClose.visibility = GONE
                ivBack.visibility = VISIBLE
                tvXPMessage.visibility = GONE
                tvTimeWatched.visibility = GONE
                binding.tvCountLeft.visibility = GONE
            }
            detailedCoupletReviewAdapter.songReview = detailedAnalysis.detailedCoupletReviews
            list = detailedAnalysis.detailedCoupletReviews
        }
    }

    private fun showUserReviewAccount(userReviewAccount: UserReviewAccount) {
        if (userReviewAccount.lineReviews > 0) {
            binding.tvCountLeft.visibility = VISIBLE
            binding.tvViewFullDetail.visibility = VISIBLE
        } else {
            binding.tvCountLeft.visibility = GONE
            binding.llcProDialogue.visibility = VISIBLE
            binding.tvViewFullDetail.visibility = GONE
        }
        binding.tvCountLeft.text = "${userReviewAccount.lineReviews} Left"
    }

    private fun showUserSubscription(proSubscription: ProSubscription) {
        isUserPro = proSubscription.subscribed
        if (proSubscription.subscribed) {
            binding.apply {
                tvViewFullDetail.visibility = VISIBLE
                llcProDialogue.visibility = GONE
            }
        } else {
            viewModel.getUserReviewAccount(args.userId)
        }
    }

    private fun showSimpleAnalysisData(simpleAnalysis: SimpleAnalysis) {
        viewModel.getSongPreviewUrl(simpleAnalysis.songMini.id)
        this.simpleAnalysis = simpleAnalysis
        binding.apply {
            Analytics.logEvent(
                Analytics.Event.DetailedAnalysisEvent(
                    Analytics.Event.Param.SongId(simpleAnalysis.songMini.title),
                    Analytics.Event.Param.GenreId("NA"),
                    Analytics.Event.Param.ArtistId(simpleAnalysis.songMini.artists.names),
                    Analytics.Event.Param.TotalScore(simpleAnalysis.totalScore),
                    Analytics.Event.Param.TuneScore(simpleAnalysis.tuneScore),
                    Analytics.Event.Param.TimeScore(simpleAnalysis.timeScore),
                    Analytics.Event.Param.RightLines(simpleAnalysis.linesDonePerfectly),
                    Analytics.Event.Param.WrongLines(simpleAnalysis.linesNeedsImprovement)
                )
            )

            tvTitle.text = simpleAnalysis.songMini.title
            songType.text = simpleAnalysis.songMini.difficulty
            ivSongThumbnail.loadUrl(simpleAnalysis.songMini.thumbnailUrl)
            llcDetailList.visibility = GONE
            clSimpleAnalysis.visibility = VISIBLE
            tvXPMessage.visibility = VISIBLE
            tvTimeWatched.visibility = VISIBLE
            tvRecordDate.text = convertDatePatternDetailAnalysis(simpleAnalysis.recordedOn)
            val xpEarned = "${simpleAnalysis.attemptXp} XP Earned From This Cover"
            tvXPMessage.text = xpEarned
            val viewsCount =
                "Your cover was viewed for more than \n ${if (simpleAnalysis.viewsCount > 1) "${simpleAnalysis.viewsCount} minutes" else "${simpleAnalysis.viewsCount} minute"}"
            tvTimeWatched.text = viewsCount
            tvFinalScore.text = simpleAnalysis.totalScore.toString()
            tvTimeScore.text = simpleAnalysis.timeScore.toString()
            tvTuneScore.text = simpleAnalysis.tuneScore.toString()
            simpleCoupletReviewAdapter.simpleCoupletReview = simpleAnalysis.simpleCoupletReviews
            tvSongArtist.text = simpleAnalysis.songMini.artists.names

            when (simpleAnalysis.songMini.difficulty.toUpperCase(Locale.ROOT)) {
                Song.Difficulty.EASY.name -> {
                    ivDifficultyLevel.setImageResource(R.drawable.ic_easy)
                }
                Song.Difficulty.MEDIUM.name -> {
                    ivDifficultyLevel.setImageResource(R.drawable.ic_medium)
                }
                Song.Difficulty.HARD.name -> {
                    ivDifficultyLevel.setImageResource(R.drawable.ic_hard)
                }
                else -> { // Note the block
                    Timber.e("Unknown difficulty ${simpleAnalysis.songMini.difficulty} for song ${simpleAnalysis.songMini.lyrics}")
                }
            }

            val firstText =
                SpannableStringBuilder(
                    "You got ${if (simpleAnalysis.linesDonePerfectly > 1) "${simpleAnalysis.linesDonePerfectly} lines" else "${simpleAnalysis.linesDonePerfectly} line"} perfectly and " +
                            "\n${if (simpleAnalysis.linesNeedsImprovement > 1) "${simpleAnalysis.linesNeedsImprovement} lines" else "${simpleAnalysis.linesNeedsImprovement} line"} need improvement"
                )

            if (simpleAnalysis.linesDonePerfectly > 1) {
                firstText.setSpan(
                    ForegroundColorSpan(Color.parseColor("#5DB427")),
                    8,
                    16,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                firstText.setSpan(
                    ForegroundColorSpan(Color.parseColor("#5DB427")),
                    8,
                    15,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if (simpleAnalysis.linesNeedsImprovement > 1) {
                firstText.setSpan(
                    ForegroundColorSpan(Color.parseColor("#CCB012")),
                    30,
                    39,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                firstText.setSpan(
                    ForegroundColorSpan(Color.parseColor("#CCB012")),
                    30,
                    38,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            tvSimpleMsg.text = firstText
        }
    }

    private fun filterReview(reviewList: List<DetailedCoupletReview>, review: String) {
        Analytics.logEvent(
            Analytics.Event.FilterDetailedAnalysisEvent(
                Analytics.Event.Param.SongId(simpleAnalysis.songMini.title),
                Analytics.Event.Param.GenreId("NA"),
                Analytics.Event.Param.ArtistId(simpleAnalysis.songMini.artists.names),
                Analytics.Event.Param.CoverId(args.attemptId),
                Analytics.Event.Param.FilterState(review),
                Analytics.Event.Param.TotalScore(simpleAnalysis.totalScore),
                Analytics.Event.Param.TuneScore(simpleAnalysis.tuneScore),
                Analytics.Event.Param.TimeScore(simpleAnalysis.timeScore)
            )
        )
        if (review == "All") {
            detailedCoupletReviewAdapter.songReview = reviewList
        } else {
            val list = reviewList.filter { it.remark == review }
            detailedCoupletReviewAdapter.songReview = list
        }
    }

    override fun onPause() {
        super.onPause()
        binding.vvReviewVideo.pause()
        correctMediaPlayer?.pause()
    }
}