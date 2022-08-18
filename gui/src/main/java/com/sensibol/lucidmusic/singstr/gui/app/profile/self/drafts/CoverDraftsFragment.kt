package com.sensibol.lucidmusic.singstr.gui.app.profile.self.drafts


import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.PaginationScrollListener
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentCoverDraftsBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
internal class CoverDraftsFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_cover_drafts
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentCoverDraftsBinding::inflate
    override val binding get():FragmentCoverDraftsBinding = super.binding as FragmentCoverDraftsBinding
//
//    @Inject
//    internal lateinit var coverDraftsAdapter: CoverDraftsAdapter

    @Inject
    internal lateinit var nodeDraftsAdapter: NodeDraftsAdapter

    private val viewModel: CoverDraftsViewModel by viewModels()

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    private val pageStart: Int = 1
    private var xpEarned: Int = 0
    private var currentPage: Int = pageStart



    private lateinit var mUserId: String
    private lateinit var mSongMini: SongMini
    private lateinit var mDraft: NodeDraft
    private lateinit var btnAction: String
    private lateinit var mSimpleAnalysis: SimpleAnalysis

    override fun onInitView() {
        viewModel.apply {
            failure(failure, ::handleFailure)
//            observe(draftsPage, ::getCoverList)
            observe(songMini){
               mSongMini = it
            }
            observe(simpleAnalysis, ::onSimpleAnalysis)
            observe(nodeDraft, ::handleNodeDraft)
//            getDrafts()
        }
        binding.apply {

            ivBack.setOnClickListener {
                activity?.onBackPressed()
            }

            nodeDraftsAdapter.apply {
                onPublishClickListener = { draft ->
//                    // FIXME - fake sing score. To be supplied by the backend
//                    val random = Random(System.currentTimeMillis())
//                    val tuneScore = random.nextInt(35, 70)
//                    val timingScore = random.nextInt(35, 70)
//
//
                    mDraft = draft
                    btnAction = "publish"
                    viewModel.loadSimpleAnalysis(mDraft.attemptId, mDraft.songId)
                }

                onAnalyseClickListener = { draft ->
                    mDraft = draft
                    btnAction = "analysis"
                    viewModel.loadSimpleAnalysis(mDraft.attemptId, mDraft.songId)
                }

                onDeleteClickListener = { draft ->
                    mDraft = draft
                    btnAction = "delete"
                    viewModel.loadSimpleAnalysis(mDraft.attemptId, mDraft.songId)
                }

            }

            rvDraftsList.apply {
                adapter = nodeDraftsAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }

//            rvDraftsList.addOnScrollListener(object : PaginationScrollListener(binding.rvDraftsList.layoutManager as LinearLayoutManager) {
//                override fun loadMoreItems() {
//                    isLoading = true
//                    currentPage += 1
//
//                    Handler(Looper.myLooper()!!).postDelayed({
//                        loadNextPage()
//                    }, 1000)
//                }
//
//                override fun getTotalPageCount(): Int {
//                    TODO("Not yet implemented")
//                }
//
//                override fun isLastPage(): Boolean {
//                    return isLastPage
//                }
//
//                override fun isLoading(): Boolean {
//                    return isLoading
//                }
//
//            })

        }
        singstrViewModel.apply {
            failure(failure, ::handleFailure)
//            observe(userStats, ::showUserStats)
            observe(user){
                mUserId = it.id
                viewModel.loadNodeDraft(it.id)
            }
            loadUserStats()
        }
    }

    private fun showUserStats(userStats: UserStats) {
        Timber.d("showUserStats: $userStats")
        binding.apply {
            xpEarned = userStats.pendingXP
            var draftsCount: Int = userStats.draftsCount
            var xpEarnedMsg = "Publish all " + if (draftsCount > 0) {
                draftsCount
            } else {
                0
            } + " covers and gain "
            val greenSpanStartPosition = xpEarnedMsg.length
            xpEarnedMsg += "+" + userStats.pendingXP + " XP Instantly"
            with(SpannableString(xpEarnedMsg)) {
                val green = ForegroundColorSpan(Color.GREEN)
                setSpan(green, greenSpanStartPosition, xpEarnedMsg.length - 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                binding.tvXPEarned.text = this
            }
        }
    }

    private fun onSimpleAnalysis(simpleAnalysis: SimpleAnalysis) {
        this.mSimpleAnalysis = simpleAnalysis
        when(btnAction){
            "publish"->{
                performPublishEvent()
            }
            "analysis"->{
                performAnalysisEvent()
            }
            "delete"->{
                performDeleteEvent()
            }
        }
        btnAction = ""
    }

    private fun performPublishEvent(){
        val singScore = SingScore(
            mDraft.songId,
            90_000,
            90_000,
            60_000,
            mSimpleAnalysis.totalScore.toFloat(),
            mSimpleAnalysis.tuneScore.toFloat(),
            mSimpleAnalysis.timeScore.toFloat(),
            "unused",
            "unused",
            "ununsed",
            "ununsed"
        )
        Analytics.logEvent(
            Analytics.Event.DraftPublishEvent(
                Analytics.Event.Param.GenreId("NA"),
                Analytics.Event.Param.ArtistId("NA"),
                Analytics.Event.Param.SongId(mDraft.songId),
                Analytics.Event.Param.TotalScore(mDraft.totalScore),
                Analytics.Event.Param.TuneScore(mSimpleAnalysis.tuneScore),
                Analytics.Event.Param.TimeScore(mSimpleAnalysis.timeScore),
                Analytics.Event.Param.XPGain(mDraft.totalScore),
                Analytics.Event.Param.RightLines(mSimpleAnalysis.linesDonePerfectly),
                Analytics.Event.Param.WrongLines(mSimpleAnalysis.linesNeedsImprovement)
            )
        )

        findNavController().navigate(
            CoverDraftsFragmentDirections.toCoverPreviewFragment(mSongMini, singScore, mDraft.attemptId)
        )
    }

    private fun performAnalysisEvent(){
        Analytics.logEvent(
            Analytics.Event.DraftAnalyseEvent(
                Analytics.Event.Param.GenreId("NA"),
                Analytics.Event.Param.ArtistId(mSongMini.artists.toString()),
                Analytics.Event.Param.SongId(mSongMini.title),
                Analytics.Event.Param.TotalScore(mDraft.totalScore),
                Analytics.Event.Param.TuneScore(mSimpleAnalysis.tuneScore),
                Analytics.Event.Param.TimeScore(mSimpleAnalysis.timeScore),
                Analytics.Event.Param.XPGain(xpEarned),
                Analytics.Event.Param.RightLines(mSimpleAnalysis.linesDonePerfectly),
                Analytics.Event.Param.WrongLines(mSimpleAnalysis.linesNeedsImprovement)
            )
        )
        findNavController().navigate(CoverDraftsFragmentDirections.toDetailAnalysisFragment(mDraft.attemptId, mUserId))
    }

    private fun performDeleteEvent(){
        Analytics.logEvent(
            Analytics.Event.DraftDeleteEvent(
                Analytics.Event.Param.GenreId("NA"),
                Analytics.Event.Param.ArtistId(mSongMini.artists.toString()),
                Analytics.Event.Param.SongId(mSongMini.title),
                Analytics.Event.Param.TotalScore(mDraft.totalScore),
                Analytics.Event.Param.TuneScore(mSimpleAnalysis.tuneScore),
                Analytics.Event.Param.TimeScore(mSimpleAnalysis.timeScore),
                Analytics.Event.Param.XPGain(xpEarned),
            )
        )
        viewModel.deleteDraft(mDraft.attemptId)
    }

    fun loadNextPage() {
        viewModel.getDrafts()
        nodeDraftsAdapter.notifyDataSetChanged()
    }

    private fun getCoverList(draftsPage: DraftsPage) {
//        nodeDraftsAdapter.drafts = draftsPage.drafts
//        coverDraftsAdapter.notifyDataSetChanged()

        binding.apply {
            progressBar.visibility = GONE
            var totalXp = 0
            draftsPage.drafts.forEach {
                totalXp += it.totalXP.toInt()
            }
            xpEarned = totalXp
            var draftsCount: Int = draftsPage.drafts.size
            var xpEarnedMsg = "Publish all " + if (draftsCount > 0) {
                draftsCount
            } else {
                0
            } + " covers and gain "
            val greenSpanStartPosition = xpEarnedMsg.length
            xpEarnedMsg += "+" + totalXp + " XP Instantly"
            with(SpannableString(xpEarnedMsg)) {
                val green = ForegroundColorSpan(Color.GREEN)
                setSpan(green, greenSpanStartPosition, xpEarnedMsg.length - 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                binding.tvXPEarned.text = this
            }
        }
    }

    private fun handleNodeDraft(nodeDrafts: List<NodeDraft>){
        nodeDraftsAdapter.drafts = nodeDrafts
        nodeDraftsAdapter.notifyDataSetChanged()

        binding.apply {
            progressBar.visibility = GONE
            var totalXp = 0
            nodeDrafts.forEach {
                totalXp += it.totalScore
            }
            xpEarned = totalXp
            var draftsCount: Int = nodeDrafts.size
            var xpEarnedMsg = "Publish all " + if (draftsCount > 0) {
                draftsCount
            } else {
                0
            } + " covers and gain "
            val greenSpanStartPosition = xpEarnedMsg.length
            xpEarnedMsg += "+" + totalXp + " XP Instantly"
            with(SpannableString(xpEarnedMsg)) {
                val green = ForegroundColorSpan(Color.GREEN)
                setSpan(green, greenSpanStartPosition, xpEarnedMsg.length - 9, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
                binding.tvXPEarned.text = this
            }
        }
    }
}