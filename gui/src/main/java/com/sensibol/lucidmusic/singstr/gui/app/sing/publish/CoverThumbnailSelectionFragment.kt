package com.sensibol.lucidmusic.singstr.gui.app.sing.publish

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentCoverThumbnailSelectionBinding
import com.sensibol.lucidmusic.singstr.gui.timeMS2mmss

internal class CoverThumbnailSelectionFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_cover_thumbnail_selection
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentCoverThumbnailSelectionBinding::inflate
    override val binding: FragmentCoverThumbnailSelectionBinding get() = super.binding as FragmentCoverThumbnailSelectionBinding

    private val args: CoverThumbnailSelectionFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onInitView() {
        binding.apply {
            ivClose.setOnClickListener {
                requireActivity().onBackPressed()
            }

            seekBar.setOnSeekBarChangeListener(seekBarListener)

            vvCoverPlayer.setVideoPath(args.videoPath)
            vvCoverPlayer.setOnPreparedListener {
                seekBar.max = vvCoverPlayer.duration
                seekBar.progress = args.thumbnailTimeMS
                it.seekTo(args.thumbnailTimeMS)
            }

            tvSetThumbnail.setOnClickListener {
                setFragmentResult(REQ_TIMESTAMP, bundleOf(ARG_TIMESTAMP to seekBar.progress))
                findNavController().popBackStack()
            }
        }
    }

    private val seekBarListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (fromUser) {
                binding.apply {
                    vvCoverPlayer.seekTo(progress)
                    tvTimer.text = timeMS2mmss(progress)
                }
            }
        }


        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {}
    }

    companion object {
        const val REQ_TIMESTAMP = "CoverThumbnailSelectionFragment.REQ_TIMESTAMP"
        const val ARG_TIMESTAMP = "CoverThumbnailSelectionFragment.ARG_TIMESTAMP"
    }

}