package com.sensibol.lucidmusic.singstr.gui.app.feed

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.*
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.bold
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.EventLogger
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.sensibol.android.base.displayName
import com.sensibol.android.base.gui.AppToast
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.android.base.pretty
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.*
import com.sensibol.lucidmusic.singstr.gui.BuildConfig
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentFeedBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.TileFeedPlayerBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Integer.max
import java.util.*
import javax.inject.Inject
import kotlin.math.min


// TODO -  move business logic to viewModel?

@AndroidEntryPoint
internal abstract class FeedFragment : BaseFragment() {

    override val layoutResId: Int get() = R.layout.fragment_feed
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentFeedBinding::inflate
    override val binding: FragmentFeedBinding get() = super.binding as FragmentFeedBinding

    private var songTitle: String = ""
    private var artist: String = ""
    private var userName: String = ""
    private var userDisplayName: String = ""
    private var coverViewCount = ""
    private var attemptId: String = ""
    private var userImgUrl: String = ""

    private val args: FeedFragmentArgs by navArgs()

    private val viewModel: FeedViewModel by viewModels()

    protected abstract val feedSlotsVM: FeedSlotsViewModel

    protected abstract val feedTypeId: String

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    @Inject
    internal lateinit var feedBottomSheetCommentAdapter: FeedBottomSheetCommentAdapter

    @Inject
    internal lateinit var feedSlotsAdapter: FeedSlotsAdapter

    private lateinit var feedSlotsPagesManager: FeedSlotPagesManager

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetEditCaptionBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetShareOptionBehavior: BottomSheetBehavior<ConstraintLayout>

    private lateinit var mCoverSlot: CoverSlot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        singstrViewModel.apply {
            loadUserProfile()
        }

