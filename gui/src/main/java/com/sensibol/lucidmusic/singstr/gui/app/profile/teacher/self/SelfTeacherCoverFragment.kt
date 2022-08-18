package com.sensibol.lucidmusic.singstr.gui.app.profile.teacher.self

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.NodeDraft
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.*
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.CoversAdapter
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.ProfileFragment
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.ProfileViewModel
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentSelfTeacherCoversBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SelfTeacherCoverFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_self_teacher_covers
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentSelfTeacherCoversBinding::inflate
    override val binding: FragmentSelfTeacherCoversBinding get() = super.binding as FragmentSelfTeacherCoversBinding

    private val viewModel: ProfileViewModel by viewModels()

    private var xpEarned: Int = 0
    private lateinit var mUserId: String

    @Inject
    internal lateinit var coversAdapter: CoversAdapter

    companion object {
        private const val USER_ID = "user_id"

        fun newInstance(userId: String) = SelfTeacherCoverFragment().apply {
            arguments = Bundle(1).apply {
                putString(USER_ID, userId)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            mUserId = it.getString(USER_ID).toString()
            onInitView()
        }
    }

    override fun onInitView() {

        viewModel.apply {
            observe(coverViews, ::showCoverViews)
            observe(nodeDraft, ::handleNodeDraft)
            viewModel.getNodeDraftList(mUserId)
//            getCovers()
        }

        coversAdapter.onCreateCoverClickListener = {
            (parentFragment as ProfileFragment).moveToSingScreen()
        }

        coversAdapter.onSavedDraftsClickListener = {
//            Analytics.logEvent(
//                Analytics.Event.CheckDraftsEvent(
//                    Analytics.Event.Param.UserId(userId)
//                )
//            )
//            Navigation.findNavController(binding.root).navigate(ProfileFragmentDirections.actionProfileFragToCoversDraftFragment())
            (parentFragment as ProfileFragment).moveToDraftListScreen()
        }

        coversAdapter.onCoverDeleteListener = { it ->
            (parentFragment as ProfileFragment).showDeleteConfirmationDialog(it.attemptId, it.title)
        }

        coversAdapter.onCoverClickListener = { coverView, _, position ->
            (parentFragment as ProfileFragment).moveToUserFeedScreen(coverView, position)
        }

        binding.apply {

            rvTeacherCovers.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = coversAdapter
            }
        }
    }

    private fun showCoverViews(coverViews: List<CoverView>) {
        coversAdapter.coverViews = coverViews
    }

    private fun handleNodeDraft(nodeDrafts: List<NodeDraft>){

        Timber.d("handleNodeDraft ${nodeDrafts.size}")
        coversAdapter.draftCount = nodeDrafts.size
        var totalXp = 0
        nodeDrafts.forEach {
            totalXp += it.totalScore
        }
        coversAdapter.totalXp = totalXp
        viewModel.getCovers()
    }

}