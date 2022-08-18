package com.sensibol.lucidmusic.singstr.gui.app.home

import android.animation.LayoutTransition
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import com.applozic.mobicomkit.Applozic
import com.applozic.mobicomkit.api.account.register.RegistrationResponse
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService
import com.applozic.mobicomkit.listners.AlLoginHandler
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.*
import com.sensibol.lucidmusic.singstr.gui.app.SingstrActivity
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.feed.generic.GenericFeedSlotsViewModel
import com.sensibol.lucidmusic.singstr.gui.app.learn.academy.LessonMiniTimingAdapter
import com.sensibol.lucidmusic.singstr.gui.app.learn.academy.LessonMiniTuneAdapter
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs


@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    override val layoutResId = R.layout.fragment_home
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentHomeBinding::inflate
    override val binding get() : FragmentHomeBinding = super.binding as FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private val genericFeedSlotVM: GenericFeedSlotsViewModel by activityViewModels()

    private var isLoginStreakShown = false
    //test comment dev_sanjeet branch

    internal lateinit var feedCoversAdapter1: FeedCoversAdapter
    internal lateinit var feedCoversAdapter2: FeedCoversAdapter
    internal lateinit var feedCoversAdapter3: FeedCoversAdapter
    internal lateinit var feedCoversAdapter4: FeedCoversAdapter
    internal lateinit var feedCoversAdapter5: FeedCoversAdapter

    internal lateinit var feedCoversAdapter: FeedCoversAdapter

    @Inject
    internal lateinit var lessonMinisAdapter: LessonMiniAdapterHome

    @Inject
    internal lateinit var carouselBannersAdapter: CarouselBannersAdapter

    @Inject
    internal lateinit var coverLessonGroupsAdapter: CoverLessonGroupsAdapter

    @Inject
    internal lateinit var lessonMiniTimingAdapter: LessonMiniTimingAdapter

    @Inject
    internal lateinit var lessonMiniTuneAdapter: LessonMiniTuneAdapter

    private var userId: String = "NA"

    override fun onInitViewModel() {
        singstrViewModel.apply {
            failure(failure, ::handleFailure)

//            loadDailyChallenge()
            loadUserStats()
            loadUserProfile()
        }
        viewModel.apply {
            observe(academyContent, ::showLessonContent)
//            observe(coverLessonData, ::ShowCoverLessonData)
            loadCarouselBanners()
            loadNewLessons()
            loadFeed()
            loadLearnContent()
            getUserRank()
//            loadCoverLessonData()
        }
    }

    private fun ShowCoverLessonData(coverLesson: CoverLesson) {
//        coverLessonGroupsAdapter.lessonGroups = coverLesson.lessonGroups
        coverLessonGroupsAdapter.conceptGroups = coverLesson.conceptGroups
        coverLessonGroupsAdapter.feedGroups = coverLesson.feed

    }

    private fun showLessonContent(academyContent: AcademyContent) {
        showRecommendedLesson(academyContent.recommendedLesson)
        lessonMiniTimingAdapter.lessons = academyContent.lessonTime.lessons
        lessonMiniTuneAdapter.lessons = academyContent.lessonTune.lessons
    }

    override fun onInitView() {

        feedCoversAdapter1 = FeedCoversAdapter()
        feedCoversAdapter2 = FeedCoversAdapter()
        feedCoversAdapter3 = FeedCoversAdapter()
        feedCoversAdapter4 = FeedCoversAdapter()
        feedCoversAdapter5 = FeedCoversAdapter()
        feedCoversAdapter = FeedCoversAdapter()

        binding.clRoot.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        Analytics.logEvent(
            Analytics.Event.HomePageViewEvent(
                Analytics.Event.Param.ScrollPercent("NA"),
            )
        )

        Analytics.facebookLogEvent(
            Analytics.Event.CompleteRegistrationEvent(
                Analytics.Event.Param.ScrollPercent("NA"),
            )
        )
        Analytics.facebookLogEvent(
            Analytics.Event.CompleteRegistrationNewEvent(
                Analytics.Event.Param.RegistrationMethod("Google"),
            )
        )

        singstrViewModel.apply {
            observe(dailyChallenge, ::showDailyChallenge)
            observe(userStats, ::showUserStats)
            observe(isTeacher, :: handleIsTeacher)
            observe(user) { user ->
                userId = user.id
                showUserProfile(user)
                loginMessagingUser(user)
                checkIsTeacher(user.id)
                if (!user.isOnBoarded) {
                    Timber.d("User has not on-boarded. Starting on-boarding.")
                    findNavController().navigate(HomeFragmentDirections.toOnBoardingNavGraph())
                    setUserOnBoarded(true)
                } else
                    viewModel.checkStreak()
            }
        }

        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(streakInfo, ::showStreakDialog)
            observe(coverLessonData, ::ShowCoverLessonData)
            observe(leaderUserRank, ::showUserRank)


//            loadCoverLessonData()

            binding.apply {
                incPracticeNow.root.visibility = GONE

                observe(carouselBanners) {
                    carouselBannersAdapter.banners = it
                    vpCarousel.visibility = if (it.isNotEmpty()) VISIBLE else GONE
                }
                observe(feed1) {
                    feedCoversAdapter1.covers = it.covers
                    grTrendingCovers.visibility = if (it.covers.isNotEmpty()) VISIBLE else GONE
                }
                observe(feed2) {
                    feedCoversAdapter2.covers = it.covers
                    grTrendingCovers.visibility = if (it.covers.isNotEmpty()) VISIBLE else GONE
                }
                observe(feed3) {
                    feedCoversAdapter3.covers = it.covers
                    grTrendingCovers.visibility = if (it.covers.isNotEmpty()) VISIBLE else GONE
                }
                observe(feed4) {
                    feedCoversAdapter4.covers = it.covers
                    grTrendingCovers.visibility = if (it.covers.isNotEmpty()) VISIBLE else GONE
                }
                observe(feed5) {
                    feedCoversAdapter5.covers = it.covers
                    grTrendingCovers.visibility = if (it.covers.isNotEmpty()) VISIBLE else GONE
                }
                observe(newLessons) {
                    lessonMinisAdapter.lessons = it
                    grNewLessons.visibility = if (it.isNotEmpty()) VISIBLE else GONE
                }
                observe(feed) {
                    feedCoversAdapter.covers = it.covers
                    grTrendingCovers.visibility = if (it.covers.isNotEmpty()) VISIBLE else GONE
                }
            }
        }

        coverLessonGroupsAdapter.onLessonClickListener = { showLessonDetailPage(it.id) }

        coverLessonGroupsAdapter.onCoverClickListener = { _, position ->
            viewModel.feed.value?.let { feed ->
                genericFeedSlotVM.setFeed(feed)
            }
            findNavController().navigate(HomeFragmentDirections.toGenericFeedFragment(position))
        }

        lessonMinisAdapter.onLessonClickListener = {
            (requireActivity() as SingstrActivity).selectBottomMenu(R.id.learnFragment)
            findNavController().navigate(HomeFragmentDirections.toLessonFragment(it.id))
        }

        lessonMiniTimingAdapter.onLessonClickListener = {
            findNavController().navigate(HomeFragmentDirections.toLessonFragment(it.id))
        }

        lessonMiniTuneAdapter.onLessonClickListener = {
            findNavController().navigate(HomeFragmentDirections.toLessonFragment(it.id))
        }

        feedCoversAdapter1.onCoverClickListener = { _, position ->
            viewModel.feed1.value?.let { feed ->
                genericFeedSlotVM.setFeed(feed)
            }
            findNavController().navigate(HomeFragmentDirections.toGenericFeedFragment(position))
        }

        feedCoversAdapter2.onCoverClickListener = { _, position ->
            viewModel.feed2.value?.let { feed ->
                genericFeedSlotVM.setFeed(feed)
            }
            findNavController().navigate(HomeFragmentDirections.toGenericFeedFragment(position))
        }

        feedCoversAdapter3.onCoverClickListener = { _, position ->
            viewModel.feed3.value?.let { feed ->
                genericFeedSlotVM.setFeed(feed)
            }
            findNavController().navigate(HomeFragmentDirections.toGenericFeedFragment(position))
        }

        feedCoversAdapter4.onCoverClickListener = { _, position ->
            viewModel.feed4.value?.let { feed ->
                genericFeedSlotVM.setFeed(feed)
            }
            findNavController().navigate(HomeFragmentDirections.toGenericFeedFragment(position))
        }

        feedCoversAdapter5.onCoverClickListener = { _, position ->
            viewModel.feed5.value?.let { feed ->
                genericFeedSlotVM.setFeed(feed)
            }
            findNavController().navigate(HomeFragmentDirections.toGenericFeedFragment(position))
        }

        coverLessonGroupsAdapter.onAllViewLessonClickListener = { conceptId ->
            findNavController().navigate(HomeFragmentDirections.toConceptLessonListFragment(conceptId))
        }

        binding.apply {
            ivMessage.visibility = GONE
            tvMsgUnreadCount.visibility = GONE
            ivNotification.visibility = GONE
            tvNotificationUnreadCount.visibility = GONE
//            tvUserLevel.visibility = GONE
            val practiceXPLabel = SpannableStringBuilder(getString(R.string.practice_for_15_mins_to_get_100xp))
            practiceXPLabel.setSpan(
                ForegroundColorSpan(Color.parseColor("#5DB427")),
                28,
                practiceXPLabel.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            vpCarousel.apply {
                adapter = carouselBannersAdapter
                clipToPadding = false
                clipChildren = false
                offscreenPageLimit = 3
                setPageTransformer(CompositePageTransformer().apply {
                    addTransformer(MarginPageTransformer(resources.getDimension(R.dimen.home_carousel_banner_margin).toInt()))
                    addTransformer { page, position -> page.scaleY = 0.85f + (1 - abs(position)) * 0.15f }
                })
            }

            carouselBannersAdapter.onBannerClickListener = { banner ->
                Analytics.logEvent(
                    Analytics.Event.HeroBannerClickEvent(
                        Analytics.Event.Param.BannerId(banner.id),
                    )
                )
                when {
                    banner.deepLinkUrl.contains("www.singshala.com/app") -> {
                        findNavController().navigate(Uri.parse(banner.deepLinkUrl))
                    }
                    banner.deepLinkUrl.contains("singshala.page.link") -> {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(banner.deepLinkUrl))
                        startActivity(intent)
                    }
                    else -> {
                        findNavController().navigate(HomeFragmentDirections.toWebViewFragment(banner.deepLinkUrl))
                    }
                }
            }

            rvTrendingCoversOne.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = feedCoversAdapter1
            }

            rvTrendingCoversTwo.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = feedCoversAdapter2
            }

            rvTrendingCoversThree.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = feedCoversAdapter3
            }

            rvTrendingCoversFour.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = feedCoversAdapter4
            }

            rvTrendingCoversFive.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = feedCoversAdapter5
            }
            rvTimingLesson.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = lessonMiniTimingAdapter
            }

            rvTuneLesson.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = lessonMiniTuneAdapter
            }

            tvViewTimingLesson.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.toLessonListFragment(LessonListType.TIMING_LESSON.toString()))
            }

            tvViewTuneLesson.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.toLessonListFragment(LessonListType.TUNE_LESSON.toString()))
            }

