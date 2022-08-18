package com.sensibol.lucidmusic.singstr.gui.app.profile.self

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentPlayCoverBinding

class PlayCoverFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_play_cover
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentPlayCoverBinding::inflate

    val args: PlayCoverFragmentArgs by navArgs()


    override fun onInitView() {
        (binding as FragmentPlayCoverBinding).apply {
            vvCoverPlayer.setVideoPath(args.coversData.coverUrl)
            vvCoverPlayer.setOnPreparedListener {
                it.seekTo(10)
            }

            vvCoverPlayer.setOnClickListener {
                if (vvCoverPlayer.isPlaying) {
                    ivPlayPause.setImageResource(R.drawable.ic_pause)
                    vvCoverPlayer.pause()
                } else {
                    ivPlayPause.setImageResource(R.drawable.ic_play)
                    vvCoverPlayer.start()
                }
            }
        }
    }
}