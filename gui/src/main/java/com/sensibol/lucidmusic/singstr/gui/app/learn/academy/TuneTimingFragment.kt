package com.sensibol.lucidmusic.singstr.gui.app.learn.academy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentTuningAndTimingBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TuneTimingFragment : BaseFragment() {
    override val layoutResId = R.layout.fragment_tuning_and_timing
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentTuningAndTimingBinding::inflate
    override val binding: FragmentTuningAndTimingBinding get() = super.binding as FragmentTuningAndTimingBinding
//    private val viewModel: LearnViewModel by viewModels()

    @Inject
    internal lateinit var tuneTimingAdapter: TuneTimingAdapter

    override fun onInitView() {
        binding.apply{
            rvImproveTuning.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = tuneTimingAdapter
            }
        }
//        viewModel.apply {
//            failure(failure, ::handleFailure)
//            observe(tagLessons,{ tuneTimingAdapter.lessons = it })
//        }
    }

}