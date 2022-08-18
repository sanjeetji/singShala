package com.sensibol.lucidmusic.singstr.gui.app.learn.lessonList

import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
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
import com.sensibol.lucidmusic.singstr.domain.model.ConceptInfo
import com.sensibol.lucidmusic.singstr.domain.model.LessonMini
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentConceptLessonListBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConceptLessonListFragment : BaseFragment() {

    override val layoutResId = R.layout.fragment_concept_lesson_list

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentConceptLessonListBinding::inflate

    override val binding: FragmentConceptLessonListBinding get() = super.binding as FragmentConceptLessonListBinding

    private val viewModel: ConceptLessonListViewModel by viewModels()

    private val args: ConceptLessonListFragmentArgs by navArgs()

    private lateinit var mConceptInfo: ConceptInfo

    @Inject
    internal lateinit var lessonListAdapter: LessonListAdapter

    private var mLessonList: List<LessonMini> = listOf()

    override fun onInitView() {
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(conceptInfo, ::handleConceptInfo)
            observe(saveAllSuccess, ::updateAddToMyListButtonState)
            loadConceptInfo(args.conceptId)
        }

        lessonListAdapter.onLessonClickListener = {
            findNavController().navigate(ConceptLessonListFragmentDirections.toLearnDetailFragment(it.id))
        }

        binding.apply {
            shareButton.isEnabled = false
            ivBackBtn.setOnClickListener {
                findNavController().popBackStack()
            }
            rvFragSongList.apply {
                adapter = lessonListAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }

            cbSearch.setOnCheckedChangeListener { _, checked ->

                if (checked) {
                    etSearch.visibility = View.VISIBLE
                    tvTopTitle.visibility = View.GONE
                    etSearch.requestFocus()


                } else {
                    etSearch.visibility = View.GONE
                    tvTopTitle.visibility = View.VISIBLE
                    etSearch.setText("")
                }
            }

            etSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0?.isEmpty() == true) {
                        lessonListAdapter.lessons = mLessonList
                    } else {
                        lessonListAdapter.filter!!.filter(p0.toString())
                    }
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })

            buttonAddToMyList.setOnClickListener {
                viewModel.saveAllToMyList(mLessonList.map { it.id })
            }

            shareButton.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.ShareLessonGroupEvent(
                        Analytics.Event.Param.LessonGroupId(mConceptInfo.id),
                        Analytics.Event.Param.LessonGroupName(mConceptInfo.name),
                        )
                )
                shareLessonGroupLink()
            }
        }
    }

    private fun handleConceptInfo(conceptInfo: ConceptInfo) {

        mConceptInfo = conceptInfo
        mLessonList = conceptInfo.lessonList
        lessonListAdapter.lessons = mLessonList

        binding.apply {
            shareButton.isEnabled = true
            tvTopTitle.text = conceptInfo.displayName
            tvTitle.text = conceptInfo.displayName
            tvVideoCount.text = "${mLessonList.size} Videos"

        }
    }

    private fun shareLessonGroupLink() {
        Firebase.dynamicLinks.shortLinkAsync { // or Firebase.dynamicLinks.shortLinkAsync
            link = Uri.parse("https://www.singshala.com/app/concept/${args.conceptId}/lessons")
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
