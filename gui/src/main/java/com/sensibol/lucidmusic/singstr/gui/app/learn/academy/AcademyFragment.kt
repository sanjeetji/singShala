package com.sensibol.lucidmusic.singstr.gui.app.learn.academy

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.net.Uri
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.learn.LearnFragment
import com.sensibol.lucidmusic.singstr.gui.app.learn.LearnFragmentDirections
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentAcademyBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
internal class AcademyFragment : BaseFragment() {

    override val layoutResId = R.layout.fragment_academy
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentAcademyBinding::inflate
    override val binding: FragmentAcademyBinding get() = super.binding as FragmentAcademyBinding

    private val viewModel: AcademyViewModel by viewModels()

    @Inject
    lateinit var lessonGroupsAdapter: LessonGroupsAdapter

    @Inject
    internal lateinit var lessonMinisAdapter: LessonMinisAdapter

    @Inject
    internal lateinit var lessonMiniTimingAdapter: LessonMiniTimingAdapter

    @Inject
    internal lateinit var lessonMiniTuneAdapter: LessonMiniTuneAdapter

    @Inject
    internal lateinit var tuneTimingAdapter: TuneTimingAdapter

    @Inject
    internal lateinit var lessonTabAdapter: LessonTabAdapter

    @Inject
    internal lateinit var academyAnswerOptionsAdapter: AcademyAnswerOptionsAdapter

    private lateinit var optionSelected: String
    var tagList = mutableListOf<String>()

    private lateinit var exoPlayer: ExoPlayer
    private lateinit var mcqCurrentQuestion: String

    override fun onInitView() {
        Analytics.logEvent(
            Analytics.Event.AcademyPageViewEvent(
                Analytics.Event.Param.ScrollPercent("NA"),
            )
        )
        lessonGroupsAdapter.onAllViewLessonClickListener = { conceptId->
            findNavController().navigate(LearnFragmentDirections.toConceptLessonListFragment(conceptId))
        }

        lessonGroupsAdapter.onLessonClickListener = { showLessonDetailPage(it.id) }

        lessonMinisAdapter.onLessonClickListener = {
            findNavController().navigate(LearnFragmentDirections.toLearnDetailFragment(it.id))
        }

        lessonMiniTuneAdapter.onLessonClickListener = {
            findNavController().navigate(LearnFragmentDirections.toLearnDetailFragment(it.id))
        }
        lessonMiniTimingAdapter.onLessonClickListener = {
            findNavController().navigate(LearnFragmentDirections.toLearnDetailFragment(it.id))
        }
        tuneTimingAdapter.onLessonClickListener = {
            findNavController().navigate(LearnFragmentDirections.toLearnDetailFragment(it.id))
        }

        binding.apply {
            rvLessonGroups.apply {
                adapter = lessonGroupsAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
            rvFragNewLessons.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = lessonMinisAdapter
            }
            rvLessonCategories.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
                adapter = lessonTabAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
            rvLessonsWrtTag.apply {
                adapter = tuneTimingAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
            lessonTabAdapter.onLessonCategoryClickListener = { isChecked: Boolean, tagName: String ->
                if (tagName == "ALL LESSONS") {
                    resetFilter()
                    tvNoOfFilters.text = "${tagList.size} filter selected"
                } else {
                    when (isChecked) {
                        true -> {
                            tagList.add(tagName)
                        }
                        false -> {
                            tagList.remove(tagName)
                        }

                    }
                    if (tagList.isNotEmpty()) {
                        fetchDataForSelectedTab()
                    } else {
                        resetFilter()
                    }
                }
            }

            rvLessonHighScore.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
            }
            rvLessonLowScore.apply {
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        val startMargin = if (0 == parent.getChildAdapterPosition(view)) resources.getDimension(R.dimen.screen_horz_margin) else 0
                        outRect.set(startMargin.toInt(), 0, 0, 0)
                    }
                })
            }

            incMcqQuestion.rvAnswerOptions.apply {
                adapter = academyAnswerOptionsAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }

            academyAnswerOptionsAdapter.onOptionClickListener = { optionId, answer ->

                optionSelected = answer
                binding.incMcqQuestion.groupProcess.visibility = VISIBLE
                viewModel.submitAnswer(viewModel.questionId, optionId)
            }

            tvReset.setOnClickListener {
                resetFilter()
            }

            tvResetFilter.setOnClickListener {
                resetFilter()
            }

            tvViewNewLesson.setOnClickListener {
                findNavController().navigate(AcademyFragmentDirections.toLessonListFragment(LessonListType.NEW_LESSON.toString()))
            }
        }

        exoPlayer = SimpleExoPlayer.Builder(requireContext())
            .setTrackSelector(
                DefaultTrackSelector(requireContext()).also {
                    it.parameters = it.buildUponParameters()
                        .setForceLowestBitrate(true)
                        .build()
                }
            ).build()

