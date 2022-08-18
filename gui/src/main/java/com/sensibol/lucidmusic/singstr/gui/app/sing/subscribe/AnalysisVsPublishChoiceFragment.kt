package com.sensibol.lucidmusic.singstr.gui.app.sing.subscribe

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.ProSubscription
import com.sensibol.lucidmusic.singstr.domain.model.SimpleAnalysis
import com.sensibol.lucidmusic.singstr.domain.model.UserReviewAccount
import com.sensibol.lucidmusic.singstr.gui.BuildConfig
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analysis.AnalysisVsPublishViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analysis.SimpleCoupletReviewAdapter
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentAnalysisVsPublishChoiceBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AnalysisVsPublishChoiceFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_analysis_vs_publish_choice
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentAnalysisVsPublishChoiceBinding::inflate
    override val binding: FragmentAnalysisVsPublishChoiceBinding get() = super.binding as FragmentAnalysisVsPublishChoiceBinding

    private val args: AnalysisVsPublishChoiceFragmentArgs by navArgs()
    private val singstrViewModel: SingstrViewModel by activityViewModels()
    private val viewModel: AnalysisVsPublishViewModel by activityViewModels()

    @Inject
    lateinit var simpleCoupletReviewAdapter: SimpleCoupletReviewAdapter

    private var mInterstitialAd: AdManagerInterstitialAd? = null

    private var isUserPro: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(requireContext()) { initializationStatus ->
            Timber.d("CHECK this Ad AdMob Sdk Initialize $initializationStatus")
        }
    }

    override fun onInitView() {
        binding.apply {
            viewModel.apply {
                failure(failure, ::handleFailure)
                observe(simpleAnalysis, ::showSimpleAnalysisData)
                observe(userReviewAccount, ::showUserReviewAccount)
                loadSimpleAnalysis(args.attemptId)
            }
            singstrViewModel.apply {
                failure(failure, ::handleFailure)
                observe(userSubscription, ::showUserSubscription)
                getUserSubscription()
            }

            rvMiniDetails.apply {
                isNestedScrollingEnabled = false
                layoutManager = LinearLayoutManager(context)
                adapter = simpleCoupletReviewAdapter
            }

            tvViewFullDetail.setOnClickListener {
                singstrViewModel.user.value?.id?.let { userId ->
                    if(isUserPro)
                        goToDetailsAnalysis(userId)
                    else
                        loadInterstitialAd(userId)
                }
            }
            tvBecomePro.setOnClickListener {
                findNavController().navigate(AnalysisVsPublishChoiceFragmentDirections.toProSubscriptionPlanFragment())
            }
            btnPreviewCover.setOnClickListener {
                findNavController().navigate(
                    AnalysisVsPublishChoiceFragmentDirections.toCoverPreviewFragment(
                        args.song, args.singScore, args.attemptId, args.singScore.mixPath
                    )
                )
            }
            ivClose.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }

    }

    private fun showSimpleAnalysisData(simpleAnalysis: SimpleAnalysis) {
        simpleCoupletReviewAdapter.simpleCoupletReview = simpleAnalysis.simpleCoupletReviews
        binding.apply {
            val firstText =
                SpannableStringBuilder(
                    "You got ${if (simpleAnalysis.linesDonePerfectly > 1) "${simpleAnalysis.linesDonePerfectly} lines" else "${simpleAnalysis.linesDonePerfectly} line"} perfectly and " +
                            "\n${if (simpleAnalysis.linesNeedsImprovement > 1) "${simpleAnalysis.linesNeedsImprovement} lines" else "${simpleAnalysis.linesNeedsImprovement} line"} need improvement"
                )

            if(simpleAnalysis.linesDonePerfectly > 1){
                firstText.setSpan(
                    ForegroundColorSpan(Color.parseColor("#5DB427")),
                    8,
                    16,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }else{
                firstText.setSpan(
                    ForegroundColorSpan(Color.parseColor("#5DB427")),
                    8,
                    15,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            if(simpleAnalysis.linesNeedsImprovement > 1){
                firstText.setSpan(
                    ForegroundColorSpan(Color.parseColor("#CCB012")),
                    30,
                    39,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }else{
                firstText.setSpan(
                    ForegroundColorSpan(Color.parseColor("#CCB012")),
                    30,
                    38,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            tvSimpleMsg.text = firstText
        }


    }

    private fun showUserSubscription(proSubscription: ProSubscription) {
        isUserPro = proSubscription.subscribed
        if (proSubscription.subscribed) {
            binding.apply {
                tvViewFullDetail.visibility = View.VISIBLE
                llcProDialogue.visibility = View.GONE
            }
        } else {
            singstrViewModel.user.value?.id?.let { userId ->
                viewModel.getUserReviewAccount(userId)
            }
        }
    }

    private fun showUserReviewAccount(userReviewAccount: UserReviewAccount) {
        if (userReviewAccount.lineReviews > 0) {
            binding.tvCountLeft.visibility = View.VISIBLE
            binding.tvViewFullDetail.visibility = View.VISIBLE
        } else {
            binding.tvCountLeft.visibility = View.GONE
            binding.llcProDialogue.visibility = View.VISIBLE
            binding.tvViewFullDetail.visibility = View.GONE
        }
        binding.tvCountLeft.text = "${userReviewAccount.lineReviews} Left"
    }

    private fun goToDetailsAnalysis(userId: String){
        findNavController().navigate(
            AnalysisVsPublishChoiceFragmentDirections.toDetailAnalysisFragment(
                args.attemptId,
                userId,
                args.singScore.mixPath
            )
        )
    }

    private fun loadInterstitialAd(userId: String) {
        val adRequest = AdManagerAdRequest.Builder().build()

        AdManagerInterstitialAd.load(
            requireContext(),
            BuildConfig.APP_INTERSTITIAL_AD_KEY,
            adRequest,
            object : AdManagerInterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Timber.d(adError.message)
                    mInterstitialAd = null
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"
                    Timber.d("Ad onAdFailedToLoad() with error $error")
                    goToDetailsAnalysis(userId)
                }

                override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                    mInterstitialAd = interstitialAd
                    Timber.d("CHECK this Ad was loaded.")
                    showInterstitialAd(userId)
                }
            }
        )
    }

    private fun showInterstitialAd(userId: String) {
        // Show the ad if it's ready. Otherwise log and got to detailed analysis
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null
                    Timber.d("Ad was dismissed.")
                    goToDetailsAnalysis(userId)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    mInterstitialAd = null
                    Timber.d("Ad failed to show.")
                }

                override fun onAdShowedFullScreenContent() {
                    Timber.d("Ad showed fullscreen content.")
                }
            }

            mInterstitialAd?.show(requireActivity())
        } else {
            Timber.d("Ad did not load")
            goToDetailsAnalysis(userId)
        }
    }
}