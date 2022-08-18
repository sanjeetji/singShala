package com.sensibol.lucidmusic.singstr.gui.app.sing.publish

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.sensibol.android.base.gui.AppToast
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.SimpleAnalysis
import com.sensibol.lucidmusic.singstr.domain.model.names
import com.sensibol.lucidmusic.singstr.gui.BuildConfig
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentCoverPublishBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.math.roundToInt

@AndroidEntryPoint
internal class CoverPublishFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_cover_publish
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentCoverPublishBinding::inflate
    override val binding: FragmentCoverPublishBinding get() = super.binding as FragmentCoverPublishBinding

    private val viewModel: CoverPublishViewModel by viewModels()
    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private val args: CoverPublishFragmentArgs by navArgs()

    private lateinit var adview: AdView
    private var thumbnailTimeMS: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(CoverThumbnailSelectionFragment.REQ_TIMESTAMP) { _, bundle ->
            thumbnailTimeMS = bundle.getInt(CoverThumbnailSelectionFragment.ARG_TIMESTAMP)
            binding.vvCoverPlayer.seekTo(thumbnailTimeMS)
            Timber.d("setFragmentResultListener: thumbnailTimeMS=$thumbnailTimeMS")
        }

        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        singstrViewModel.getUserSubscription()
    }

    private fun onPublishSuccess(simpleAnalysis: SimpleAnalysis) {
        Timber.d("onPublishSuccess:")
        Analytics.logEvent(
            Analytics.Event.PublishedCoverEvent(
                Analytics.Event.Param.SongId(args.song.title),
                Analytics.Event.Param.GenreId("NA"),
                Analytics.Event.Param.ArtistId(args.song.artists.names),
                Analytics.Event.Param.TotalScore(args.singScore.totalScore.roundToInt()),
                Analytics.Event.Param.TuneScore(args.singScore.tuneScore.roundToInt()),
                Analytics.Event.Param.TimeScore(args.singScore.timingScore.roundToInt()),
                Analytics.Event.Param.RightLines(simpleAnalysis.linesDonePerfectly),
                Analytics.Event.Param.WrongLines(simpleAnalysis.linesNeedsImprovement),
                Analytics.Event.Param.CoverId(args.attepmtId)
            )
        )

        binding.apply {
            pbLoading.visibility = VISIBLE
        }

        AppToast.show(requireContext(), "Your Video Will Be Available Soon in Your Profile!")

        singstrViewModel.user.value?.id?.let { userId ->
            findNavController().navigate(CoverPublishFragmentDirections.toDetailAnalysisFragment(args.attepmtId, userId))
        }
    }

    override fun onInitView() {
        binding.apply {
            pbLoading.visibility = GONE

            ivClose.setOnClickListener {
                requireActivity().onBackPressed()
            }

            // for loading google banner ad..
            adview = AdView(root.context)
            adview.adSize = AdSize.MEDIUM_RECTANGLE
            adview.adUnitId = BuildConfig.APP_BANNER_AD_KEY
            flAdContainer.addView(adview)
            adview.loadAd(AdRequest.Builder().build())

            btnPublish.setOnClickListener {
                btnPublish.visibility = GONE
                pbLoading.visibility = VISIBLE
                viewModel.publishCover(args.attepmtId, edtCaption.text.toString(), thumbnailTimeMS)
            }
            tvChangeThumbnail.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.ChangeThumbnailEvent(
                        Analytics.Event.Param.SongId(args.song.title),
                        Analytics.Event.Param.GenreId("NA"),
                        Analytics.Event.Param.ArtistId(args.song.artists.names),
                        Analytics.Event.Param.TotalScore(args.singScore.totalScore.roundToInt()),
                        Analytics.Event.Param.TuneScore(args.singScore.tuneScore.roundToInt()),
                        Analytics.Event.Param.TimeScore(args.singScore.timingScore.roundToInt()),
                        Analytics.Event.Param.CoverId(args.attepmtId)
                    )
                )

                findNavController().navigate(
                    CoverPublishFragmentDirections.toCoverThumbnailSelectionFragment(
                        args.mixPath, thumbnailTimeMS
                    ),
                    FragmentNavigatorExtras(vvCoverPlayer to "videoView")
                )
            }

            vvCoverPlayer.setVideoPath(args.mixPath)
            vvCoverPlayer.setOnPreparedListener {
                if (-1 == thumbnailTimeMS) {
                    thumbnailTimeMS = vvCoverPlayer.duration / 2
                }
                it.seekTo(thumbnailTimeMS)
            }
        }
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(publishSuccess, ::onPublishSuccess)
        }
        singstrViewModel.apply {
            failure(failure, ::handleFailure)
            observe(userSubscription) {
                adview.visibility = if (it.subscribed) GONE else VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        adview.resume()
    }

    override fun onPause() {
        adview.pause()
        super.onPause()
    }
}