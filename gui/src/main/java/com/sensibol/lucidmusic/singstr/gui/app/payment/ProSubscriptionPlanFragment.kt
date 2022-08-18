package com.sensibol.lucidmusic.singstr.gui.app.payment

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.android.material.imageview.ShapeableImageView
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.ProSubscription
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentProSubsciptionPlanBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.loadCenterCropImageFromUrl
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class ProSubscriptionPlanFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_pro_subsciption_plan
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentProSubsciptionPlanBinding::inflate
    override val binding: FragmentProSubsciptionPlanBinding get() = super.binding as FragmentProSubsciptionPlanBinding

    @Inject
    lateinit var proSubscriptionPlanAdapter: ProSubscriptionPlanAdapter

    private val viewModel: ProSubscriptionPlanViewModel by viewModels()

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private lateinit var iapConnector: IapConnector
    private lateinit var subscriptionPlanId: String
    private var isAlreadySubscribed: Boolean = false

    // TODO - fetch this list from either play-store or backend
    val subsList = listOf("singshala.subscription.months.1", "singshala.subscription.months.3", "singshala.subscription.years.1")

    override fun onInitView() {
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(subscriptionPlan, { proSubscriptionPlanAdapter.plans = it })
            observe(purchaseList, ::getInternalPurchaseToken)
            observe(verifyPurchase, ::showVerifyInAppPurchase)

            fetchSubscriptionPlans()
        }

        singstrViewModel.apply {
            failure(failure, ::handleFailure)
            observe(userSubscription, ::showProSubscription)
            getUserSubscription()
            loadUserProfile()
        }

        iapConnector = IapConnector(
            context = requireActivity(),
            subscriptionKeys = subsList,
            enableLogging = true
        )

        binding.apply {
            rvSubscriptionPlanList.apply {
                layoutManager = LinearLayoutManager(requireContext())
                isNestedScrollingEnabled = false
                adapter = proSubscriptionPlanAdapter
            }

            ivClose.setOnClickListener {
                findNavController().popBackStack()
            }

            tvRestorePurchase.setOnClickListener {
                if (isAlreadySubscribed) {
                    showProAlreadyMemberDialog()
                } else {
                    Toast.makeText(context, "Not subscribed previously", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // TODO - make listener removable
        iapConnector.addSubscriptionListener(object : SubscriptionServiceListener {
            override fun onSubscriptionRestored(purchaseInfo: DataWrappers.PurchaseInfo) {
                // will be triggered upon fetching owned subscription upon initialization
                Timber.d("CHECK THIS onSubscriptionRestored $purchaseInfo")
                showProAlreadyMemberDialog()
            }

            override fun onSubscriptionPurchased(purchaseInfo: DataWrappers.PurchaseInfo) {
                // will be triggered whenever subscription succeeded
                Timber.d("CHECK THIS onSubscriptionPurchased ${purchaseInfo.sku}")
                when (purchaseInfo.sku) {
                    "singshala.subscription.months.1" -> {
                        Timber.d("CHECK THIS onSubscriptionPurchased months.1")
                    }
                    "singshala.subscription.months.3" -> {
                        Timber.d("CHECK THIS onSubscriptionPurchased months.3")
                    }
                    "singshala.subscription.years.1" -> {
                        Timber.d("CHECK THIS onSubscriptionPurchased years.1")
                    }
                }
                Analytics.logEvent(
                    Analytics.Event.ProPurchaseEvent(
                        Analytics.Event.Param.PlanDetail(purchaseInfo.packageName),
                        Analytics.Event.Param.PlanValue(purchaseInfo.skuInfo.price),
                    )
                )
                viewModel.verifyInAppPurchase(purchaseInfo.originalJson, purchaseInfo.signature)
            }

            override fun onPricesUpdated(iapKeyPrices: Map<String, String>) {
                // list of available products will be received here, so you can update UI with prices if needed
                Timber.d("CHECK THIS onPricesUpdated ${iapKeyPrices.keys} and ${iapKeyPrices.values}")
            }
        })
    }

    private fun showProSubscription(proSubscription: ProSubscription) {
        Timber.d("CHECK THIS showProSubscription")
        isAlreadySubscribed = proSubscription.subscribed
        if (!isAlreadySubscribed) {
            binding.apply {
                proSubscriptionPlanAdapter.onPlanClickListener = { subscriptionPlan ->
                    Analytics.logEvent(
                        Analytics.Event.SelectProPlanEvent(
                            Analytics.Event.Param.PlanDetail(subscriptionPlan.name),
                        )
                    )
                    viewModel.initialisePayment(subscriptionPlan.id)
                    subscriptionPlanId = subscriptionPlan.id
                    Timber.d("CHECK THIS showProSubscription ClickEvent ${subscriptionPlan.id}")
                }
            }
        }
    }

    private fun showProSubscriptionSuccessfulDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialogue_pro_success, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        val tvDone = dialogView.findViewById<TextView>(R.id.tvDone)
        val ivClose = dialogView.findViewById<ImageView>(R.id.ivClose)
        val tvUsername = dialogView.findViewById<TextView>(R.id.tv_handle)
        val ivProfilePicture = dialogView.findViewById<ShapeableImageView>(R.id.iv_profilePicture)
        singstrViewModel.user.value?.let {
            tvUsername.text = it.name
            ivProfilePicture.loadCenterCropImageFromUrl(it.dpUrl)
        }
        val alertDialog = mBuilder.show()
        alertDialog.setCancelable(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tvDone.setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigate(ProSubscriptionPlanFragmentDirections.toProfileFragment())
        }
        ivClose.setOnClickListener {
            alertDialog.dismiss()
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
        /*singstrViewModel.user.value?.let {
            tvUsername.text = it.name
            ivProfilePicture.loadCenterCropImageFromUrl(it.dpUrl)
        }*/
        val alertDialog = mBuilder.show()
        alertDialog.setCancelable(false)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        tvDone.setOnClickListener {
            alertDialog.dismiss()
            findNavController().navigate(ProSubscriptionPlanFragmentDirections.toProfileFragment())
        }
    }

    private fun getInternalPurchaseToken(token: String) {
        iapConnector.subscribe(requireActivity(), subscriptionPlanId, token)
    }

    private fun showVerifyInAppPurchase(isVerified: Boolean) {
        Timber.d("CHECK THIS showVerifyInAppPurchase $isVerified")
        if (isVerified) {
            showProSubscriptionSuccessfulDialog()
        }
    }
}