        feedSlotsPagesManager = FeedSlotPagesManager(requireContext())
        feedSlotsVM.loadFeed(feedTypeId, args.tileIndex + 1)
    }

    open fun onFeedSlots(feedSlots: List<FeedSlot>) {
        Timber.d("onFeedSlots: ${feedSlots.size} feedSlots available")
        if (feedSlots.isNotEmpty()) {
            val isFirstRun: Boolean
//            val isFirstRun = feedSlotsAdapter.itemCount == 0
            feedSlotsAdapter.apply {
                if (feedSlotsVM.isSwitchedBtwFeedCategory) {
                    replaceFeedSlots(feedSlots)
                    feedSlotsPagesManager.onSlotSelected(0)
                    isFirstRun = true
                } else {
                    isFirstRun = feedSlotsAdapter.itemCount == 0
                    addFeedSlots(feedSlots)
                }
                feedSlotsVM.isSwitchedBtwFeedCategory = false
            }

            feedSlotsPagesManager.feedRefreshPosition = max(0, feedSlotsAdapter.itemCount - RELOAD_DISTANCE_FROM_END)
            if (isFirstRun) {
                feedSlotsAdapter.findItemIndexForCoverIndex(args.tileIndex).let { itemIndex ->
                    val startIndex = when {
                        itemIndex < 0 -> feedSlots.size - 1
                        feedSlots.size < itemIndex -> feedSlots.size - 1
                        else -> itemIndex
                    }
                    Timber.d("onInitView: setting current page to $startIndex")
                    binding.vpVideoPlayerPager.setCurrentItem(startIndex, false)
                }
            }
        }
    }

    open fun onFollowingFeedSlot(feedSlots: List<FeedSlot>) {
        Timber.d("onFeedSlots: ${feedSlots.size} feedSlots available")
        if (feedSlots.isNotEmpty()) {
            feedSlotsAdapter.apply {
                if (feedSlotsVM.isSwitchedBtwFeedCategory) {
                    this.replaceFeedSlots(feedSlots)
                    feedSlotsPagesManager.onSlotSelected(0)
                } else
                    this.addFeedSlots(feedSlots)
                feedSlotsVM.isSwitchedBtwFeedCategory = false
            }
            val isFirstRun = feedSlotsAdapter.itemCount == 0
            feedSlotsPagesManager.feedRefreshPosition = max(0, feedSlotsAdapter.itemCount - RELOAD_DISTANCE_FROM_END)
            if (isFirstRun) {
                feedSlotsAdapter.findItemIndexForCoverIndex(args.tileIndex).let { itemIndex ->
                    val startIndex = when {
                        itemIndex < 0 -> feedSlots.size - 1
                        feedSlots.size < itemIndex -> feedSlots.size - 1
                        else -> itemIndex
                    }
                    Timber.d("onInitView: setting current page to $startIndex")
                    binding.vpVideoPlayerPager.setCurrentItem(startIndex, false)
                }
            }
        }
    }

    private fun loadFeed() {
        feedSlotsVM.loadFeed(feedTypeId)
    }

    override fun onInitView() {

        feedSlotsVM.apply {
            observe(feedSlots) { onFeedSlots(it) }
            observe(followingFeedSlots) { onFollowingFeedSlot(it) }
        }
        singstrViewModel.apply {
            observe(user) { viewModel.loadFollowingUser(it.id) }
        }

        binding.apply {
            vpVideoPlayerPager.apply {
                adapter = feedSlotsAdapter
                offscreenPageLimit = 1
            }

            incBottomSheet.apply {
//                rvComments.apply {
//                    layoutManager = LinearLayoutManager(context)
//                    adapter = feedBottomSheetCommentAdapter
//                }
            }

            ibClap.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.ClapReactEvent(
                        Analytics.Event.Param.SongId(mCoverSlot.cover.songMini.id),
                        Analytics.Event.Param.ArtistId(mCoverSlot.cover.songMini.artists.toString()),
                        Analytics.Event.Param.UserId(mCoverSlot.cover.userMini.id),
                        Analytics.Event.Param.CoverId(mCoverSlot.cover.id)
                    )
                )
                handleClapClick()
            }

            ivBack.setOnClickListener {
                activity?.onBackPressed()
            }

            feedSlotsVM.selectedFeedCategory.apply {
                if (this == -1)
                    rgFeedType.check(R.id.rb_for_you)
                else
                    rgFeedType.check(this)
            }

            rbFollowing.setOnClickListener {
                feedSlotsVM.isSwitchedBtwFeedCategory = true
                feedSlotsVM.selectedFeedCategory = it.id
                feedSlotsVM.loadFollowingFeed()
            }

            rbForYou.setOnClickListener {
                feedSlotsVM.isSwitchedBtwFeedCategory = true
                feedSlotsVM.selectedFeedCategory = it.id
                feedSlotsVM.loadFeed(feedTypeId, args.tileIndex + 1)
            }

            bottomSheetBehavior = BottomSheetBehavior.from(incBottomSheet.clRoot)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            bottomSheetEditCaptionBehavior = BottomSheetBehavior.from(incEditcap.clRoot)
            bottomSheetEditCaptionBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            bottomSheetShareOptionBehavior = BottomSheetBehavior.from(incShareOptions.clRoot)
            bottomSheetShareOptionBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            vpVideoPlayerPager.registerOnPageChangeCallback(pageChangeListener)


            ivBack.setOnClickListener {
                activity?.onBackPressed()
            }

            incEditcap.apply {
                commentInput.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        tvCharCount.text = s?.length.toString()
                        tvDone.isEnabled = s?.length != 0
                    }
                })
            }

            viewModel.apply {
                failure(failure, ::handleFailure)
                observe(deleteCover, ::showDeleteCoverResult)
                observe(simpleAnalysis, ::onSimpleAnalysis)
                observe(downloadFilePath, ::handleDownloadVideUrl)
            }

            bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {

//                    if (newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_EXPANDED) {
//                        // clRoot.setBackgroundResource(R.drawable.bg_bottom_slidechange)
//                        bottomSheet.setBackgroundResource(R.drawable.bg_bottom_slidechange)
//
//                    } else {
//
//                        bottomSheet.setBackgroundResource(R.drawable.bg_bottom_sheet)
//                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}


            })

        }
    }


    private val pageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            Timber.d("onPageSelected: $position")
            feedSlotsPagesManager.onSlotSelected(position)
        }
    }

    private fun onUploadSuccess() {
        Timber.d("Success Uploading comment")
    }

    private fun handleClapClick() {
        Timber.d("onClap:")
        feedSlotsPagesManager.onClap()
    }

    private fun handleShareClick(attemptId: String, userId: String, title: String, artistsName: String) {
        Analytics.logEvent(
            Analytics.Event.ShareCoverEvent(
                Analytics.Event.Param.SongId(title),
                Analytics.Event.Param.ArtistId(artistsName),
                Analytics.Event.Param.CoverId(attemptId),
                Analytics.Event.Param.UserId(userId),
            )
        )
        feedSlotsPagesManager.onShare()
    }

    private lateinit var otherMessage: EditText

    private fun showReportCover(attemptId: String, userId: String) {
        Analytics.logEvent(
            Analytics.Event.ReportContentEvent(
                Analytics.Event.Param.CoverId(attemptId),
                Analytics.Event.Param.UserId(userId),
            )
        )
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_report_cover, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        val reportCover = dialogView.findViewById<TextView>(R.id.btnReportFeed)

        val abusiveCheckBox = dialogView.findViewById<CheckBox>(R.id.cbAbusive)
        val inappropriateCheckBox = dialogView.findViewById<CheckBox>(R.id.cbInappropriate)
        val hatefulCheckBox = dialogView.findViewById<CheckBox>(R.id.cbHateful)
        val spamCheckBox = dialogView.findViewById<CheckBox>(R.id.cbSpam)
        val fakeCheckBox = dialogView.findViewById<CheckBox>(R.id.cbFake)
        val harrasmentCheckBox = dialogView.findViewById<CheckBox>(R.id.cbHarrasment)
        val disrespectfulCheckBox = dialogView.findViewById<CheckBox>(R.id.cbDisrespectful)
        otherMessage = dialogView.findViewById(R.id.tvReasonReport)

        val selectedStrings = ArrayList<String>()
        val keepCover = dialogView.findViewById<TextView>(R.id.btnNo)
        val alertDialog = mBuilder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        reportCover.setOnClickListener {
            if (abusiveCheckBox.isChecked) selectedStrings.add("Abusive")
            if (inappropriateCheckBox.isChecked) selectedStrings.add("Inappropriate")
            if (hatefulCheckBox.isChecked) selectedStrings.add("Hateful")
            if (spamCheckBox.isChecked) selectedStrings.add("Spam")
            if (fakeCheckBox.isChecked) selectedStrings.add("Fake Account")
            if (harrasmentCheckBox.isChecked) selectedStrings.add("Harrasment")
            if (disrespectfulCheckBox.isChecked) selectedStrings.add("Disrespectful")
            viewModel.reportCover(otherMessage.text.toString(), selectedStrings, attemptId)
            alertDialog.dismiss()
        }
    }

    private fun showReportUser(attemptId: String, userId: String) {
        Analytics.logEvent(
            Analytics.Event.ReportUserEvent(
                Analytics.Event.Param.CoverId(attemptId),
                Analytics.Event.Param.UserId(userId),
            )
        )
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_report_cover, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        dialogView.findViewById<TextView>(R.id.tvReportText).setText(R.string.report_user_reason_text)
        dialogView.findViewById<TextView>(R.id.btnReportFeed).setText(R.string.report_user)

        val selectedStrings = ArrayList<String>()
        val alertDialog = mBuilder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<TextView>(R.id.btnReportFeed).setOnClickListener {
            if (dialogView.findViewById<CheckBox>(R.id.cbAbusive).isChecked) selectedStrings.add("Abusive")
            if (dialogView.findViewById<CheckBox>(R.id.cbInappropriate).isChecked) selectedStrings.add("Inappropriate")
            if (dialogView.findViewById<CheckBox>(R.id.cbHateful).isChecked) selectedStrings.add("Hateful")
            if (dialogView.findViewById<CheckBox>(R.id.cbSpam).isChecked) selectedStrings.add("Spam")
            if (dialogView.findViewById<CheckBox>(R.id.cbFake).isChecked) selectedStrings.add("Fake Account")
            if (dialogView.findViewById<CheckBox>(R.id.cbHarrasment).isChecked) selectedStrings.add("Harrasment")
            if (dialogView.findViewById<CheckBox>(R.id.cbDisrespectful).isChecked) selectedStrings.add("Disrespectful")
            val selfUserId = singstrViewModel.user.value?.id ?: "unknown"
            viewModel.reportUser(dialogView.findViewById<TextView>(R.id.tvReasonReport).text.toString(), selectedStrings, selfUserId)
            alertDialog.dismiss()
        }
    }

    private fun showDialogCoverFeedOption(attemptId: String, userId: String, songTitle: String, artistsName: String) {
        val selfUserId = singstrViewModel.user.value?.id
        if (userId == selfUserId) {
            val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_cover_feed_options, null)
            val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
            val deleteCover = dialogView.findViewById<TextView>(R.id.tv_deleteCover)
            val editCaption = dialogView.findViewById<TextView>(R.id.tv_editCaption)
            val tvDetailAnalysis = dialogView.findViewById<TextView>(R.id.tv_detailedAnalysis)
            val tvShareCover = dialogView.findViewById<TextView>(R.id.tv_sharevideo)

            val alertDialog = mBuilder.show()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            tvDetailAnalysis.setOnClickListener {
                alertDialog.dismiss()
                findNavController().navigate(FeedFragmentDirections.toDetailAnalysisFragment(attemptId, userId))
            }
            deleteCover.setOnClickListener {
                showCustomDialog(attemptId, songTitle, artistsName)
                alertDialog.dismiss()
            }
            editCaption.setOnClickListener {
                showCaptionDialog(attemptId)
                alertDialog.dismiss()
            }
            tvShareCover.setOnClickListener {
                handleShareClick(
                    attemptId,
                    userId,
                    songTitle,
                    artistsName
                )
            }

        } else {
            val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialogue_report_user_feed, null)
            val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
            val reportContent = dialogView.findViewById<TextView>(R.id.tv_reportContent)
            val reportUser = dialogView.findViewById<TextView>(R.id.tv_reportUser)
            val alertDialog = mBuilder.show()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            reportContent.setOnClickListener {
                showReportCover(attemptId, userId)
                alertDialog.dismiss()
            }
            reportUser.setOnClickListener {
                showReportUser(attemptId, userId)
                alertDialog.dismiss()
            }
        }
    }

    fun showCaptionDialog(attemptId: String) {
        bottomSheetEditCaptionBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.apply {
            incEditcap.apply {
                tvDone.setOnClickListener {
                    // FIXME - functionality not built
                    bottomSheetEditCaptionBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
    }

    fun showShareOptionDialog(shareMsg: String, attemptId: String) {
        bottomSheetShareOptionBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        Timber.d("bottomSheetShareOptionBehavior state expanded")
//        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.apply {
            incShareOptions.apply {

                vCopyLink.setOnClickListener {
                    val clickBoardMng = requireActivity().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    clickBoardMng.setPrimaryClip(ClipData.newPlainText("label", shareMsg))
                    AppToast.show(requireContext(), "Link Copied!")
                    bottomSheetShareOptionBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                tvDownloadVideo.setOnClickListener {
                    pbDownloadVideo.visibility = VISIBLE
                    startDownloadCover(attemptId)
                }
                vShareOther.setOnClickListener {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareMsg)
                        type = "text/plain"
                    }
                    startActivity(Intent.createChooser(sendIntent, "Choose application.."))
                    bottomSheetShareOptionBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

                ivClose.setOnClickListener {
                    bottomSheetShareOptionBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
    }

    private fun showCustomDialog(attemptId: String, songTitle: String, artistsName: String) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_delete_your_cover, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        val deleteCover = dialogView.findViewById<TextView>(R.id.tvDelete)
        val keepCover = dialogView.findViewById<TextView>(R.id.btnNo)
        val alertDialog = mBuilder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteCover.setOnClickListener {
            viewModel.deleteCover(attemptId)
            Analytics.logEvent(
                Analytics.Event.DeleteCoverEvent(
                    Analytics.Event.Param.SongId(songTitle),
                    Analytics.Event.Param.GenreId("NA"),
                    Analytics.Event.Param.ArtistId(artistsName),
                )
            )
            alertDialog.dismiss()
        }
        keepCover.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun showDeleteCoverResult(isCoverDeleted: Boolean) {
        if (isCoverDeleted)
            Toast.makeText(context, "Cover Deleted Successfully", Toast.LENGTH_SHORT).show()
    }

    private fun onSimpleAnalysis(simpleAnalysis: SimpleAnalysis) {
        if (binding.incBottomSheet.tvDetailAnalysis.isEnabled)
            return
        Analytics.logEvent(
            Analytics.Event.CheckDetailedAnalysisEvent(
                Analytics.Event.Param.SongId(mCoverSlot.cover.songMini.title),
                Analytics.Event.Param.GenreId("NA"),
                Analytics.Event.Param.ArtistId(mCoverSlot.cover.songMini.artists.toString()),
                Analytics.Event.Param.CoverId(mCoverSlot.cover.id),
                Analytics.Event.Param.RightLines(simpleAnalysis.linesDonePerfectly),
                Analytics.Event.Param.WrongLines(simpleAnalysis.linesNeedsImprovement),
                Analytics.Event.Param.TotalScore(simpleAnalysis.totalScore),
                Analytics.Event.Param.TuneScore(simpleAnalysis.tuneScore),
                Analytics.Event.Param.TimeScore(simpleAnalysis.timeScore),
            )
        )
        findNavController().navigate(
            FeedFragmentDirections.toDetailAnalysisFragment(mCoverSlot.cover.id, mCoverSlot.cover.userMini.id)
        )
        binding.incBottomSheet.tvDetailAnalysis.isEnabled = true
    }

    override fun onResume() {
        Timber.d("onResume: ")
        super.onResume()
        feedSlotsPagesManager.resume()
        // feedSlotsPagesManager.createSlotPage()
        refreshData(songTitle, artist, userName)
    }

    override fun onPause() {
        Timber.d("onPause: ")
        feedSlotsPagesManager.pause()
        super.onPause()
    }

    override fun onDestroyView() {
        Timber.d("onDestroyView: ")
        binding.vpVideoPlayerPager.unregisterOnPageChangeCallback(pageChangeListener)
        super.onDestroyView()
    }

    override fun onDestroy() {
        Timber.d("onDestroy: $displayName")
        feedSlotsPagesManager.destroy()
        feedSlotsVM.selectedFeedCategory = -1
        super.onDestroy()
    }

    companion object {
        private const val IS_EXO_DEBUG = false
        private const val IS_FORCE_LOWEST_BITRATE = false

        private const val MAX_EXO_PAYER_COUNT = 3

        private const val MAX_CLAP_COUNT = 10

        private const val ACTION_POLL_DELAY_MS = 50L

        private const val MSG_UPDATE_PLAYBACK_PROGRESS = 8550

        private const val COVER_MIN_PLAY_DURATION_MS = 15_000L
        private const val CLAP_NOTIFICATION_REMAINDER_MS = 5_000L


        private const val RELOAD_DISTANCE_FROM_END = 3

        private const val MAX_COMMENTS_IN_BOTTOM_SHEET_COUNT = 3

    }


    internal inner class FeedSlotPagesManager(context: Context) {


        private val playersPool = (1..MAX_EXO_PAYER_COUNT)
            .map {
                SimpleExoPlayer.Builder(context)
                    .setTrackSelector(
                        DefaultTrackSelector(context).also {
                            it.parameters = it.buildUponParameters()
                                .setForceLowestBitrate(true/*BuildConfig.DEBUG && IS_FORCE_LOWEST_BITRATE*/)
                                .build()
                        }
                    )
                    .build().also {
                        if (BuildConfig.DEBUG && IS_EXO_DEBUG) {
                            it.addAnalyticsListener(EventLogger(null, "ExoPlayer"))
                        }
                    }
            }.toSet()

        private val activePlayers: MutableMap<String, SimpleExoPlayer> = mutableMapOf()

        private val actionCallback: Handler.Callback = Handler.Callback { msg ->

            when (msg.what) {

                MSG_UPDATE_PLAYBACK_PROGRESS -> {
//                Timber.d("MSG_UPDATE_PLAYBACK_PROGRESS")
                    slotPage.updatePlaybackProgress()
                    true
                }

                else -> false
            }
        }

        private val actionHandler: Handler = Handler(actionCallback)

        private lateinit var slotPage: FeedSlotPage

        internal var currentSlotPosition: Int = -1
            private set

        private var coverSwitchTimeMS: Long = 0

        internal var feedRefreshPosition: Int = 0

        internal fun onSlotSelected(position: Int) {
            Timber.d("onSlotSelected: $position")

            actionHandler.removeCallbacksAndMessages(null)

            // clean-up for old position
            if (::slotPage.isInitialized && currentSlotPosition != position) {
                slotPage.cleanup()
            }

            // currently necessary to assign new value after clean-up
            currentSlotPosition = position
            preparePlayers(position)

            if (feedRefreshPosition <= position) {
                Timber.d("onPageSelected: refreshing feed from position $feedRefreshPosition")
                feedRefreshPosition = feedSlotsAdapter.itemCount
                loadFeed()
            }

            createSlotPage()
        }

        internal fun createSlotPage() {

            Timber.d("createSlotPage: [$currentSlotPosition]")

            val feedSlotVH = ((binding.vpVideoPlayerPager.getChildAt(0) as RecyclerView)
                .findViewHolderForAdapterPosition(currentSlotPosition))

            if (null != feedSlotVH) {
                val tileFeedPlayerBinding = (feedSlotVH as FeedSlotsAdapter.FeedSlotVH).binding

                val feedSlot = feedSlotsAdapter.feedSlots[currentSlotPosition]
                activePlayers[feedSlot.videoUrl]?.let { player ->
                    slotPage = when (feedSlot) {
                        is CoverSlot -> {
                            CoverSlotPage(feedSlot, tileFeedPlayerBinding, player)
                        }
                        is LearnSlot -> {
                            LearnSlotPage(feedSlot, tileFeedPlayerBinding, player)
                        }
                        else -> {
                            Timber.e("pageChangeListener: Unknown slotView: $feedSlot. Switching to next one.")
                            switchToNextFeedSlot()
                            return
                        }
                    }
                    slotPage.setup()
                }
            } else {
                Timber.d("createSlotPage: feedSlotVH not available")
                binding.vpVideoPlayerPager.doOnPreDraw { createSlotPage() }
            }

        }

        internal fun switchToNextFeedSlot() {
            Timber.d("switchToNextFeedSlot: currentSlotPosition=$currentSlotPosition")
            actionHandler.removeCallbacksAndMessages(null)
            binding.apply {
                vpVideoPlayerPager.setCurrentItem(vpVideoPlayerPager.currentItem + 1, true)
            }
        }

        private fun preparePlayers(currentPosition: Int) {
            Timber.d("preparePlayers: $currentPosition")
            val fromPosition = max(0, currentPosition - (MAX_EXO_PAYER_COUNT - 1) / 2)
            val endPosition = min(fromPosition + MAX_EXO_PAYER_COUNT, feedSlotsAdapter.feedSlots.size)
            val newUrls2Slots = feedSlotsAdapter.feedSlots.subList(fromPosition, endPosition).associateBy { it.videoUrl }

            activePlayers.keys.retainAll(newUrls2Slots.keys)

            val availablePlayers = (playersPool - activePlayers.values).toMutableSet()
            val pendingUrls = (newUrls2Slots.keys - activePlayers.keys).toMutableSet()

            Timber.d("preparePlayers: availablePlayers=${availablePlayers.size}, pendingUrls=${pendingUrls.size}")

            while (availablePlayers.isNotEmpty() && pendingUrls.isNotEmpty()) {
                val player = availablePlayers.first()
                val videoUrl = pendingUrls.first()

                newUrls2Slots[videoUrl]?.let { feedSlot ->

                    val mediaItemBuilder = MediaItem.Builder()
                        .setUri(feedSlot.videoUrl)
                        .setMimeType(MimeTypes.APPLICATION_M3U8)

                    if (feedSlot is CoverSlot) {
                        mediaItemBuilder.setClipStartPositionMs(feedSlot.cover.songMini.lyricsStartTimeMS.toLong())
                    }

                    player.apply {
                        setMediaItem(mediaItemBuilder.build())
                        playWhenReady = false
                        prepare()
                    }
                }
                availablePlayers.remove(player)
                pendingUrls.remove(videoUrl)
                activePlayers[videoUrl] = player
            }
        }

        internal fun resume() {
            Timber.d("resume:")
            if (::slotPage.isInitialized) {
                slotPage.play()

                createSlotPage()

            }
        }

        internal fun pause() {
            Timber.d("pause: ")
            actionHandler.removeCallbacksAndMessages(null)
            if (::slotPage.isInitialized) {
                slotPage.pause()
            }
        }

        internal fun destroy() {
            Timber.d("destroy: ")
            playersPool.forEach { it.release() }
            if (::slotPage.isInitialized) {
                if (slotPage is CoverSlotPage) {
                    (slotPage as CoverSlotPage).sendCoveStats()
                }
            }
        }

        fun onClap() {
            Timber.d("onClap:")
            slotPage.onClap()
        }

        fun onShare() {
            Timber.d("onShare:")
            slotPage.onShare()
        }

        internal abstract inner class FeedSlotPage(
            protected val tileFeedPlayerBinding: TileFeedPlayerBinding,
            protected val player: SimpleExoPlayer
        ) : Player.Listener {

            override fun onRenderedFirstFrame() {
                Timber.d("onRenderedFirstFrame:")
                tileFeedPlayerBinding.ivThumbnail.animate().alpha(0f)
            }

            override fun onPlaybackStateChanged(state: Int) {
                Timber.d("onPlaybackStateChanged: [$state]")
                // TODO - Use LiveData for these events?
                when (state) {
                    STATE_READY -> onPlayerReady()
                    STATE_ENDED -> switchToNextFeedSlot()
                }
            }

            open fun setup() {
                Timber.d("setup:")
                tileFeedPlayerBinding.pvVideo.player = player
                player.addListener(this)

                if (STATE_READY == player.playbackState) {
                    Timber.d("setup: player ready")
                    onPlayerReady()
                } else {
                    binding.pbLoading.visibility = VISIBLE
                    tileFeedPlayerBinding.ivThumbnail.alpha = 1f
                }

                binding.apply {
                    pbFeedProgress.apply {
                        setProgress(0, true)
                        secondaryProgress = 0
                    }
                    gClapNotification.visibility = GONE
                    vCircle.apply {
                        scaleX = 1.3f
                        scaleY = 1.3f
                    }
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    updatePlayerUi()
                    updateBottomSheet()
                }
                player.seekTo(0)
                player.play()
            }

            abstract fun updatePlayerUi()

            abstract fun updateBottomSheet()

            abstract fun updateCoverSwitchTime(videoDurationMS: Int)

            open fun onPlayerReady() {
                Timber.d("onPlayerReady: ")

                updateCoverSwitchTime(player.duration.toInt())

                try {
                    binding.apply {
                        pbFeedProgress.apply {
                            max = player.duration.toInt()
                            progress = player.currentPosition.toInt()
                            secondaryProgress = coverSwitchTimeMS.toInt()
                        }
                        pbLoading.visibility = GONE
                    }
                    tileFeedPlayerBinding.ivThumbnail.animate().alpha(0f)
                    actionHandler.sendEmptyMessage(MSG_UPDATE_PLAYBACK_PROGRESS)
                } catch (e: Exception) {
                    Timber.d(" Message 123   is ${e.stackTrace.toString()}")
                    Timber.d(" Message 123   is1 ${e.message}")
                }
            }

            fun play() {
                Timber.d("play: ")
                player.addListener(this)
                player.play()
                if (STATE_READY == player.playbackState) {
                    Timber.d("play: player ready")
                    onPlayerReady()
                }
            }

            fun pause() {
                Timber.d("pause: ")
                player.removeListener(this)
                actionHandler.removeCallbacksAndMessages(null)
                player.pause()
            }

            abstract fun updatePlaybackProgress()

            open fun onClap() {}

            open fun onShare() {}

            open fun cleanup() {
                Timber.d("cleanup:")
                player.removeListener(this)
                tileFeedPlayerBinding.pvVideo.player = null
                tileFeedPlayerBinding.ivThumbnail.alpha = 1f
                player.pause()
            }
        }

        internal inner class CoverSlotPage(
            private val coverSlot: CoverSlot,
            tileFeedPlayerBinding: TileFeedPlayerBinding,
            player: SimpleExoPlayer
        ) : FeedSlotPage(tileFeedPlayerBinding, player) {

            private var clapToViewMoreTimeMS: Long = 0L
            private var clapCount: Int = 0
            private var lastPlayPositionMS = 0L

            override fun setup() {
                super.setup()
                clapCount = 0
                clapToViewMoreTimeMS = 0
                lastPlayPositionMS = 0L
            }

            override fun updatePlayerUi() {
                Timber.d("updatePlayerUi: $coverSlot")
                binding.apply {
                    rgFeedType.visibility = VISIBLE
                    ivMic.visibility = VISIBLE
                    tvViewCount.visibility = VISIBLE

                    ibClap.visibility = VISIBLE
                    tvClapsCount.visibility = VISIBLE
                    ibComment.visibility = VISIBLE
                    tvCommentCount.visibility = VISIBLE
                    ibShare.visibility = VISIBLE
                    tvShareText.visibility = VISIBLE
                    ibMoreOptions.visibility = VISIBLE

                    coverSlot.cover.apply {
                        statistics.let { statistics ->
                            coverViewCount = statistics.viewCount.pretty
                            tvViewCount.text = coverViewCount
                            tvClapsCount.text = statistics.clapCount.pretty
//                            tvShareCount.text = statistics.shareCount.pretty
                        }
                    }
                    feedBottomSheetCommentAdapter.comments = coverSlot.comments.take(MAX_COMMENTS_IN_BOTTOM_SHEET_COUNT)
                }
            }

            @SuppressLint("SetTextI18n")
            override fun updateBottomSheet() {
                Timber.d("updateBottomSheet: $coverSlot")

                binding.apply {
                    incBottomSheet.apply {
                        val cover: Cover = coverSlot.cover
                        mCoverSlot = coverSlot
                        tvDetailAnalysis.visibility = when (cover.userMini.id == singstrViewModel.user.value?.id) {
                            true -> VISIBLE
                            else -> GONE
                        }
                        clCoverSlotContainer.visibility = VISIBLE
                        clLearnSlotContainer.visibility = GONE

                        cover.userMini.let { userMini ->
                            incBottomSheet.apply {
                                ivUserImage.loadCenterCropImageFromUrl(userMini.dpUrl)
                                userImgUrl = userMini.dpUrl
                                userDisplayName = userMini.displayName
                                userName = userMini.handle
                                tvUserName.visibility = VISIBLE
                                tvUserName.text = userName
                                tvUserComment.text = cover.caption
                                viewModel.isFollowingUser(userMini.id).let {
                                    cbFollow.isChecked = it
                                    if (it)
                                        cbFollow.text = "Following"
                                    else
                                        cbFollow.text = "Follow"
                                    cbFollow.isEnabled = true
                                }

                                vUserProfile.setOnClickListener {
                                    findNavController().navigate(
                                        FeedFragmentDirections.toOtherUserProfileFragment(userMini.id)
                                    )
                                }

                                cbFollow.setOnCheckedChangeListener { _, isChecked ->
                                    if (isChecked) {
                                        cbFollow.text = "Following"
                                        viewModel.subscribeUser(userMini.id)
                                    } else {
                                        cbFollow.text = "Follow"
                                        viewModel.unSubscribeUser(userMini.id)
                                    }
                                }
                            }
                        }

                        incShareOptions.apply {
                            ivCoverThumbnail.loadUrl(
                                cover.thumbnailUrl
                            )
                            tvCaption.text = cover.caption
                        }

                        incSong.apply {
                            val songMini: SongMini = cover.songMini

                            root.visibility = VISIBLE
                            //Bug 1

                            songTitle = songMini.title
                            artist = songMini.lyrics

                            tvTitle.text = songTitle
                            tvSubtitle.text = artist

                            tvSubtitle.movementMethod = LinkMovementMethod.getInstance()
                            tvSubtitle.ellipsize = TextUtils.TruncateAt.MARQUEE
                            tvSubtitle.isSelected = true
                            tvSubtitle.isSingleLine = true

                            ivThumbnail.loadUrl(songMini.thumbnailUrl)
                            ivMic.setOnClickListener {
                                findNavController().navigate(
                                    FeedFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName, songMini.id)
                                )
                            }
                            tvSing.setOnClickListener {
                                findNavController().navigate(
                                    FeedFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName, songMini.id)
                                )
                            }
                        }

                        Analytics.logEvent(
                            Analytics.Event.ViewCoverEvent(
                                Analytics.Event.Param.SongId(cover.songMini.title),
                                Analytics.Event.Param.ArtistId(cover.songMini.artists.toString()),
                                Analytics.Event.Param.CoverId(cover.id),
                                Analytics.Event.Param.UserId(cover.userMini.id),
                                Analytics.Event.Param.TotalViews(cover.statistics.viewCount.toString()),
                                Analytics.Event.Param.TotalClaps(cover.statistics.clapCount.toString()),
                            )
                        )

                        Analytics.facebookLogEvent(
                            Analytics.Event.ViewContentEvent(
                                Analytics.Event.Param.SongId(cover.songMini.title),
                                Analytics.Event.Param.ArtistId(cover.songMini.artists.toString()),
                                Analytics.Event.Param.CoverId(cover.id),
                                Analytics.Event.Param.UserId(cover.userMini.id),
                                Analytics.Event.Param.TotalViews(cover.statistics.viewCount.toString()),
                                Analytics.Event.Param.TotalClaps(cover.statistics.clapCount.toString()),
                            )
                        )

//                        incUserComment.apply {
//                            val userMini: UserMini = cover.userMini
//                            singleTextView(tvUserName, userMini.handle, cover.caption, userMini.id)
//                        }
                        feedBottomSheetCommentAdapter.onUserIDClicked = {
                            val selfUserId = singstrViewModel.user.value?.id
                            if (selfUserId == it) {
                                findNavController().navigate(FeedFragmentDirections.toProfileFrag())
                            } else {
                                Timber.e("=============== User Id in Frr :: " + it)
                                findNavController().navigate(FeedFragmentDirections.toOtherUserProfileFragment(it))
                            }
                        }
                        feedBottomSheetCommentAdapter.onCommentClickListener = {
                            findNavController().navigate(FeedFragmentDirections.toCoverCommentsFragment(coverSlot.cover.id))
                        }
//                        incBottomSheet.viewComments.setOnClickListener {
//                            findNavController().navigate(FeedFragmentDirections.toCoverCommentsFragment(coverSlot.cover.id))
//                        }
//
//                        incBottomSheet.tvCommentdummy.setOnClickListener {
//                            findNavController().navigate(FeedFragmentDirections.toCoverCommentsFragment(coverSlot.cover.id))
//                        }

                        incBottomSheet.tvDetailAnalysis.setOnClickListener {
                            tvDetailAnalysis.isEnabled = false
                            viewModel.loadSimpleAnalysis(cover.id)
                        }

                        ibComment.setOnClickListener {
                            findNavController().navigate(FeedFragmentDirections.toCoverCommentsFragment(coverSlot.cover.id))
                        }

                        ibMoreOptions.setOnClickListener {
                            showDialogCoverFeedOption(
                                coverSlot.cover.id,
                                coverSlot.cover.userMini.id,
                                coverSlot.cover.songMini.title,
                                coverSlot.cover.songMini.artists.names
                            )
                        }
                        ibShare.setOnClickListener {
                            handleShareClick(
                                coverSlot.cover.id,
                                coverSlot.cover.userMini.id,
                                coverSlot.cover.songMini.title,
                                coverSlot.cover.songMini.artists.names
                            )
                        }
                    }
                }
            }

            private fun singleTextView(textView: TextView, name: String, comment: String, id: String) {
                val spanText = SpannableStringBuilder()
                spanText.bold {
                    append(name.checkUserHandle())
                }
//            spanText.setSpan(AbsoluteSizeSpan(40),0,spanText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                spanText.setSpan(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        findNavController().navigate(FeedFragmentDirections.toOtherUserProfileFragment(id))
                    }

                    override fun updateDrawState(textPaint: TextPaint) {
                        textPaint.isUnderlineText = false // this remove the underline
                    }
                }, spanText.length - name.length, spanText.length, 0)
                spanText.append(" $comment")

                textView.movementMethod = LinkMovementMethod.getInstance()
                textView.setText(spanText, TextView.BufferType.SPANNABLE)
            }

            override fun updateCoverSwitchTime(videoDurationMS: Int) {
                coverSwitchTimeMS = min(videoDurationMS.toLong(), COVER_MIN_PLAY_DURATION_MS)
                Timber.d("updateCoverSwitchTime: $coverSwitchTimeMS/$videoDurationMS")
            }

            override fun onPlayerReady() {
                super.onPlayerReady()
                clapToViewMoreTimeMS = (player.duration - coverSwitchTimeMS) / MAX_CLAP_COUNT
            }

            override fun updatePlaybackProgress() {
                val currentPlayPositionMS = player.currentPosition.toInt()
                if (currentPlayPositionMS < coverSwitchTimeMS) {
                    binding.pbFeedProgress.progress = currentPlayPositionMS
                    lastPlayPositionMS = currentPlayPositionMS.toLong()

                    actionHandler.sendEmptyMessageDelayed(MSG_UPDATE_PLAYBACK_PROGRESS, ACTION_POLL_DELAY_MS)

                    if (clapCount < MAX_CLAP_COUNT && (coverSwitchTimeMS - currentPlayPositionMS) < CLAP_NOTIFICATION_REMAINDER_MS) {
                        binding.gClapNotification.visibility = VISIBLE
                    }

                } else {
                    Timber.d("updatePlaybackProgress: $currentPlayPositionMS < $coverSwitchTimeMS")
                    switchToNextFeedSlot()
                }
            }

            override fun onClap() {
                Timber.d("onClap: ")
                if (clapCount < MAX_CLAP_COUNT) {
                    coverSlot.cover.statistics.clapCount++
                    clapCount++
                    coverSwitchTimeMS += clapToViewMoreTimeMS
                    binding.apply {
                        pbFeedProgress.secondaryProgress = coverSwitchTimeMS.toInt()
                        tvClapsCount.text = coverSlot.cover.statistics.clapCount.pretty
                        gClapNotification.visibility = GONE
                        vCircle.animate().scaleXBy(1.3f).scaleYBy(1.3f)
                        vWave.apply {
                            scaleX = 0.3f
                            scaleY = 0.3f
                            alpha = 1f
                            animate().scaleX(1f).scaleY(1f).alpha(0f)
                        }
                    }
                }
            }

            override fun onShare() {
                Timber.d("onShare: implemtation")
                val selfUserId = singstrViewModel.user.value?.id
                val sharingMsg = if (coverSlot.cover.userMini.id == selfUserId)
                    getString(R.string.share_own_cover_generic)
                else
                    getString(R.string.share_message_generic)

                createCoverDynamicLink(coverSlot.cover.id, sharingMsg, coverSlot.cover.songMini.title)
            }

            override fun cleanup() {
                Timber.d("cleanup:")
                super.cleanup()
                sendCoveStats()
            }

            internal fun sendCoveStats() {
                Timber.d("sendCoveStats: ")
                if (0 < clapCount) {
                    viewModel.addClap(coverSlot.cover.id, clapCount)
                }
                viewModel.sendCoverWatchedDurationMS(coverSlot.cover.id, lastPlayPositionMS)
            }
        }

        internal inner class LearnSlotPage(
            private val learnSlotView: LearnSlot,
            tileFeedPlayerBinding: TileFeedPlayerBinding,
            player: SimpleExoPlayer
        ) : FeedSlotPage(tileFeedPlayerBinding, player) {

            override fun updatePlayerUi() {
                binding.apply {
                    rgFeedType.visibility = GONE
                    ivMic.visibility = GONE
                    tvViewCount.visibility = GONE

                    ibClap.visibility = GONE
                    tvClapsCount.visibility = GONE
                    ibComment.visibility = GONE
                    tvCommentCount.visibility = GONE
                    ibShare.visibility = GONE
                    tvShareText.visibility = GONE
                    ibMoreOptions.visibility = GONE
                }
            }

            override fun updateBottomSheet() {
                binding.apply {
                    incBottomSheet.apply {
                        clCoverSlotContainer.visibility = GONE
                        clLearnSlotContainer.visibility = VISIBLE

                        val learn = learnSlotView.learnSlot
                        tvTitle.text = learn.title
                        tvDifficulty.text = learn.difficulty
                        tvWatchLesson.setOnClickListener {
                            findNavController().navigate(FeedFragmentDirections.toLessonFragment(learn.lessonId))
                        }
                    }
                }
            }

            override fun updateCoverSwitchTime(videoDurationMS: Int) {
                coverSwitchTimeMS = videoDurationMS.toLong()
                Timber.d("updateCoverSwitchTime: $coverSwitchTimeMS/$videoDurationMS")
            }

            override fun updatePlaybackProgress() {
                binding.pbFeedProgress.progress = player.currentPosition.toInt()
            }

        }

    }


    private fun refreshData(tittle: String, artistname: String, userName: String) {


        binding.apply {

            incBottomSheet.apply {
                //val cover: Cover = coverSlot.cover

//                clCoverSlotContainer.visibility = VISIBLE
                clLearnSlotContainer.visibility = GONE

                incBottomSheet.tvUserName.text = userName
                tvViewCount.text = coverViewCount

                incSong.apply {
                    // val songMini: SongMini = cover.songMini

                    root.visibility = VISIBLE
                    //Bug 1

                    tvTitle.text = tittle
                    tvSubtitle.text = artistname
                    // ivThumbnail.loadUrl(songMini.thumbnailUrl)

                }

            }


        }

    }

    private fun createCoverDynamicLink(coverId: String, message: String, songTitle: String) {
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("https://www.singshala.com/app/feed/covers/${coverId}")
            domainUriPrefix = "https://singshala.page.link"
            androidParameters("com.lucidmusic.singstr") {
            }
//            iosParameters("com.example.ios") {
//                appStoreId = "123456789"
//                minimumVersion = "1.0.1"
//            }
            googleAnalyticsParameters {
                source = "android"
                medium = "social"
                campaign = "score-card"
            }
            socialMetaTagParameters {
                title = "Singhshala Cover"
                description = songTitle
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->

            Timber.d("createCoverDynamicLink share msg ${shortLink.toString()}")
            showShareOptionDialog("$message ${shortLink.toString()}", coverId)
        }
    }

    private fun startDownloadCover(coverId: String) {
        attemptId = coverId
        feedSlotsPagesManager.pause()
        binding.incShareOptions.pbDownloadVideo.visibility = VISIBLE
        viewModel.downloadVideoUrl(coverId)

    }

    open fun getFilePath(context: Context): String? {
        val dir = File(context.getExternalFilesDir("Output").toString())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val extension = ".mp4"
        val dest = File(dir.path + File.separator + "Output" + System.currentTimeMillis() / 1000L + extension)
        return dest.absolutePath
    }

    private fun ffmpegProcess(


        inputPath: String, outputPath: String?, firstLogoPath: String, secondLogoPath: String,
        userImagePath: String, fontfilePath: String, boldFontFilePath: String
    ) {


        val query = arrayOf(
            "-i", "",  // original video file [1]
            "-i", "",  // first singshala logo [3]
            "-i", "",  // full screen singshala logo [5]
            "-i", "",  // user image [7]
            "-filter_complex",
//            "\u0022[0] scale=720:1280:force_original_aspect_ratio=increase,crop=720:1280[v1];[v1][1]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)*0.9:enable='between(t,0.1,4)', drawtext=fontfile=font.otf:text='Stack Overflow':fontcolor=white:fontsize=26:box=1:boxcolor=black@0.5:boxborderw=5:x=(w-text_w)/2:y=(main_h-text_h)*0.891, fade=t=in:st=0:d=0.5 [v2]; [v2][2]overlay=0.1:0.1:enable='gt(t,4)'[v2]; [v2][3]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)*0.24:enable='gt(t,4.1)', drawtext=fontfile=font_bold.otf:text='UserName':fontcolor=white:fontsize=30:shadowcolor=black:shadowx=5:shadowy=5:x=(w-text_w)/2:y=(main_h-text_h)*0.42:enable='gt(t,4.1)', fade=t=out:st=6.5:d=0.5, drawtext=fontfile=font_bold.otf:text='@username':fontcolor=white@0.5:fontsize=24:shadowcolor=black:shadowx=2:shadowy=2:x=(w-text_w)/2:y=(main_h-text_h)*0.45:enable='gt(t,4.1)', fade=t=out:st=6.5:d=0.5 [v3]\u0022",
            "\u0022[0] scale=720:1280:force_original_aspect_ratio=increase,crop=720:1280[v1];[v1][1]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)*0.9:enable='between(t,0.1,4)', drawtext=fontfile=font.otf:text='Stack Overflow':fontcolor=white:fontsize=26:box=1:boxcolor=black@0.5:boxborderw=5:x=(w-text_w)/2:y=(main_h-text_h)*0.891, fade=t=in:st=0:d=0.5 [v2]; [v2][2]overlay=0.1:0.1:enable='gt(t,4)'[v2]; [v2][3]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)*0.24:enable='gt(t,4.1)', drawtext=fontfile=$boldFontFilePath:text='UserName':fontcolor=white:fontsize=30:shadowcolor=black:shadowx=5:shadowy=5:x=(w-text_w)/2:y=(main_h-text_h)*0.42:enable='gt(t,4.1)', fade=t=out:st=6.5:d=0.5, drawtext=fontfile=$boldFontFilePath:text='@username':fontcolor=white@0.5:fontsize=24:shadowcolor=black:shadowx=2:shadowy=2:x=(w-text_w)/2:y=(main_h-text_h)*0.45:enable='gt(t,4.1)', fade=t=out:st=6.5:d=0.5 [v3]\u0022",
            "-map",
            "\u0022[v3]\u0022",
            "-map",
            "0:a",
            ""          // output video file [14]
        )



        query[1] = inputPath
        query[3] = firstLogoPath
        query[5] = secondLogoPath
        query[7] = userImagePath
        query[14] = outputPath!!
//        handler.postDelayed(Runnable {
//            Timber.d("STOPPING THE RENDERING!")
//            task.sendQuitSignal()
//        }, 8000)
    }

    @Throws(IOException::class)
    fun getFileFromAssets(fileName: String): File =
        File(requireContext().cacheDir, fileName).also {
            if (!it.exists()) {
                it.outputStream().use { cache ->
                    requireContext().assets.open(fileName).use { inputStream ->
                        inputStream.copyTo(cache)
                    }
                }
            }
        }

    private fun handleDownloadVideUrl(videoUrl: String) {
//        val manager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        val req = DownloadManager.Request(Uri.parse(videoUrl))
//        req.apply {
//            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
//            setTitle("Downloading Video...")
//            setVisibleInDownloadsUi(true)
////            setDestinationInExternalFilesDir(requireContext(), Environment.DIRECTORY_DOWNLOADS,"100001.mp4")
//            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "10001.mp4")
//        }
//        manager.enqueue(req)

        Timber.d("videoUrl $videoUrl")
        val fileName = "video_" + System.currentTimeMillis() + ".mp4"
        val resolver = requireContext().applicationContext.contentResolver
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Video.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            )
        } else {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        }

