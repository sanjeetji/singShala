package com.sensibol.lucidmusic.singstr.gui.app.profile.self.followersfollowing

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.*
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentFollowingBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class FollowingFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_following
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentFollowingBinding::inflate
    override val binding get():FragmentFollowingBinding = super.binding as FragmentFollowingBinding

    private val viewModel: FollowingUserViewModel by viewModels()

    private lateinit var userId: String

    companion object {
        private const val USER_ID = "user_id"

        fun newInstance(userID: String) = FollowingFragment().apply {
            arguments = Bundle(1).apply {
                putString(USER_ID, userID)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            userId = it.getString(USER_ID).toString()
            onInitView()
        }
    }

    @Inject
    internal lateinit var followingListAdapter: FollowingListAdapter

    override fun onInitView() {

        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(followingUser, ::showUserDetails)
            loadFollowingUserData(userId)
        }

        followingListAdapter.onFollowingBtnClickListener = { check: Boolean, userId: String ->
            if (check){
                viewModel.subscribeUser(userId)
            }else{
                viewModel.unSubscribeUser(userId)
            }
        }

        followingListAdapter.onImageClickListener = {
            (parentFragment as FollowingFollowersPagerFragment).openUserProfile(it._id)
        }

        binding.apply {
            follwersItems.apply {
                adapter = followingListAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    private fun showUserDetails(list: List<FollowingUser>) {
        followingListAdapter.followingUserList = list
        (parentFragment as FollowingFollowersPagerFragment).setFollowingCount(list.size)
    }


}