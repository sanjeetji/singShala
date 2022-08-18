package com.sensibol.lucidmusic.singstr.gui.app.onboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.SingMode
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentOnBoardSongRecommendationBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardSongRecommendationFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_on_board_song_recommendation
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentOnBoardSongRecommendationBinding::inflate
    override val binding: FragmentOnBoardSongRecommendationBinding get() = super.binding as FragmentOnBoardSongRecommendationBinding

    @Inject
    lateinit var songRecommendAdapter: SongRecommendationAdapter

    private val viewModel: OnBoardRecommendSongViewModel by viewModels()

    override fun onInitView() {
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(recommendSong, { songRecommendAdapter.songs = it })
            loadRecommendSong()
        }
        binding.apply {
            tvSkip.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.SkippedWalkThroughEvent(
                        Analytics.Event.Param.SkippedScreenName("Recommended Song")
                    )
                )
//                findNavController().popBackStack(R.id.homeFragment, true)
                findNavController().navigate(OnBoardSongRecommendationFragmentDirections.toHomeFragment())
            }
            rvList.apply {
                isNestedScrollingEnabled = false
                layoutManager = GridLayoutManager(binding.root.context, 2)
                adapter = songRecommendAdapter
            }
            songRecommendAdapter.onSingClickListener = { songMini ->
                findNavController().navigate(OnBoardSongRecommendationFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName, songMini.id))
            }
        }
    }
}