package com.sensibol.lucidmusic.singstr.gui.app.exftr

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.lucidmusic.singstr.domain.model.SingMode
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentExclusiveFeaturesBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExclusiveFeaturesFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_exclusive_features
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentExclusiveFeaturesBinding::inflate
    override val binding: FragmentExclusiveFeaturesBinding get() = super.binding as FragmentExclusiveFeaturesBinding

    @Inject
    internal lateinit var exclusiveFeaturesAdapter: ExclusiveFeaturesAdapter

    override fun onInitView() {
        binding.apply {
            rvTeacherExperience.apply {
                adapter = exclusiveFeaturesAdapter
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            }
            exclusiveFeaturesAdapter.onClickListener = { exclusiveFeature ->
                findNavController().navigate(exclusiveFeature.navDirections)
            }
            ivBtnBack.setOnClickListener {
                findNavController().popBackStack()
            }

        }
    }

    companion object {
        val exclusiveFeatureList = arrayListOf(
            ExclusiveFeature(
                "Leaderboard",
                R.string.ftr_leaderboard_desc,
                "View Leaderboard",
                R.drawable.ftr_leaderboard,
                ExclusiveFeaturesFragmentDirections.toLeaderBoardFragment()
            ),
            ExclusiveFeature(
                "Detailed Analysis",
                R.string.ftr_detailed_analysis_desc,
                "Record Cover",
                R.drawable.ftr_detailed_analysis,
                ExclusiveFeaturesFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName)
            ),
            ExclusiveFeature(
                "Audio Exercises",
                R.string.ftr_audio_exercises_desc,
                "View Lessons",
                R.drawable.ftr_audio_exercises,
                ExclusiveFeaturesFragmentDirections.toLearnFragment()
            ),
            ExclusiveFeature(
                "Scoring Tech",
                R.string.ftr_scoring_tech_desc,
                "Sing a Song",
                R.drawable.ftr_scoring_tech,
                ExclusiveFeaturesFragmentDirections.toPrepareSingHostFragment(SingMode.RECORD.argName)
            ),
            ExclusiveFeature(
                "Practice Mode",
                R.string.ftr_practice_mode_desc,
                "Practice a Song",
                R.drawable.ftr_practice_mode,
                ExclusiveFeaturesFragmentDirections.toPrepareSingHostFragment(SingMode.PRACTICE.argName)
            ),
            ExclusiveFeature(
                "Guided Learning",
                R.string.ftr_guided_learning_desc,
                "Begin Learning",
                R.drawable.ftr_guided_learning,
                ExclusiveFeaturesFragmentDirections.toLearnFragment()
            ),
            ExclusiveFeature(
                "singShala Community",
                R.string.ftr_singshala_community_desc,
                "View Covers",
                R.drawable.ftr_singshala_community,
                ExclusiveFeaturesFragmentDirections.toExploreFragment()
            )
        )
    }
}
