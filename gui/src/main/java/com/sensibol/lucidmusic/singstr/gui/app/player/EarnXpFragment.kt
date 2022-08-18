package com.sensibol.lucidmusic.singstr.gui.app.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.DailyChallenge
import com.sensibol.lucidmusic.singstr.domain.model.SingMode
import com.sensibol.lucidmusic.singstr.domain.model.names
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentEarnXpBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import com.sensibol.lucidmusic.singstr.gui.loadUrl
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EarnXpFragment : BaseFragment() {

    override val layoutResId: Int get() = R.layout.fragment_earn_xp
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding get() = FragmentEarnXpBinding::inflate
    override val binding: FragmentEarnXpBinding get() = super.binding as FragmentEarnXpBinding

    private val activityViewModel: SingstrViewModel by viewModels()

    override fun onInitView() {
        activityViewModel.apply {
            failure(failure, ::handleFailure)
            observe(dailyChallenge, ::showDailyChallenge)
//            loadDailyChallenge()
        }
        binding.apply {
            ivBtnBack.setOnClickListener {
                findNavController().popBackStack()
            }

            txvBtnRecordNow.setOnClickListener {
                findNavController().navigate(EarnXpFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName))
            }

            txvBtnPracticeNow.setOnClickListener {
                findNavController().navigate(EarnXpFragmentDirections.toPrepareSingHostFragment(SingMode.PRACTICE.argName))
            }

            txvBtnAnswerMCQ.setOnClickListener {
                findNavController().navigate(EarnXpFragmentDirections.actionEarnXpFragmentToLearnFragment())
            }
//            txvBtnWatchLesson.setOnClickListener {
//                findNavController().navigate(EarnXpFragmentDirections.toLearnFragment())
//            }
        }
    }

    private fun showDailyChallenge(dailyChallenge: DailyChallenge) {
        val song = dailyChallenge.song
        binding.incDailyChallenge.apply {
            tvTitle.text = song.title
            tvSubtitle.text = song.artists.names
            //tvFragMainFirstTimeSongGroup.text = song.lyrics
            ivThumbnail.loadUrl(song.thumbnailUrl)
            tvSing.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.DailyChallengeAttemptEvent(
                        Analytics.Event.Param.SongId(song.title),
                        Analytics.Event.Param.GenreId("NA"),
                        Analytics.Event.Param.ArtistId(song.artists.names),
                    )
                )

                findNavController().navigate(
                    EarnXpFragmentDirections.toPrepareSingHostFragment(SingMode.DAILY_CHALLENGE.argName, dailyChallenge.song.id)
                )
            }
        }


    }
}