//            tvViewAllCoversButton.setOnClickListener {
//                viewModel.feed.value?.let { feed ->
//                    genericFeedSlotVM.setFeed(feed)
//                }
//                Analytics.logEvent(Analytics.Event.ViewAllCoversEvent(Analytics.Event.Param.UserId(userId)))
//                findNavController().navigate(HomeFragmentDirections.toGenericFeedFragment())
//            }

            incPracticeNow.apply {
                tvPracticeButton.setBackgroundResource(R.drawable.bg_btn_outline_only)
                tvPracticeXpLabel.text = practiceXPLabel
                pbPracticeRemainingTime.max = SystemProperties.MAX_PRACTICE_DURATION_MS
                tvPracticeButton.setOnClickListener {
                    Analytics.logEvent(Analytics.Event.HomePracticeCTAEvent(Analytics.Event.Param.UserId(userId)))
                    findNavController().navigate(HomeFragmentDirections.toPrepareSingHostFragment(SingMode.PRACTICE.argName))
                }
            }

            tvFirstSingButton.setOnClickListener {
                Analytics.logEvent(Analytics.Event.SingSongButtonEvent(Analytics.Event.Param.UserId(userId)))
                findNavController().navigate(HomeFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName))
            }

            tvFirstLearnButton.setOnClickListener {
                Analytics.logEvent(Analytics.Event.HomeLearnButtonEvent(Analytics.Event.Param.UserId(userId)))
                findNavController().navigate(HomeFragmentDirections.toLearnFragment())
            }

            vEnhanceTuneCardBg.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.toLearnFragment("Tune"))
            }

            vEnhanceTimingCardBg.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.toLearnFragment("Timing"))
            }

            /*tvLevelClick.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.ProfileViewEvent(Analytics.Event.Param.UserId(userId))
                )
                findNavController().navigate(HomeFragmentDirections.toProfileFragment())
            }*/

            ivMessage.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.MessageViewEvent()
                )
