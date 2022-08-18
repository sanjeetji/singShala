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
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentFollowersBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
internal class FollowersFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_followers
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentFollowersBinding::inflate
    override val binding get():FragmentFollowersBinding = super.binding as FragmentFollowersBinding

    private val viewModel: FollowerUserViewModel by viewModels()

    private lateinit var userId: String

    companion object {
        private const val USER_ID = "user_id"

        fun newInstance(userID: String) = FollowersFragment().apply {
            arguments = Bundle(1).apply {
                putString(USER_ID, userID)
            }
        }
    }

    @Inject
    internal lateinit var followersAdapter: FollowerListAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            userId = it.getString(USER_ID).toString()
            onInitView()
        }
    }

    override fun onInitView() {
        
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(followerUser, ::showUserDetails)
            loadFollowerUserData(userId)
        }

        followersAdapter.onImageClickListener = {
            (parentFragment as FollowingFollowersPagerFragment).openUserProfile(it._id)
        }

        followersAdapter.onFollowBtnClickListener = { check: Boolean, userId: String ->
            if (check){
                viewModel.subscribeUser(userId)
            }else{
                viewModel.unSubscribeUser(userId)
            }
        }

        binding.apply {
            follwersItems.apply {
                adapter = followersAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
        }
    }

    private fun showUserDetails(followerList: List<FollowersUser>) {
        followersAdapter.followerUserList = followerList
        (parentFragment as FollowingFollowersPagerFragment).setFollowerCount(followerList.size)
    }


}