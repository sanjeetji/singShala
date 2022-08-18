package com.sensibol.lucidmusic.singstr.gui.app.learn.lesson

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrActivity
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentLessonBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@AndroidEntryPoint
internal class LessonFragment : BaseFragment() {

    override val layoutResId = R.layout.fragment_lesson
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentLessonBinding::inflate
    override val binding: FragmentLessonBinding get() = super.binding as FragmentLessonBinding

    @Inject
    lateinit var recommendedSongsAdapter: RecommendedSongsAdapter

    @Inject
    lateinit var relatedSongsAdapter: RelatedSongsAdapter

    @Inject
    lateinit var vocalExercisesAdapter: VocalExercisesAdapter

    private val viewModel: LessonViewModel by viewModels()

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private val args: LessonFragmentArgs by navArgs()

    private var actionHandler: Handler = Handler()

    private lateinit var exoPlayer: SimpleExoPlayer

    private var lesson: Lesson? = null

    var teacherId: String? = null

    private var isProLessonProgressCheck = true
    private var isProLesson = false
    private var isUserPro = false
    var fullscreen: Boolean = false
    var openProDialog: Boolean = false

    override fun onInitViewModel() {
        super.onInitViewModel()
        viewModel.getLesson(args.lessonId)
    }

    override fun onInitView() {

        (requireActivity() as SingstrActivity).selectBottomMenu(R.id.learnFragment)
        binding.apply {
            pbFollow.visibility = GONE
            tvRelatedSongs.setOnClickListener {
                showSongList()
            }
            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
            shareButton.setOnClickListener {
                lesson?.let { lesson ->
                    Analytics.logEvent(
                        Analytics.Event.ShareLessonEvent(
                            Analytics.Event.Param.LessonId(lesson.title),
                            Analytics.Event.Param.LessonOwner(lesson.teacher.name),
                            Analytics.Event.Param.LessonCategory(lesson.type),
                            Analytics.Event.Param.LessonCollection("NA"),
                            Analytics.Event.Param.LessonStatus(lesson.subscriptionType)
                        )
                    )
                }

                shareLessonLink()
            }

            cbFollow.isEnabled = false
            incUpNext.ivPlayPause.isEnabled = false
            buttonAddToMyList.isEnabled = false
        }

        vocalExercisesAdapter.onExerciseClickListener = {
            if (!isUserPro && isProLesson) {
                exoPlayer.pause()
                showProSubscriptionDialog()
            } else {

                findNavController().navigate(
                    LessonFragmentDirections.toExerciseLauncherFragment(it, lesson!!)
                )
            }
        }

        relatedSongsAdapter.onPracticeSongClickListener = { songMini ->
            findNavController().navigate(LessonFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName, songMini.id))
        }

        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(lesson, ::showLessonDetail)
            observe(isAddedToMyList, ::updateAddToMyListButtonState)
            observe(progressCheck, ::showProSubscription)
            observe(reportLesson, ::showReportLessonResult)
            observe(reportLessonItems, ::showReportLessonItems)
            observe(teacherDetails, ::showTeacherDetails)
            observe(isSubscribed, ::handleSubscribed)
            observe(isUnSubscribed, ::handleUnsubscribed)
            observe(isAlreadySubscribed, ::handleAlreadySubscribed)

        }

        singstrViewModel.apply {
            failure(failure, ::handleFailure)
            observe(userSubscription, ::showUserSubscription)
            getUserSubscription()
        }
    }

    private fun showTeacherDetails(teacherDetails: TeacherDetails) {
        binding.apply {
            Timber.d("teacherDetails id ${teacherDetails.id}")
            if (teacherDetails.userId.isNotBlank() && teacherDetails.userId == singstrViewModel.user.value?.id) {
                Timber.d("teacherDetails user_id ${teacherDetails.userId}")
                groupTeacherDetails.visibility = GONE
                return
            }

            val teacherUserId = teacherDetails.userId
            viewModel.checkIsAlreadySubscribe(teacherUserId)
            incUpNext.ivPlayPause.isEnabled = true
            buttonAddToMyList.isEnabled = true
            ivProfilePic.loadUrl(teacherDetails.profile_img_url)
            tvArtistName.text = teacherDetails.name

            vTeacher.setOnClickListener {
//                findNavController().navigate(LessonFragmentDirections.toTeacherDetailFragment(lesson!!.teacher.id))
                if (teacherUserId.isNotBlank())
                    findNavController().navigate(LessonFragmentDirections.toOtherUserProfileFragment(teacherUserId))
            }

            cbFollow.setOnCheckedChangeListener { _, isChecked ->
                if (teacherUserId.isNotBlank()) {
                    if (isChecked) {
                        pbFollow.visibility = VISIBLE
                        viewModel.subscribeUser(teacherUserId)
                    } else {
                        pbFollow.visibility = VISIBLE
                        viewModel.deleteSubscribeUser(teacherUserId)
                    }
                }
            }
        }
    }

    private fun updateAddToMyListButtonState(isAddedToList: Boolean) {
        binding.apply {
            when (isAddedToList) {
                true -> {
                    buttonAddToMyList.text = getString(R.string.added_to_list)
                    buttonAddToMyList.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0)
                    buttonAddToMyList.isEnabled = false
                }
                false -> {
                    buttonAddToMyList.text = getString(R.string.add_to_my_list)
                    buttonAddToMyList.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_added, 0, 0, 0)
                    buttonAddToMyList.isEnabled = true
                }
            }
        }
    }

    private fun showUserSubscription(proSubscription: ProSubscription) {
        isUserPro = proSubscription.subscribed
    }

    private fun showProSubscription(isDialogDisplayed: Boolean) {
        binding.apply {
            exoPlayer.pause()
            actionHandler.removeCallbacks(exoPlayerTimerUpdater)
        }
    }

    private fun showReportLessonResult(isLessonReported: Boolean) {
        Timber.d("showRepostLessonResult: Reported $isLessonReported")

    }

    private fun showProSubscriptionDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialogue_become_pro, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        val tvBecomePro = dialogView.findViewById<TextView>(R.id.tvBecomePro)
        val ivClose = dialogView.findViewById<ImageView>(R.id.ivClose)
        val alertDialog = mBuilder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tvBecomePro.setOnClickListener {
            findNavController().navigate(LessonFragmentDirections.toProSubscriptionPlanFragment())
            Analytics.logEvent(
                Analytics.Event.CheckProDetailsEvent(
                    Analytics.Event.Param.ProUserId("NA")
                )
            )
            alertDialog.dismiss()
        }
        ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
    }

    private fun hideAfterDelay(view: View) {
        val delay = 3000L // 3 seconds
        view.postDelayed({ view.visibility = View.GONE }, delay)
    }

    private fun showDescription(description: String) {
        with(BottomSheetDialog(binding.root.context, R.style.AppBottomSheetDialogTheme)) {
            setContentView(R.layout.bottomsheet_description)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            findViewById<TextView>(R.id.tvDescription)?.apply {
                this.text = description
            }
            findViewById<ImageView>(R.id.ivClose)?.setOnClickListener {
                this.dismiss()
            }
            show()
        }
    }

    private fun showSongList() {
        with(BottomSheetDialog(binding.root.context, R.style.AppBottomSheetDialogTheme)) {
            setContentView(R.layout.bottomsheet_song_list)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            findViewById<ImageView>(R.id.ivClose)?.setOnClickListener {
                this.dismiss()
            }
            findViewById<RecyclerView>(R.id.rvSongs)?.apply {
                this.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = recommendedSongsAdapter
            }
            show()
        }
    }

    private fun showLessonDetail(lesson: Lesson) {
        this.lesson = lesson
        teacherId = lesson.teacher.id
        Timber.d("teacher id $teacherId")
        viewModel.loadTeacherDetails(teacherId)

        Analytics.logEvent(
            Analytics.Event.LessonViewEvent(
                Analytics.Event.Param.LessonId(lesson.title),
                Analytics.Event.Param.LessonOwner(lesson.teacher.name),
                Analytics.Event.Param.LessonCategory(lesson.type),
                Analytics.Event.Param.LessonCollection("NA"),
                Analytics.Event.Param.LessonStatus(lesson.subscriptionType)
            )
        )

        Analytics.facebookLogEvent(
            Analytics.Event.CompleteTutorialEvent(
                Analytics.Event.Param.LessonId(lesson.title),
                Analytics.Event.Param.LessonOwner(lesson.teacher.name),
                Analytics.Event.Param.LessonCategory(lesson.type),
                Analytics.Event.Param.LessonCollection("NA"),
                Analytics.Event.Param.LessonStatus(lesson.subscriptionType)
            )
        )


        binding.apply {
//            tvDuration.text = lesson.duration
            tvTitle.text = lesson.title
            tvType.text = lesson.type
            tvDifficulty.text = lesson.difficulty
//            tvArtistName.text = lesson.teacher.name
//            ivProfilePic.loadUrl(lesson.teacher.profileImgUrl)
            incUpNext.ivMusic.loadUrl(lesson.nextLesson.thumbnailUrl)
            incUpNext.tvLessonName.text = lesson.nextLesson.title
            if (lesson.subscriptionType == "Paid") {
                isProLesson = true
                binding.ivProTag.visibility = VISIBLE
            } else {
                isProLesson = false
                binding.ivProTag.visibility = GONE
            }

            if (lesson.exercises.isNotEmpty()) {
                rvVocalExcercise.visibility = VISIBLE
                val modifyExercise = mutableListOf<Exercise>()
                lesson.exercises.forEach {
                    if (it.status == "Avail")
                        modifyExercise.add(it)
                }
                if (modifyExercise.isNotEmpty())
                    vocalExercisesAdapter.exercises = modifyExercise

                rvVocalExcercise.layoutManager = LinearLayoutManager(context)
                rvVocalExcercise.adapter = vocalExercisesAdapter
            } else {
                rvVocalExcercise.visibility = GONE
            }

            if (lesson.relatedSongs.isNotEmpty()) {
                recommendedSongsAdapter.songs = lesson.relatedSongs
                rvPracticeSongs.visibility = VISIBLE
            } else {
                rvPracticeSongs.visibility = GONE
            }
//            if (lesson.relatedSongs.isNotEmpty()) {
//                clRelatedSong.visibility = VISIBLE
//                if (lesson.relatedSongs.size > 1) {
//                    tvRelatedSongs.text = "${lesson.relatedSongs.size} songs related to this lesson"
//                } else tvRelatedSongs.text = "${lesson.relatedSongs.size} song related to this lesson"
//                recommendedSongsAdapter.songs = lesson.relatedSongs
//            } else {
//                clRelatedSong.visibility = GONE
//            }
            updateAddToMyListButtonState(lesson.isAddedToMyList)
            ivLessonThumbnail.loadUrl(lesson.thumbnailUrl)

            exoPlayer = SimpleExoPlayer.Builder(requireContext())
                .setTrackSelector(
                    DefaultTrackSelector(requireContext()).also {
                        it.parameters = it.buildUponParameters()
                            .setForceLowestBitrate(true)
                            .build()
                    }
                ).build()
            val mediaItem = MediaItem.Builder()
                .setUri(lesson.videoUrl)
                .setMimeType(MimeTypes.APPLICATION_M3U8)
            exoPlayer.setMediaItem(mediaItem.build())
            exoPlayer.prepare()
            exoPlayer.play()
            vvLesson.player = exoPlayer

            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_READY -> {
                            ivLessonThumbnail.visibility = GONE
                            Timber.d("CHECK THis STATE_READY Lesson Activity currentPosition ${lesson.title} ${exoPlayer.currentPosition}")
                            binding.pbLoading1.visibility = GONE

                        }
                        Player.STATE_ENDED -> {
                            Timber.d("CHECK THis onPlaybackStateChanged Lesson Activity currentPosition ${exoPlayer.currentPosition} and ${exoPlayer.duration}")
                            viewModel.lessonWatched(args.lessonId, exoPlayer.duration)
                        }
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    actionHandler.postDelayed(exoPlayerTimerUpdater, 1000)
                }

            })

            vvLesson.findViewById<ImageButton>(R.id.exo_fullscreen).setOnClickListener {
                findNavController().navigate(LessonFragmentDirections.toVideoPlayerActivity(lesson, isUserPro))

            }

            buttonAddToMyList.setOnClickListener {
                viewModel.addToMyList(args.lessonId)
                Analytics.logEvent(
                    Analytics.Event.AddToWatchListEvent(
                        Analytics.Event.Param.LessonId(lesson.title),
                        Analytics.Event.Param.LessonOwner(lesson.teacher.name),
                        Analytics.Event.Param.LessonCategory(lesson.type),
                        Analytics.Event.Param.LessonCollection("NA"),
                        Analytics.Event.Param.LessonStatus(lesson.subscriptionType)
                    )
                )
            }

            incUpNext.ivPlayPause.setOnClickListener {
                findNavController().navigate(LessonFragmentDirections.toSelf(lesson.nextLesson.id))
            }

            ivExpandMore.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.CheckLessonDetailsEvent(
                        Analytics.Event.Param.LessonId(lesson.title),
                        Analytics.Event.Param.LessonOwner(lesson.teacher.name),
                        Analytics.Event.Param.LessonCategory(lesson.type),
                        Analytics.Event.Param.LessonCollection("NA"),
                        Analytics.Event.Param.LessonStatus(lesson.subscriptionType)
                    )
                )
                showDescription(lesson.description)
            }
            ObjectAnimator.ofPropertyValuesHolder(
                ivExpandMore,
                PropertyValuesHolder.ofFloat("translationY", 20f),
            ).apply {
                interpolator = AccelerateInterpolator()
                duration = 500
                repeatMode = ValueAnimator.REVERSE
                repeatCount = ValueAnimator.INFINITE
                start()
            }

            ivThreeDots.setOnClickListener {
                showBottomSheetDialog(lesson)
            }

            incNextLesson.apply {
                tvTitle.text = lesson.nextLesson.title
                tvType.text = lesson.nextLesson.type
                ivLessonThumbnail.loadUrl(lesson.nextLesson.thumbnailUrl)
                tvDuration.text = lesson.nextLesson.duration
                tvFreeLabel.text = lesson.nextLesson.subscriptionType
                tvDifficulty.text = lesson.nextLesson.difficulty
            }

            incNextLesson.root.setOnClickListener {
                findNavController().navigate(LessonFragmentDirections.toSelf(lesson.nextLesson.id))
            }

            recommendedSongsAdapter.songs = lesson.relatedSongs
            relatedSongsAdapter.songs = lesson.relatedSongs
            rvPracticeSongs.layoutManager = LinearLayoutManager(context)
            rvPracticeSongs.adapter = relatedSongsAdapter
        }
    }

    private fun showBottomSheetDialog(lesson: Lesson) {
        with(BottomSheetDialog(binding.root.context, R.style.AppBottomSheetDialogTheme)) {
            setContentView(R.layout.dialog_report_captions_quality)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            findViewById<LinearLayoutCompat>(R.id.llcReport)?.setOnClickListener {
                openDialogForVideo(lesson)
                dismissWithAnimation = true
                dismiss()
            }
            findViewById<LinearLayoutCompat>(R.id.llcCaption)?.setOnClickListener {
                openBottomSheetForCaption()
                dismissWithAnimation = true
                dismiss()
            }
            findViewById<LinearLayoutCompat>(R.id.llcQuality)?.setOnClickListener {
                openBottomSheetForQuality()
                dismissWithAnimation = true
                dismiss()
            }
            show()
        }
    }

    private lateinit var otherMessage: EditText

    private fun openDialogForVideo(lesson: Lesson) {
        with(Dialog(binding.root.context, R.style.wideDialog)) {
            setContentView(R.layout.dialog_report_video)
            setCanceledOnTouchOutside(false)
            window?.setBackgroundDrawableResource(android.R.color.transparent)
            val selectedStrings = ArrayList<String>()
            findViewById<TextView>(R.id.btnReport).setOnClickListener {
                if (findViewById<CheckBox>(R.id.cbAbusive).isChecked)
                    selectedStrings.add("Abusive")
                if (findViewById<CheckBox>(R.id.cbInappropriate).isChecked)
                    selectedStrings.add("Inappropriate")
                if (findViewById<CheckBox>(R.id.cbSpam).isChecked)
                    selectedStrings.add("Spam")
                if (findViewById<CheckBox>(R.id.cbFake).isChecked)
                    selectedStrings.add("Fake Account")
                if (findViewById<CheckBox>(R.id.cbHarrasment).isChecked)
                    selectedStrings.add("Harrasment")
                if (findViewById<CheckBox>(R.id.cbDisrespectful).isChecked)
                    selectedStrings.add("Disrespectful")
                println(selectedStrings)
                if (selectedStrings.size == 0)
                    selectedStrings.add("")
                viewModel.reportLesson(findViewById<TextView>(R.id.tvReasonReport).text.toString(), selectedStrings, lesson.id)       //attemptId
                dismiss()
            }
            show()
        }
    }

    private fun showReportLessonItems(list: List<String>) {
        otherMessage.setText(list.toString().replace("[", "").replace("]", ""))
    }

    private fun openBottomSheetForCaption() {
        with(BottomSheetDialog(binding.root.context, R.style.AppBottomSheetDialogTheme)) {
            setContentView(R.layout.bottomsheet_captions)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    private fun openBottomSheetForQuality() {
        with(BottomSheetDialog(binding.root.context, R.style.AppBottomSheetDialogTheme)) {
            setContentView(R.layout.bottomsheet_quality)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            show()
        }
    }

    private val exoPlayerTimerUpdater: Runnable = Runnable { checkProLesson() }

    private fun checkProLesson() {
        if (!isUserPro && isProLesson && exoPlayer.currentPosition > 14000) {
            viewModel.progressCheck()
            showProSubscriptionDialog()
            isProLessonProgressCheck = false
        }
        actionHandler.postDelayed(exoPlayerTimerUpdater, 1000)
    }

    private fun shareLessonLink() {
        val dynamicLink = Firebase.dynamicLinks.shortLinkAsync { // or Firebase.dynamicLinks.shortLinkAsync
            link = Uri.parse("https://www.singshala.com/app/lessons/${lesson?.id}")
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
                campaign = "user-share"
            }
            socialMetaTagParameters {
                title = "Singhshala Lesson "
                description = "${lesson?.description}"
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_lesson_msg) + " $shortLink")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Choose application.."))
        }
    }

    private fun handleSubscribed(subscribe: Boolean) {
        binding.apply {
            pbFollow.visibility = GONE
            cbFollow.isChecked = true
            cbFollow.text = getString(R.string.following)
        }
    }

    private fun handleUnsubscribed(unsusbcribe: Boolean) {
        binding.apply {
            pbFollow.visibility = GONE
            cbFollow.isChecked = false
            cbFollow.text = getString(R.string.follow)
        }
    }

    private fun handleAlreadySubscribed(isSubscribe: Boolean) {
        binding.cbFollow.apply {
            isChecked = isSubscribe
            text = if (isSubscribe) getString(R.string.following) else getString(R.string.follow)
            isEnabled = true
        }
    }

    override fun onPause() {
        super.onPause()
        if (this::exoPlayer.isInitialized) {
            Timber.d("CHECK THis Lesson Tag onPause Lesson Activity currentPosition ")
            exoPlayer.pause()
        }
        actionHandler.removeCallbacks(exoPlayerTimerUpdater)
    }

    override fun onResume() {
        super.onResume()
        Timber.d("Lesson Tag onResume Lesson Activity")
        if (!isProLessonProgressCheck) {
            binding.apply {
                if (this@LessonFragment::exoPlayer.isInitialized) {
                    exoPlayer.pause()
                }
            }
        }
    }

    override fun onDestroyView() {
        Timber.d("Lesson Tag onDestroyView Lesson")
        super.onDestroyView()
        if (this::exoPlayer.isInitialized) {
            val viewTime = TimeUnit.MILLISECONDS.toSeconds(exoPlayer.currentPosition)
            val totalDuration = TimeUnit.MILLISECONDS.toSeconds(exoPlayer.duration)
            val percent = (viewTime.toDouble() / totalDuration) * 100
            if (lesson != null) {
                Analytics.logEvent(
                    Analytics.Event.LessonCompleteEvent(
                        Analytics.Event.Param.LessonId(lesson!!.title),
                        Analytics.Event.Param.LessonOwner(lesson!!.teacher.name),
                        Analytics.Event.Param.LessonCategory(lesson!!.type),
                        Analytics.Event.Param.LessonCollection("NA"),
                        Analytics.Event.Param.LessonStatus(lesson!!.subscriptionType),
                        Analytics.Event.Param.ViewTime(viewTime.toInt()),
                        Analytics.Event.Param.ViewPercent(percent.toInt()),
                    )
                )
            }
            exoPlayer.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Lesson Tag onDestroy Lesson Activity")
        if (this::exoPlayer.isInitialized) {
            exoPlayer.stop()
        }
    }
}