//        if (Player.STATE_READY == exoPlayer.playbackState) {
//            Timber.d("setup: player ready")
//            binding.pbLoading1.visibility= GONE
//
//        } else {
//            binding.pbLoading1.visibility= VISIBLE
//
//
//        }

        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(academyContent, ::showLearnContent)
            observe(newLessons, { lessonMinisAdapter.lessons = it })
            observe(tagLessons, ::showTagLessons)
            observe(academyQuestion, ::showAcademyQuestion)
            observe(submitAnswer, ::showSubmitAnswer)
            observe(triviaLesson, ::showTriviaLesson)
            observe(tags, ::showTags)
            loadTags()
        }
    }

    private fun resetFilter() {
        tagList.clear()
        binding.apply {
            llFilterText.visibility = GONE
            clNoMatchingFilter.visibility = GONE
            rvAllLessons.visibility = VISIBLE
            clOtherLessons.visibility = GONE
        }

        lessonTabAdapter.lessonCat[0].isCheck = true
        for (i in 1 until lessonTabAdapter.lessonCat.size) {
            lessonTabAdapter.lessonCat[i].isCheck = false
        }
        lessonTabAdapter.notifyDataSetChanged()

        fetchDataForAllLesson()
    }

    private fun showTriviaLesson(triviaLesson: LessonMini) {
        binding.apply {
            clMcqQuestion.visibility = GONE
            clCorrectAnswer.visibility = GONE
            clWrongAnswer.visibility = VISIBLE
            incWrongAnswer.ivLessonThumbnail.loadUrl(triviaLesson.thumbnailUrl)
            incWrongAnswer.tvTitle.text = triviaLesson.title
            incWrongAnswer.tvDifficulty.text = triviaLesson.difficulty
            incWrongAnswer.tvSubscriptionType.text = triviaLesson.subscriptionType
            incWrongAnswer.tvTileLessonTime.text = triviaLesson.duration
            incWrongAnswer.tvAnswer.text = optionSelected
            incWrongAnswer.ivLessonThumbnail.setOnClickListener {
                showLessonDetailPage(triviaLesson.id)
            }
            incWrongAnswer.tvNextQuestion.setOnClickListener {
                viewModel.loadAcademyQuestion()
            }
        }
    }

    private fun showAcademyQuestion(mcqQuestion: McqQuestion) {
        binding.apply {
            clWrongAnswer.visibility = GONE
            clCorrectAnswer.visibility = GONE
            clMcqQuestion.visibility = VISIBLE
            incMcqQuestion.tvQuestion.text = mcqQuestion.attributes.text
            mcqCurrentQuestion = mcqQuestion.attributes.text
            viewModel.questionId = mcqQuestion.id
            academyAnswerOptionsAdapter.options = mcqQuestion.options
        }
    }

    private fun showSubmitAnswer(answerSubmitData: AnswerSubmitData) {

        binding.incMcqQuestion.groupProcess.visibility = GONE

        Analytics.logEvent(
            Analytics.Event.MCQAttemptEvent(
                Analytics.Event.Param.McqQuestion(mcqCurrentQuestion),
                Analytics.Event.Param.McqAnswer(answerSubmitData.answer.isAnswerCorrect)
            )
        )
        if (answerSubmitData.answer.isAnswerCorrect) {
            binding.apply {
                clMcqQuestion.visibility = GONE
                clCorrectAnswer.visibility = VISIBLE
                incCorrectAnswer.tvAnswer.text = optionSelected
                incCorrectAnswer.tvXpPoints.text = coloredString(answerSubmitData.answer.xp)
                incCorrectAnswer.tvNextQuestion.setOnClickListener {
                    viewModel.loadAcademyQuestion()
                    clMcqQuestion.visibility = VISIBLE
                    clCorrectAnswer.visibility = GONE
                }
            }
        } else {
            viewModel.getTriviaLesson(answerSubmitData.answer.lessonId)
        }
    }

    private fun coloredString(xp: String): SpannableString {
        val xpEarned = "You got +$xp XP"
        val sp = SpannableString(xpEarned)
        val green = ForegroundColorSpan(Color.GREEN)
        sp.setSpan(green, 8, 11, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        return sp
    }

    private fun showLearnContent(academyContent: AcademyContent) {
        showRecommendedLesson(academyContent.recommendedLesson)
        showTuneTimeLessons(academyContent.lessonTune, academyContent.lessonTime)
        lessonGroupsAdapter.lessonGroups = academyContent.lessonGroups
        lessonMiniTimingAdapter.lessons = academyContent.lessonTime.lessons
        lessonMiniTuneAdapter.lessons = academyContent.lessonTune.lessons
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showTuneTimeLessons(tuneLesson: AcademyContent.LessonTune, timeLesson: AcademyContent.LessonTime) {
        binding.apply {
            if (tuneLesson.score <= timeLesson.score) {
                rvLessonLowScore.apply {
                    pbLowScore.progressDrawable = resources.getDrawable(R.drawable.circular_progress_learn_tune, null)
                    tvLowScore.text = tuneLesson.score.toString()
                    tvLessonLowScore.text = tuneLesson.title
                    pbLowScore.progress = tuneLesson.score
                    adapter = lessonMiniTuneAdapter
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    tvViewLowScore.setOnClickListener {
                        findNavController().navigate(AcademyFragmentDirections.toLessonListFragment(LessonListType.TUNE_LESSON.toString()))
                    }
                }
                rvLessonHighScore.apply {
                    pbHighScore.progressDrawable = resources.getDrawable(R.drawable.circular_progress_learn_timing, null)
                    tvHighScore.text = timeLesson.score.toString()
                    pbHighScore.progress = timeLesson.score
                    tvLessonHighScore.text = timeLesson.title
                    adapter = lessonMiniTimingAdapter
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    tvViewHighScore.setOnClickListener {
                        findNavController().navigate(AcademyFragmentDirections.toLessonListFragment(LessonListType.TIMING_LESSON.toString()))
                    }

                }

            } else {
                rvLessonLowScore.apply {
                    pbLowScore.progressDrawable = resources.getDrawable(R.drawable.circular_progress_learn_timing, null)
                    tvLowScore.text = timeLesson.score.toString()
                    pbLowScore.progress = timeLesson.score
                    tvLessonLowScore.text = timeLesson.title
                    adapter = lessonMiniTimingAdapter
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    tvViewHighScore.setOnClickListener {
                        findNavController().navigate(AcademyFragmentDirections.toLessonListFragment(LessonListType.TIMING_LESSON.toString()))
                    }
                }
                rvLessonHighScore.apply {
                    pbHighScore.progressDrawable = resources.getDrawable(R.drawable.circular_progress_learn_tune, null)
                    tvHighScore.text = tuneLesson.score.toString()
                    pbHighScore.progress = tuneLesson.score
                    tvLessonHighScore.text = tuneLesson.title
                    adapter = lessonMiniTuneAdapter
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    tvViewHighScore.setOnClickListener {
                        findNavController().navigate(AcademyFragmentDirections.toLessonListFragment(LessonListType.TUNE_LESSON.toString()))
                    }
                }
            }
        }
    }

    private fun showTagLessons(tagLessons: List<LessonMini>) {
        tuneTimingAdapter.lessons = tagLessons
        binding.apply {
            pbLoading.visibility = GONE
            if (tagLessons.isEmpty()) {
                llFilterText.visibility = GONE
                clNoMatchingFilter.visibility = VISIBLE
            } else {
                clNoMatchingFilter.visibility = GONE
                llFilterText.visibility = VISIBLE
            }
        }
    }

    private fun showRecommendedLesson(recommendedLesson: LessonMini) {
        binding.apply {
            incRecommendedLesson.apply {
                tvTitle.text = recommendedLesson.title
                tvType.text = recommendedLesson.type
                tvDifficulty.text = recommendedLesson.difficulty
                tvDuration.text = recommendedLesson.duration
                tvType.text = recommendedLesson.type
                if (recommendedLesson.subscriptionType == "Free") {
                    tvFreeLabel.text = recommendedLesson.subscriptionType
                } else {
                    tvFreeLabel.visibility = INVISIBLE
                }
                ivLessonThumbnail.loadUrl(recommendedLesson.thumbnailUrl, R.drawable.ic_baseline_music_video_24)
                root.setOnClickListener {
                    if (exoPlayer.isPlaying) {
                        exoPlayer.pause()
                    }
                    showLessonDetailPage(recommendedLesson.id)
                }
            }
            vvLesson.apply {
                exoPlayer.setMediaItem(
                    MediaItem.Builder()
                        .setUri(Uri.parse(recommendedLesson.videoUrl))
                        .setMimeType(MimeTypes.APPLICATION_M3U8).build()
                )
                exoPlayer.volume = 0f
                exoPlayer.prepare()
                exoPlayer.play()
                player = exoPlayer

                if (exoPlayer.playWhenReady) {
                    Timber.d(" player is playing")
                    binding.pbLoadingPlayerView.visibility= GONE
                }
            }
        }
    }

    private fun showLessonDetailPage(lessonId: String) {
        findNavController().navigate(LearnFragmentDirections.toLearnDetailFragment(lessonId))
    }

    private fun showTags(tags: List<LessonTags>) {
        val mutableListofTags: MutableList<LessonTags> = tags.toMutableList()
        mutableListofTags.add(0, allLessons)

        //implement filtering for tune and timing on clicking tune and timing from home screen...
        var filterType = (parentFragmentManager.fragments[0].parentFragment as LearnFragment).args.filterType
        if (!filterType.isNullOrEmpty()) {
            for (i in 0 until mutableListofTags.size) {
                if (mutableListofTags[i].title == filterType) {
                    mutableListofTags[i].isCheck = true
                    tagList.add(mutableListofTags[i].title)
                    fetchDataForSelectedTab()
                    break
                } else {
                    mutableListofTags[i].isCheck = false
                }
            }
        } else {
            mutableListofTags[0].isCheck = true
            fetchDataForAllLesson()
        }
        lessonTabAdapter.lessonCat = mutableListofTags
    }

    private fun fetchDataForAllLesson() {
        viewModel.apply {
            loadLearnContent()
            loadAcademyQuestion()
            loadNewLessonsContent()
        }
    }

    private fun fetchDataForSelectedTab() {
        binding.apply {
            viewModel.loadTagLessonsUseCase(tagList)
            llFilterText.visibility = VISIBLE
            pbLoading.visibility = VISIBLE
            clOtherLessons.visibility = VISIBLE
            rvAllLessons.visibility = GONE
            tvNoOfFilters.text = "${tagList.size} filter selected"
        }
    }
}