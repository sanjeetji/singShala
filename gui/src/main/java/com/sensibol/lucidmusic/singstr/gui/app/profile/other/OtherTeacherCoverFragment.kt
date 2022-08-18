package com.sensibol.lucidmusic.singstr.gui.app.profile.other

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.profile.self.CoverView
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentOtherTeacherCoverBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.prettyViewsCount
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OtherTeacherCoverFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_other_teacher_cover
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentOtherTeacherCoverBinding::inflate
    override val binding: FragmentOtherTeacherCoverBinding get() = super.binding as FragmentOtherTeacherCoverBinding

    private val viewModel: OtherUserProfileViewModel by viewModels()

    private lateinit var mUserId: String

    @Inject
    internal lateinit var otherUserCoversAdapter: OtherUserCoversAdapter

    companion object {
        private const val USER_ID = "user_id"

        fun newInstance(userId: String) = OtherTeacherCoverFragment().apply {
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
            failure(failure, ::handleFailure)
            observe(coverViews, ::showOtherUserCovers)
            viewModel.loadOtherUserSubmits(mUserId)
        }

        binding.apply {
            otherUserCoversAdapter.onCoverClickListener = { _, _, position ->
                (requireParentFragment() as OtherUserProfileFragment).moveToUserFeedScreen(position)
            }

            rvProfile.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = otherUserCoversAdapter
            }
        }
    }

    private fun showOtherUserCovers(list: List<CoverView>) {
        otherUserCoversAdapter.coverViews = list
//        (requireParentFragment() as OtherUserProfileFragment).showUserStatus(list.size)

    }
}