//        MediaStore.Video.Media.

        // Publish a new song.
        val videoContentValue = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "$fileName")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }
        val uriSaveVideo = resolver.insert(collection, videoContentValue)
        if(uriSaveVideo==null){
            Timber.d("videoContentValue null")
        }

        try {
            Timber.d("videoContentValue try enter")
            uriSaveVideo?.let {
                Timber.d("videoContentValue uriSaveVideo")
                val pfd = requireContext().contentResolver.openFileDescriptor(it, "w")!!
                val output = FileOutputStream(pfd.fileDescriptor)
                val input = requireContext().contentResolver.openInputStream(Uri.parse(videoUrl))
                input.use {
                    output.use {
                        input?.copyTo(output)
                    }
                }
                pfd.close()
                input?.close()
                output.close()
                Timber.d("uriSaveVideo closed")
                videoContentValue.apply {
                    clear()
                    put(MediaStore.Video.Media.IS_PENDING, 0)
                    Timber.d("videoContentValue clear")
                }
                requireContext().contentResolver.update(uriSaveVideo, videoContentValue, null, null)
                binding.incShareOptions.pbDownloadVideo.visibility = GONE
            }
        }catch (e: Exception){
            Timber.e("error $e")
        }

    }

    private fun handleDownloadVideUrlOld(downloadFilePath: String) {
        if (downloadFilePath.isNotBlank())
            feedSlotsPagesManager.pause()

        Glide.with(requireContext())
            .asBitmap()
            .load(userImgUrl)
            .circleCrop()
            .apply(RequestOptions().override(250, 250))
            .addListener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    val outputFileDir = File(requireContext().cacheDir, "images").also {
                        if (!it.exists()) it.mkdirs()
                    }

                    val outputFile = File.createTempFile("image", ".png", outputFileDir)

                    val outputStream: FileOutputStream
                    try {
                        outputStream = FileOutputStream(outputFile)
                        resource?.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        outputStream.close();
                        Timber.d("Nikhil-> download image url -> ${outputFile.absolutePath}")
                        cropVideo(downloadFilePath, outputFile.absolutePath)
                    } catch (e: java.lang.Exception) {
                        Timber.e(e)

                    }

                    return false
                }

            }).submit()

    }

    private fun cropVideo(downloadFilePath: String, userImgPath: String) {
        val outputCropPath = getCropOutputFilePath()

        val cropCommand = mutableListOf(
            "-y",
            "-ss",
            "00:00:05",
            "-i",
            "",         // input file path[4]
            "-to",
            "00:00:17",
            "-async",
            "1",
            "-strict",
            "-2",
            "-c",
            "copy",
            ""          // output video file [13]
        )

        cropCommand[4] = downloadFilePath
        cropCommand[13] = outputCropPath

//        FFmpeg.getInstance(requireContext()).execute(cropCommand.toTypedArray(), object : ExecuteBinaryResponseHandler() {
//            override fun onStart() {
//                Timber.d("FFmepg-> onStart")
//            }
//
//            override fun onFinish() {
//                Timber.d("FFmepg-> onFinish")
//                addWatermarkOnVideo(outputCropPath, userImgPath)
//            }
//
//            override fun onSuccess(message: String?) {
//                Timber.d("FFmepg-> onSuccess message $message")
//            }
//
//            override fun onProgress(message: String?) {
//                Timber.d("FFmepg-> onProgress message $message")
//            }
//
//            override fun onFailure(message: String?) {
//                Timber.d("FFmepg-> onFailure message $message")
//            }
//        })
    }

    private fun addWatermarkOnVideo(inputFilePath: String, userImgPath: String) {
        val outputPath = getFinalOutputFilePath()
        val inputPath = inputFilePath
        val firstLogoPath = getFileFromAssets("logo1.png").absolutePath
        val secondLogoPath = getFileFromAssets("logo4.png").absolutePath
        val userImagePath = userImgPath
        val fontFilePath = getFileFromAssets("bold_text.otf").absolutePath

        Timber.d("outputPath $outputPath, inputPath $inputPath fontFilePath $fontFilePath")
        Timber.d("firstLogoPath $firstLogoPath, secondLogoPath $secondLogoPath, userImagePath $userImagePath")

        val mainFilter =
            "[0]scale=720:1280:force_original_aspect_ratio=increase,crop=720:1280[v1];[v1][1]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)*0.9:enable='between(t,0.1,14)',drawtext=fontfile=$fontFilePath:text='$userName':fontcolor=white:fontsize=26:box=1:boxcolor=black@0.5:boxborderw=5:x=(w-text_w)/2:y=(main_h-text_h)*0.891,fade=t=in:st=0:d=0.5[v2];[v2][2]overlay=0.1:0.1:enable='gt(t,14)'[v2];[v2][3]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)*0.24:enable='gt(t,14)',drawtext=fontfile=$fontFilePath:text='$userDisplayName':fontcolor=white:fontsize=30:shadowcolor=black:shadowx=5:shadowy=5:x=(w-text_w)/2:y=(main_h-text_h)*0.42:enable='gt(t,14)',fade=t=out:st=16.5:d=0.5,drawtext=fontfile=$fontFilePath:text='$userName':fontcolor=white@0.5:fontsize=24:shadowcolor=black:shadowx=2:shadowy=2:x=(w-text_w)/2:y=(main_h-text_h)*0.45:enable='gt(t,14)',fade=t=out:st=16.5:d=0.5[v3]"
//            "[0]scale=720:1280:force_original_aspect_ratio=increase,crop=720:1280[v1];[v1][1]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)*0.9:enable='between(t,0.1,14)',drawtext=fontfile=$fontFilePath:text='Stack Overflow':fontcolor=white:fontsize=26:box=1:boxcolor=black@0.5:boxborderw=5:x=(w-text_w)/2:y=(main_h-text_h)*0.891,fade=t=in:st=0:d=0.5[v2];[v2][2]overlay=0.1:0.1:enable='gt(t,14)'[v2];[v2][3]overlay=(main_w-overlay_w)/2:(main_h-overlay_h)*0.24:enable='gt(t,14)',drawtext=fontfile=$fontFilePath:text='$userName':fontcolor=white:fontsize=30:shadowcolor=black:shadowx=5:shadowy=5:x=(w-text_w)/2:y=(main_h-text_h)*0.42:enable='gt(t,14)',fade=t=out:st=6.5:d=0.5,drawtext=fontfile=$fontFilePath:text='@username':fontcolor=white@0.5:fontsize=24:shadowcolor=black:shadowx=2:shadowy=2:x=(w-text_w)/2:y=(main_h-text_h)*0.45:enable='gt(t,14)',fade=t=out:st=6.5:d=0.5[v3]"
        val command = mutableListOf(
            "-i", "",  // original video file [1]
            "-i", "",  // first singshala logo [3]
            "-i", "",  // full screen singshala logo [5]
            "-i", "",  // user image [7]
            "-filter_complex",
            mainFilter,
            "-map",
            "",         //[11]
            "-map",
            "0:a",
            ""          // output video file [14]
        )

        command[1] = inputPath
        command[3] = firstLogoPath
        command[5] = secondLogoPath
        command[7] = userImagePath
        command[11] = "[v3]"
        command[14] = outputPath

//        FFmpeg.getInstance(requireContext()).execute(command.toTypedArray(), object : ExecuteBinaryResponseHandler() {
//            override fun onStart() {
//                Timber.d("FFmepg-> onStart")
//            }
//
//            override fun onFinish() {
//                Timber.d("FFmepg-> onFinish")
//                binding.incShareOptions.pbDownloadVideo.visibility = GONE
//            }
//
//            override fun onSuccess(message: String?) {
//                Timber.d("FFmepg-> onSuccess message $message")
//            }
//
//            override fun onProgress(message: String?) {
//                Timber.d("FFmepg-> onProgress message $message")
//            }
//
//            override fun onFailure(message: String?) {
//                Timber.d("FFmepg-> onFailure message $message")
//            }
//        })

    }

    private fun getCropOutputFilePath(): String {
        val dir = File(requireContext().getExternalFilesDir("Output").toString())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val extension = ".mp4"
        val dest = File(dir.path + File.separator + "Output" + System.currentTimeMillis() / 1000L + extension)
        return dest.absolutePath
    }

    private fun getFinalOutputFilePath(): String {

//        val resolver = requireContext().contentResolver
//
//        val audioCollection =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                MediaStore.Audio.Media.getContentUri(
//                    MediaStore.VOLUME_EXTERNAL_PRIMARY
//                )
//            } else {
//                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//            }
//
//        val newSongDetails = ContentValues().apply {
//            put(MediaStore.Audio.Media.DISPLAY_NAME, "Output" + System.currentTimeMillis() / 1000L + ".mp4")
//        }
//
//        val downloadVideoUri = resolver
//            .insert(audioCollection, newSongDetails)
//

        val dir = File(requireContext().getExternalFilesDir("Downloads").toString())
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val extension = ".mp4"
        val dest = File(dir.path + File.separator + "Output" + System.currentTimeMillis() / 1000L + extension)
        return dest.absolutePath

//        val fileName = (System.currentTimeMillis() / 1000L).toString() + ".mp4"
//        val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
//        if (!dir.exists()) {
//            dir.mkdirs()
//        }
//        val extension = ".mp4"
//        val dest = File(dir.path + File.separator + "Output" + System.currentTimeMillis() / 1000L + extension)

    }

}