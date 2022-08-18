package com.sensibol.lucidmusic.singstr.gui.app.contest

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.ContestData
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentContestBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContestFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_contest
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentContestBinding::inflate
    override val binding: FragmentContestBinding get() = super.binding as FragmentContestBinding

    private val contestViewModel: ContestViewModel by viewModels()

    override fun onInitViewModel() {
        super.onInitViewModel()
        contestViewModel.apply {
            getContestData()
        }
    }

    override fun onInitView() {
        contestViewModel.apply {
            failure(failure,::handleFailure)
            observe(contestData,::showContestDetail)
        }
        binding.apply {
            tvContestName.setOnClickListener {
            }
        }
    }

    private fun showContestDetail(contestData: ContestData){
        binding.apply {
            tvContestName.text = contestData.contestName
            tvPrize.text = contestData.prize
            tvHowToEnter.text = contestData.howToEnter
            tvRules.text = contestData.rules
            tvJudgingCriteria.text = contestData.judgingCriteria
        }
    }



}
