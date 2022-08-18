package com.sensibol.lucidmusic.singstr.gui.app.sing.process

import android.media.MediaMetadataRetriever
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.AppToast
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.BuildConfig
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentCoverProcessBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlin.math.floor
import kotlin.math.roundToInt

@AndroidEntryPoint
class CoverProcessFragment : BaseFragment() {
    override val layoutResId: Int get() = R.layout.fragment_cover_process
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentCoverProcessBinding::inflate
    override val binding: FragmentCoverProcessBinding get() = super.binding as FragmentCoverProcessBinding

    private val viewModel: CoverProcessViewModel by viewModels()
    private val args: CoverProcessFragmentArgs by navArgs()

    override fun onInitViewModel() {
        super.onInitViewModel()
        viewModel.apply {
            uploadCover(args.attemptId, args.singScore)
            observe(uploadSuccess, ::handleUploadSuccess)
            observe(updateForStaging) {
                moveToNextScreen()
            }
        }
    }

    override fun onInitView() {
        binding.apply {
            pbFinalScore.progress = args.singScore.totalScore.roundToInt()
            tvFinalScore.text = args.singScore.totalScore.roundToInt().toString()
            try {
                with(MediaMetadataRetriever()) {
                    setDataSource(args.singScore.mixPath)
                    simThumbnail.setImageBitmap(getFrameAtTime(30 * 1000_000L))
                    release()
                }
            } catch (_: Exception) {
                Timber.e("Error occurred while setting the thumbnail")
            }

            viewModel.apply {
                observe(transferProgress) {
                    if (BuildConfig.FLAVOR != "stage")
                        floor(it * 100).toInt().let { progress ->
                            pbProgress.progress = progress
                            tvProcessingPercent.text = "$progress%"
                        }
                }
            }
            ibClose.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }

    private fun handleUploadSuccess(success: Boolean) {
        if (BuildConfig.FLAVOR == "stage") {
//            Toast.makeText(
//                requireContext(),
//                "Please wait, for staging we need additional process to upload cover.\nIn production app this additional process is not needed.",
//                Toast.LENGTH_LONG
//            ).show()

            binding.tvProcessingPercent.text = "0%"
            binding.pbProgress.isIndeterminate = false
            binding.pbProgress.max = 20
            binding.pbProgress.progress = 0
            val timer = object : CountDownTimer(20000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val progress = (millisUntilFinished / 1000).toInt()
                    binding.pbProgress.setProgress(progress, true)
                    binding.tvProcessingPercent.text = "${100 - (progress * 5)}%"
                }

                override fun onFinish() {
                    viewModel.updateDraftForStaging(args.attemptId)
                }
            }
            timer.start()
        } else
            moveToNextScreen()
    }

    private fun moveToNextScreen() {
        findNavController().navigate(
            CoverProcessFragmentDirections.toAnalysisVsPublishChoiceFragment(
                args.song,
                args.singScore,
                args.attemptId
            )
        )
    }
}