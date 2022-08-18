package com.sensibol.lucidmusic.singstr.gui.app.onboard.walkthrough

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentAppWalkThroughBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
internal class AppWalkThroughFragment : BaseFragment() {
    override val layoutResId = R.layout.fragment_app_walk_through
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentAppWalkThroughBinding::inflate
    override val binding get():FragmentAppWalkThroughBinding = super.binding as FragmentAppWalkThroughBinding

    private val viewModel: AppWalkThroughViewModel by viewModels()

    @Inject
    internal lateinit var appWalkThroughSlidesAdapter: AppWalkThroughSlidesAdapter

    override fun onInitView() {
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(walkThroughSlides) { appWalkThroughSlidesAdapter.slides = it }
            fetchWalkThroughSlide()
        }
        binding.apply {
            tvGetStarted.setOnClickListener {
                clOnboardingMain.visibility = GONE
                clOnboardSlider.visibility = VISIBLE
                vpSlidePager.adapter = appWalkThroughSlidesAdapter
                TabLayoutMediator(tabIndicator, vpSlidePager) { tab, _ ->
                    tab.isSelected
                }.attach()
            }

            tvNext.setOnClickListener {
                Timber.d("Next button clicked")
                if (vpSlidePager.currentItem + 1 < appWalkThroughSlidesAdapter.itemCount) {
                    vpSlidePager.currentItem += 1
                } else {
                    findNavController().navigate(AppWalkThroughFragmentDirections.toOnBoardNameFragment())
                }
            }
        }
    }
}