//                findNavController().navigate(HomeFragmentDirections.toMessageFragment())
                val conversationsIntent = Intent(context, ConversationActivity::class.java)
                requireContext().startActivity(conversationsIntent)
            }

            ivNotification.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.toNotificationsFragment())
            }

            tvViewAllNewLessons.setOnClickListener {
                Analytics.logEvent(Analytics.Event.ViewAllLessonsEvent(Analytics.Event.Param.UserId(userId)))
                findNavController().navigate(HomeFragmentDirections.toLearnFragment())
            }

            tvSingButton.setOnClickListener {
                Analytics.logEvent(Analytics.Event.SingSongButtonEvent(Analytics.Event.Param.UserId(userId)))
                findNavController().navigate(HomeFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName))
            }

            tvSingNowBtn.setOnClickListener {
                Analytics.logEvent(Analytics.Event.SingSongButtonEvent(Analytics.Event.Param.UserId(userId)))
                findNavController().navigate(HomeFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName))
            }

            rvNewLessons.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = lessonMinisAdapter
            }

            tvLearnButton.setOnClickListener {
                Analytics.logEvent(Analytics.Event.HomeLearnButtonEvent(Analytics.Event.Param.UserId(userId)))
                findNavController().navigate(HomeFragmentDirections.toLearnFragment())
            }

            incAppFeature.tvAppFeatures.setOnClickListener {
                Analytics.logEvent(Analytics.Event.ViewAppFeaturesEvent(Analytics.Event.Param.UserId(userId)))
                findNavController().navigate(HomeFragmentDirections.toExclusiveFeaturesFragment())
            }

            rvCoverLessonGroups.apply {
                adapter = coverLessonGroupsAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    private fun showUserRank(rank: Int) {
        binding.apply {
            incUserStatics.tvRank.text = "$rank th"

//            if (rank <= 100) {
//                incUserStatics.clRank.visibility = VISIBLE
//                incUserStatics.tvRank.text = rank.toString()
//            }
//            else
//                incUserStatics.clRank.visibility = GONE
        }
    }

    private fun showLessonDetailPage(lessonId: String) {
        findNavController().navigate(HomeFragmentDirections.toLearnDetailFragment(lessonId))
    }

    private val actionSwitchCarouselBanner: Runnable = Runnable {
        binding.vpCarousel.apply {
            currentItem = if (currentItem == carouselBannersAdapter.banners.size - 1) 0 else currentItem + 1
        }
        scheduleCarouselBannerSwitch()
    }

    private val carouselPageChangeCallback: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            when (state) {
                SCROLL_STATE_IDLE -> scheduleCarouselBannerSwitch()
                else -> binding.vpCarousel.removeCallbacks(actionSwitchCarouselBanner)
            }
        }
    }

    override fun onPause() {
        binding.vpCarousel.apply {
            removeCallbacks(actionSwitchCarouselBanner)
            unregisterOnPageChangeCallback(carouselPageChangeCallback)
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        scheduleCarouselBannerSwitch()
        binding.vpCarousel.apply {
            registerOnPageChangeCallback(carouselPageChangeCallback)
        }
    }

    private fun scheduleCarouselBannerSwitch() {
        binding.vpCarousel.apply {
            removeCallbacks(actionSwitchCarouselBanner)
            postDelayed(actionSwitchCarouselBanner, BANNER_SWITCH_TIME_MS)
        }
    }

    companion object {
        const val BANNER_SWITCH_TIME_MS = 5000L
    }

    private fun showDailyChallenge(dailyChallenge: DailyChallenge) {
        binding.apply {
            grDailyChallenge.visibility = VISIBLE

            val song = dailyChallenge.song
            tvDailyChallengeSongTitle.text = song.title
            tvDailyChallengeSongSubtitle.text = song.artists.first().name
            ivDailyChallengeSongThumbnail.loadUrl(song.thumbnailUrl)
            tvDailyChallengeSingButton.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.DailyChallengeAttemptEvent(
                        Analytics.Event.Param.SongId(song.title),
                        Analytics.Event.Param.GenreId("NA"),
                        Analytics.Event.Param.ArtistId(song.artists.names),
                    )
                )
                findNavController().navigate(HomeFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName, song.id))
            }
        }
    }

    private fun showUserProfile(user: User) {
        binding.apply {
            ivMessage.visibility = VISIBLE
            ivNotification.visibility = VISIBLE
//            tvUserLevel.visibility = VISIBLE
//            tvUserName.text = user.name.capitalize(Locale.ROOT)
//            ivUserImage.loadCenterCropImageFromUrl(user.dpUrl)
            incUserStatics.ivUserImage.loadCenterCropImageFromUrl(user.dpUrl)
        }
    }

    private fun showUserStats(userStats: UserStats) {
        Analytics.setUserProperty(Analytics.UserProperty.AvgTuneScore(userStats.averageTuneScore.toInt()))
        Analytics.setUserProperty(Analytics.UserProperty.AvgTimeScore(userStats.averageTimeScore.toInt()))


        Timber.d("showUserStats: $userStats")
        binding.apply {

            userStats.apply {
                /*if (0 < coversCount || 0 < draftsCount) {
                    clUserStatsContainer.visibility = VISIBLE
                    clCreateFirstCoverContainer.visibility = GONE
                } else {
                    clUserStatsContainer.visibility = GONE
                    clCreateFirstCoverContainer.visibility = VISIBLE
                }*/
                /*tvUserLevel.text = level.toString()
                pbUserXpProgress.isIndeterminate = false
                (totalXp + remainingNextXp).let { nextLevelXp ->
                    if (0 == nextLevelXp) {
                        pbUserXpProgress.progress = 0
                    } else {
                        pbUserXpProgress.setProgress(remainingNextXp * 100 / nextLevelXp, true)
                    }
                }*/


                val avgScore = ((averageTuneScore + averageTimeScore) * 0.5).toInt()

                incUserStatics.apply {
                    clAvgScore.apply {
                        //only need to set timing score, tune score is set in background drawable of pb
                        visibility = if(avgScore == 0) GONE else VISIBLE
                        pbTune.max = avgScore
                        pbTune.isIndeterminate = false
                        pbTune.setProgress((averageTimeScore*0.5).toInt(), true)
                        tvAvgScore.text = avgScore.toString()
                        tvTiming.text = averageTimeScore.toInt().toString()
                        tvTune.text = averageTuneScore.toInt().toString()
                    }
                    clWatchTime.apply {
                        visibility = if(viewDurationMS.toInt() == 0) GONE else VISIBLE
                        val formattedTime = getFormattedContentViewTime(viewDurationMS)
                        tvContentViewTime.text = formattedTime
                        val formatter = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
                        val date = Time(viewDurationMS)
                        val result = formatter.format(date).toString()
                        val hour = result.split(':')
                        var digit1 = "00"
                        var digit2 = "00"
                        var matrix1 = "Hour"
                        var matrix2 = "Min"

                        if(hour[0] == "00"){
                            matrix1 = "Min"
                        }
                        else{
                            digit1 = hour[0]
                            digit2 = hour[1]
                        }

                        if(matrix1 == "Min"){
                            digit1 = hour[1]
                            digit2 = hour[2]
                            matrix2 = "Sec"
                        }

                        tvTimingDigitOne.text = digit1
                        tvTimingMatrix1.text = matrix1
                        tvTimingDigitTwo.text = digit2
                        tvTimingMatrix2.text = matrix2
                        Timber.d("viewDurationMS result $result  $hour")
                        Analytics.setUserProperty(Analytics.UserProperty.UserContentViewTime(date))
                    }
                }

                if (0 < coversCount || 0 < draftsCount)
                    flUserStatics.visibility = VISIBLE
                else
                    flUserStatics.visibility = GONE

//                tvAvgTuneScore.text = averageTuneScore.toInt().toString()
//                pbAvgTuneScore.isIndeterminate = false
//                pbAvgTuneScore.setProgress(averageTuneScore.toInt(), true)
//                tvAvgTimingScore.text = averageTimeScore.toInt().toString()
//                pbAvgTimingScore.isIndeterminate = false
//                pbAvgTimingScore.setProgress(averageTimeScore.toInt(), true)
                remainingPracticeTimeMS.let {
                    incPracticeNow.apply {
                        val practiceTime = SystemProperties.MAX_PRACTICE_DURATION_MS - it
                        pbPracticeRemainingTime.apply {
                            progress = if (practiceTime < 6000)
                                500
                            else
                                practiceTime
                        }
                        tvFrgMainPracticeScore.text = (practiceTime / (1_000 * 60)).toString()
                    }
                }
//                var formattedTime = getFormattedContentViewTime(viewDurationMS)
//                tvContentViewTime.text = formattedTime
//                val formatter = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
//
//                val date = Time(viewDurationMS)
//                val result = formatter.format(date)
//                Timber.d("paste result here $result")
//                Analytics.setUserProperty(Analytics.UserProperty.UserContentViewTime(date))
            }
        }
    }

    private fun handleIsTeacher(checkTeacher: CheckTeacher){
        binding.incPracticeNow.root.visibility = if(checkTeacher.teacherId=="false") VISIBLE else GONE
    }

    private fun getFormattedContentViewTime(viewDurationMS: Long): String {
        var formattedContentViewTime = ""

        val hours = viewDurationMS / (1_000 * 60 * 60)
        formattedContentViewTime += if (hours == 0L) "" else hours.toString().padStart(2, '0') + "h "

        val minutes = (viewDurationMS % (1_000 * 60 * 60)) / (1_000L * 60)
        formattedContentViewTime += minutes.toString().padStart(2, '0') + "m "

        val seconds = (viewDurationMS / 1_000) % 60
        formattedContentViewTime += seconds.toString().padStart(2, '0') + "s"

        return formattedContentViewTime
    }

    //old
    /*private fun showStreakDialog(streakInfo: StreakInfo) {
        if (isLoginStreakShown)
            return

        isLoginStreakShown = true
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_daily_login, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        dialogView.findViewById<TextView>(R.id.tv_daily_earned_xps).setText(
            "You got ${streakInfo.xp}XP as a daily login reward"
        )
        dialogView.findViewById<TextView>(R.id.tv_daily_earned_xps_method).setText(
            "Login Everyday for 10 days straight to receive ${(streakInfo.xp) * 25} XP Bonus"
        )
        val days = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        val streakDaySlab = (streakInfo.day / 10).toInt() * 10
        days.forEach {
            it + streakDaySlab
        }

        dialogView.findViewById<TextView>(R.id.tv_day_1).text = "Day ${days[0]}"
        dialogView.findViewById<TextView>(R.id.tv_day_2).text = "Day ${days[1]}"
        dialogView.findViewById<TextView>(R.id.tv_day_3).text = "Day ${days[2]}"
        dialogView.findViewById<TextView>(R.id.tv_day_4).text = "Day ${days[3]}"
        dialogView.findViewById<TextView>(R.id.tv_day_5).text = "Day ${days[4]}"
        dialogView.findViewById<TextView>(R.id.tv_day_6).text = "Day ${days[5]}"
        dialogView.findViewById<TextView>(R.id.tv_day_7).text = "Day ${days[6]}"
        dialogView.findViewById<TextView>(R.id.tv_day_8).text = "Day ${days[7]}"
        dialogView.findViewById<TextView>(R.id.tv_day_9).text = "Day ${days[8]}"
        dialogView.findViewById<TextView>(R.id.tv_day_10).text = "Day ${days[9]}"

        var bonusXp: Int

        var dayXp: Int
        if (streakInfo.xp % 500 == 0) {
            dayXp = streakInfo.xp / 25
            bonusXp = streakInfo.xp
        } else {
            dayXp = streakInfo.xp
            bonusXp = streakInfo.xp * 25
        }

        Timber.d("bonusXp $bonusXp  dayXp $dayXp")
        dialogView.findViewById<CheckBox>(R.id.cb_day_1_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[0] <= streakInfo.day
        }
        dialogView.findViewById<CheckBox>(R.id.cb_day_2_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[1] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_3_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[2] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_4_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[3] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_5_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[4] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_6_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[5] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_7_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[6] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_8_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[7] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_9_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[8] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_10_xp).apply {
            text = "${bonusXp}\n"
            isChecked = days[9] <= streakInfo.day
        }

        val alertDialog = mBuilder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.tv_btn_earn_more_xp).setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigate(HomeFragmentDirections.toEarnXpFragment())
        }
    }
*/

    //new
    private fun showStreakDialog(streakInfo: StreakInfo) {
        if (isLoginStreakShown)
            return

        isLoginStreakShown = true
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_daily_login, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        dialogView.findViewById<TextView>(R.id.tv_daily_earned_xps).setText(
            "You got ${streakInfo.xp}XP as a daily login reward"
        )
        dialogView.findViewById<TextView>(R.id.tv_daily_earned_xps_method).setText(
            "Login Everyday for 10 days straight to receive ${(streakInfo.xp) * 25} XP Bonus"
        )
        val days = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

        val streakDaySlab = (streakInfo.day / 10).toInt() * 10
        days.forEach {
            it + streakDaySlab
        }

        dialogView.findViewById<TextView>(R.id.tv_day_1).text = "Day ${days[0]}"
        dialogView.findViewById<TextView>(R.id.tv_day_2).text = "Day ${days[1]}"
        dialogView.findViewById<TextView>(R.id.tv_day_3).text = "Day ${days[2]}"
        dialogView.findViewById<TextView>(R.id.tv_day_4).text = "Day ${days[3]}"
        dialogView.findViewById<TextView>(R.id.tv_day_5).text = "Day ${days[4]}"
        dialogView.findViewById<TextView>(R.id.tv_day_6).text = "Day ${days[5]}"
        dialogView.findViewById<TextView>(R.id.tv_day_7).text = "Day ${days[6]}"
        dialogView.findViewById<TextView>(R.id.tv_day_8).text = "Day ${days[7]}"
        dialogView.findViewById<TextView>(R.id.tv_day_9).text = "Day ${days[8]}"
        dialogView.findViewById<TextView>(R.id.tv_day_10).text = "Day ${days[9]}"

        var bonusXp: Int

        var dayXp: Int
        if (streakInfo.xp % 500 == 0) {
            dayXp = streakInfo.xp / 25
            bonusXp = streakInfo.xp
        } else {
            dayXp = streakInfo.xp
            bonusXp = streakInfo.xp * 25
        }

        Timber.d("bonusXp $bonusXp  dayXp $dayXp")
        dialogView.findViewById<CheckBox>(R.id.cb_day_1_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[0] <= streakInfo.day
        }
        dialogView.findViewById<CheckBox>(R.id.cb_day_2_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[1] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_3_xp).apply {
            text = "$dayXp}\n"
            isChecked = days[2] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_4_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[3] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_5_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[4] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_6_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[5] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_7_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[6] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_8_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[7] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_9_xp).apply {
            text = "${dayXp}\n"
            isChecked = days[8] <= streakInfo.day
        }

        dialogView.findViewById<CheckBox>(R.id.cb_day_10_xp).apply {
            text = "${bonusXp}\n"
            isChecked = days[9] <= streakInfo.day
        }

        val alertDialog = mBuilder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<ImageView>(R.id.ivClose).setOnClickListener {
            alertDialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.tv_btn_earn_more_xp).setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigate(HomeFragmentDirections.toEarnXpFragment())
        }
    }

    private fun loginMessagingUser(mainUser: User) {
        Timber.d("loginMessagingUser IN")
        if (Applozic.isConnected(requireContext())) {
            setupMsgUnreadCount()
            return
        }
        val user = com.applozic.mobicomkit.api.account.user.User()
        user.userId = mainUser.id // userId it can be any unique user identifier NOTE : +,*,? are not allowed chars in userId.
        user.displayName = mainUser.name // displayName is the name of the user which will be shown in chat message
        user.authenticationTypeId =
            com.applozic.mobicomkit.api.account.user.User.AuthenticationType.APPLOZIC.value //User.AuthenticationType.APPLOZIC.getValue() for password verification from Applozic server and User.AuthenticationType.CLIENT.getValue() for access Token verification from your server set access token as password
        user.password = mainUser.id // Optional, Pass the password
        user.imageLink = mainUser.dpUrl // Optional, If you have public image URL of user you can pass here

        Applozic.connectUser(context, user, object : AlLoginHandler {
            override fun onSuccess(registrationResponse: RegistrationResponse?, context: Context?) {
                Timber.d("loginMessagingUser registration $registrationResponse?.message")
            }

            override fun onFailure(
                registrationResponse: RegistrationResponse?,
                exception: Exception?
            ) {
                // If any failure in registration the callback  will come here
                if (registrationResponse != null) {
                    Timber.d("loginMessagingUser Login as been failed:%s", registrationResponse.message)
                } else if (exception != null) {
                    Timber.d("loginMessagingUser Login as been failed due to exception:  $exception.localizedMessage")
                }
            }
        })

        setupMsgUnreadCount()
    }

    private fun setupMsgUnreadCount() {
        val msgCount = MessageDatabaseService(context).totalUnreadCount
        Timber.d("setupMsgUnreadCount count: $msgCount")
        binding.tvMsgUnreadCount.apply {
            visibility = if (msgCount > 0) VISIBLE else GONE
//            text = when {
//                msgCount <= 100 -> {
//                    msgCount.toString()
//                }
//                else -> "99"
//            }
        }
    }

    private fun showRecommendedLesson(recommendedLesson: LessonMini) {
        binding.apply {
            groupRecommended.visibility = VISIBLE
            incRecommendedLesson.apply {
                tvTitle.text = recommendedLesson.title
                tvType.text = recommendedLesson.type
                tvDifficulty.text = recommendedLesson.difficulty
                tvDuration.text = recommendedLesson.duration
                tvType.text = recommendedLesson.type
                if (recommendedLesson.subscriptionType == "Free") {
                    tvFreeLabel.text = recommendedLesson.subscriptionType
                } else {
                    tvFreeLabel.visibility = View.INVISIBLE
                }
                ivLessonThumbnail.loadUrl(recommendedLesson.thumbnailUrl)

                ivLessonThumbnail.setOnClickListener {
                    findNavController().navigate(HomeFragmentDirections.toLessonFragment(recommendedLesson.id))
                }
            }
        }
    }

    private fun showFollowingFeed(followingFeed: FollowingFeed) {
        Timber.e("Following Feeds are :: " + followingFeed.covers)
        Timber.e("Following Feeds are :: " + followingFeed.nextPageToken)
        Timber.e("Following Feeds are :: " + followingFeed.covers.get(0).songMini.lyrics)
    }

}