package com.sensibol.lucidmusic.singstr.gui.app.payment.claim

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.google.android.material.imageview.ShapeableImageView
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.ProSubscription
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentClaimFreeSubscriptionBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.loadCenterCropImageFromUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClaimFreeSubscriptionFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_claim_free_subscription
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentClaimFreeSubscriptionBinding::inflate
    override val binding: FragmentClaimFreeSubscriptionBinding get() = super.binding as FragmentClaimFreeSubscriptionBinding

    private val viewModel: ClaimFreeSubscriptionViewModel by viewModels()

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    val arg: ClaimFreeSubscriptionFragmentArgs by navArgs()

    override fun onInitView() {
        singstrViewModel.apply {
            failure(failure, ::handleFailure)
            observe(userSubscription, ::showUserSubscription)
        }

        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(claimMsg, ::handleClaimMsg)
        }

        binding.apply {
            progressBar.visibility = View.GONE

            tvClaimNow.setOnClickListener {
                tvClaimNow.isEnabled = false
                singstrViewModel.getUserSubscription()
            }

            ivClose.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun showUserSubscription(proSubscription: ProSubscription) {
        if (proSubscription.subscribed) {
            showProAlreadyMemberDialog()
            binding.tvClaimNow.isEnabled = true
        } else {
            viewModel.claimFreeSubscription(arg.code)
        }
    }

    private fun handleClaimMsg(msg: String) {
        binding.tvClaimNow.isEnabled = true
        if (msg == "user already redeem a code")
            showAlreadyClaimDialog()
        else
            showClaimSuccessDialog()
    }

    private fun showAlreadyClaimDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_already_claim_free_subscription, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)

        val alertDialog = mBuilder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<TextView>(R.id.btnCancel).setOnClickListener {
            alertDialog.dismiss()
            findNavController().popBackStack()
        }

        dialogView.findViewById<TextView>(R.id.btnBuy).setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigate(ClaimFreeSubscriptionFragmentDirections.toProSubscriptionPlanFragment())
        }
    }

    private fun showClaimSuccessDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_success_claim_free_subscription, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        val tvUsername = dialogView.findViewById<TextView>(R.id.tv_handle)
        val ivProfilePicture = dialogView.findViewById<ShapeableImageView>(R.id.iv_profilePicture)
        singstrViewModel.user.value?.let {
            tvUsername.text = it.name
            ivProfilePicture.loadCenterCropImageFromUrl(it.dpUrl)
        }

        val alertDialog = mBuilder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialogView.findViewById<TextView>(R.id.btnDone).setOnClickListener {
            alertDialog.dismiss()
            findNavController().popBackStack()
        }
    }

    private fun showProAlreadyMemberDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialogue_pro_success, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        val tvDone = dialogView.findViewById<TextView>(R.id.tvDone)
        val tvWelcome = dialogView.findViewById<TextView>(R.id.tv_welcome)
        val tvUsername = dialogView.findViewById<TextView>(R.id.tv_handle)
        val ivProfilePicture = dialogView.findViewById<ShapeableImageView>(R.id.iv_profilePicture)
        tvWelcome.text = getString(R.string.you_are_already_a_pro_member)
        singstrViewModel.user.value?.let {
            tvUsername.text = it.name
            ivProfilePicture.loadCenterCropImageFromUrl(it.dpUrl)
        }
        val alertDialog = mBuilder.show()
        alertDialog.setCancelable(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tvDone.setOnClickListener {
            alertDialog.dismiss()
            findNavController().popBackStack()
        }
    }

}