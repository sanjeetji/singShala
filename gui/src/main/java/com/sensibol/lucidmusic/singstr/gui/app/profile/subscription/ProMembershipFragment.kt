package com.sensibol.lucidmusic.singstr.gui.app.profile.subscription

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.imageview.ShapeableImageView
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.User
import com.sensibol.lucidmusic.singstr.domain.model.UserStats
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.checkUserHandle
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentProMembershipBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.loadCenterCropImageFromUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class ProMembershipFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_pro_membership
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentProMembershipBinding::inflate
    override val binding: FragmentProMembershipBinding get() = super.binding as FragmentProMembershipBinding

    private val viewModel: ProSubscriptionViewModel by viewModels()

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private lateinit var ivProfilePicture: ShapeableImageView
    private lateinit var tvUsername: TextView

    override fun onInitView() {
        binding.tvBecomePro.setOnClickListener {
            viewModel.updateUserSubscription()
        }
        binding.ivClose.setOnClickListener {
            findNavController().popBackStack()
        }
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(updateProSubscription, ::showProSubscription)
            observe(user, ::showUserProfile)
        }

        singstrViewModel.apply {
            failure(failure, ::handleFailure)
            observe(updateProSubscription, ::showProSubscription)
            observe(userStats, ::showUserStats)
        }

        singstrViewModel.loadUserStats()
    }

    private fun showUserStats(userStats: UserStats) {
        binding.apply {
            if (userStats.level > 5) {
                clLevelSeekBar.visibility = GONE
                tvFreeDuration.text = "For 1 month"
            } else {
                clLevelSeekBar.visibility = VISIBLE
                tvLevelScore.text = "Your level: ${userStats.level}"
                progressBar.progress = userStats.level
                tvFreeDuration.text = "For everyone above level 5"
                clLevelSeekBar.setOnClickListener {
                    findNavController().navigate(ProMembershipFragmentDirections.toLeaderBoardFragment())
                }
            }
        }
    }

    private fun showUserProfile(user: User) {
        showProSubscriptionSuccessfulDialog()
        ivProfilePicture.loadCenterCropImageFromUrl(user.dpUrl)
        tvUsername.text = user.handle.checkUserHandle()
    }

    private fun showProSubscription(isUserUpdated: Boolean) {
        findNavController().navigate(ProMembershipFragmentDirections.toProfileFragment())
    }

    private fun showProSubscriptionSuccessfulDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialogue_pro_success, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        val tvDone = dialogView.findViewById<TextView>(R.id.tvDone)
        val ivClose = dialogView.findViewById<ImageView>(R.id.ivClose)
        tvUsername = dialogView.findViewById(R.id.tv_handle)
        ivProfilePicture = dialogView.findViewById(R.id.iv_profilePicture)
        val alertDialog = mBuilder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tvDone.setOnClickListener {
            alertDialog.dismiss()
        }
        ivClose.setOnClickListener {
            alertDialog.dismiss()
        }
    }
}