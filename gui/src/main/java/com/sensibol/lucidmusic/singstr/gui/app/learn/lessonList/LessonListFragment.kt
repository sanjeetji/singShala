package com.sensibol.lucidmusic.singstr.gui.app.learn.lessonList

import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.LessonListType
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentNewLessonListBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class LessonListFragment : BaseFragment() {

    override val layoutResId = R.layout.fragment_new_lesson_list

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentNewLessonListBinding::inflate

    override val binding: FragmentNewLessonListBinding get() = super.binding as FragmentNewLessonListBinding

    private val viewModel: LessonListViewModel by viewModels()

    private lateinit var mLessonList: List<LessonMini>

    private val args: LessonListFragmentArgs by navArgs()

    @Inject
    internal lateinit var lessonListAdapter: LessonListAdapter

    override fun onInitView() {

        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(lessons, ::showLessonList)
            observe(saveAllSuccess, ::updateAddToMyListButtonState)


            when (args.listType) {
                LessonListType.NEW_LESSON.toString() ->
                    loadNewLessons()
                LessonListType.TUNE_LESSON.toString() ->
                    loadTuneLessons()
                LessonListType.TIMING_LESSON.toString() ->
                    loadTimingLessons()
            }
        }

        lessonListAdapter.onLessonClickListener = {
            Timber.e("============ Lesson Id is ::: "+it.id)
            findNavController().navigate(LessonListFragmentDirections.toLearnDetailFragment(it.id))
        }

        binding.apply {

            shareButton.isEnabled = false
            buttonAddToMyList.isEnabled = false

            args.apply {
                when (args.listType) {
                    LessonListType.NEW_LESSON.toString() -> {
                        tvTopTitle.text = "New Lessons"
                        tvTitle.text = "New Lessons"
                    }
                    LessonListType.TUNE_LESSON.toString() -> {
                        tvTopTitle.text = "Tuning Lessons"
                        tvTitle.text = "Tuning Lessons"
                    }
                    LessonListType.TIMING_LESSON.toString() -> {
                        tvTopTitle.text = "Timing Lessons"
                        tvTitle.text = "Timing Lessons"
                    }
                }
            }
            ivBackBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            buttonAddToMyList.setOnClickListener {
                viewModel.saveAllToMyList(mLessonList.map { it.id })
            }

            shareButton.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.ShareLessonGroupEvent(
                        Analytics.Event.Param.LessonGroupId(args.listType),
                        Analytics.Event.Param.LessonGroupName(args.listType),
                    )
                )
                shareLessonGroupLink()
            }

            rvFragSongList.apply {
                adapter = lessonListAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }

            cbSearch.setOnCheckedChangeListener { _, checked ->

                if (checked) {
                    etSearch.visibility = VISIBLE
                    tvTopTitle.visibility = GONE
                    etSearch.requestFocus()


                } else {
                    etSearch.visibility = GONE
                    tvTopTitle.visibility = VISIBLE
                    etSearch.setText("")
                }
            }

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0.isNullOrEmpty()) {
                        lessonListAdapter.lessons = mLessonList
                    } else {
                        lessonListAdapter.filter!!.filter(p0.toString())
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
        }
    }

    private fun showLessonList(list: List<LessonMini>) {
        mLessonList = list
        lessonListAdapter.lessons = list
        binding.tvVideoCount.text = "${list.size} Videos"
        var totalTime = 0
        list.forEach {
            if (it.duration.isNullOrEmpty()) {
                val timeArray = it.duration.split(":")
                Timber.d("timeArray ${it.duration}")
                if (timeArray.isNotEmpty()) {
//                    val min = timeArray[0]
//                    val sec = timeArray[1]
//                    Timber.d("min $min sec $sec")
//                    totalTime += (timeArray[0].toInt() * 60) + timeArray[1].toInt()
                }
            }
        }
        binding.tvTime.text = "${totalTime / 60}min"

        binding.apply {
            shareButton.isEnabled = true
            buttonAddToMyList.isEnabled = true
        }
    }

    private fun shareLongLink(){
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse("https://www.singshala.com/app/concept/type/${args.listType}/lessons")
            domainUriPrefix = "https://singshala.page.link"
            // Open links with this app on Android
            androidParameters { }
            // Open links with com.example.ios on iOS
            iosParameters("com.example.ios") { }
        }

        Timber.d("long link: ${dynamicLink.uri}")
    }

    private fun shareLessonGroupLink() {
        shareLongLink()
        Timber.d("List Type: ${args.listType}")
        Firebase.dynamicLinks
            .shortLinkAsync { // or Firebase.dynamicLinks.shortLinkAsync
            link = Uri.parse("https://www.singshala.com/app/conceptcategory/type/${args.listType}/lessons")
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
//            socialMetaTagParameters {
//                title = "Singhshala Lesson "
//                description = "${lesson?.description}"
//            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_lesson_collection_msg) + "$shortLink")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, "Choose application.."))
        }
    }

    private fun updateAddToMyListButtonState(successMsg: String) {
        binding.apply {
            when (successMsg) {
                "Record inserted" -> {
                    buttonAddToMyList.text = getString(R.string.added_to_list)
                    buttonAddToMyList.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0)
                    buttonAddToMyList.isEnabled = false
                }
                else -> {
                    buttonAddToMyList.text = getString(R.string.add_to_my_list)
                    buttonAddToMyList.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    buttonAddToMyList.isEnabled = true
                }
            }
        }
    }
}