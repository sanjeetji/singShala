package com.sensibol.lucidmusic.singstr.gui.app.onboard

import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentSingerLevelBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SingerLevelFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_singer_level
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentSingerLevelBinding::inflate
    override val binding: FragmentSingerLevelBinding get() = super.binding as FragmentSingerLevelBinding

    private val viewModel: SingerLevelViewModel by viewModels()

    private val arg: SingerLevelFragmentArgs by navArgs()

    override fun onInitView() {
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(updateUser){
                findNavController().navigate(SingerLevelFragmentDirections.toHomeFragment())
            }
        }

        binding.apply {
            progressBar.visibility = View.GONE
            tvNextBtn.visibility = View.GONE
            cardViewOne.setOnClickListener {
                tvNextBtn.visibility = View.VISIBLE
                viewModel.singerType = "Beginner"
                cardViewOne.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_singer_category)
                cardViewTwo.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_white_singer_category)
                cardViewThree.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_white_singer_category)
            }
            cardViewTwo.setOnClickListener {
                tvNextBtn.visibility = View.VISIBLE
                viewModel.singerType = "Intermediate"
                cardViewTwo.foreground = ContextCompat.getDrawable(cardViewTwo.context, R.drawable.bg_rounded_singer_category)
                cardViewOne.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_white_singer_category)
                cardViewThree.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_white_singer_category)
            }
            cardViewThree.setOnClickListener {
                tvNextBtn.visibility = View.VISIBLE
                viewModel.singerType = "Advanced"
                cardViewThree.foreground = ContextCompat.getDrawable(cardViewThree.context, R.drawable.bg_rounded_singer_category)
                cardViewOne.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_white_singer_category)
                cardViewTwo.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_white_singer_category)
            }

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            tvNextBtn.setOnClickListener {

                if(viewModel.singerType.isNotBlank()){
                    progressBar.visibility = View.VISIBLE
                    viewModel.updateUserDetail(arg.firstName)
                }
            }
        }
    }
}