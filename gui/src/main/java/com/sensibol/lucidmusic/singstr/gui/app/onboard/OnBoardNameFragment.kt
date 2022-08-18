package com.sensibol.lucidmusic.singstr.gui.app.onboard

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentOnBoardNameBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardNameFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_on_board_name
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentOnBoardNameBinding::inflate
    override val binding: FragmentOnBoardNameBinding get() = super.binding as FragmentOnBoardNameBinding

    private val viewModel: OnBoardUserDetailsViewModel by viewModels()

    override fun onInitView() {
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(success, ::handleNavigation)
        }
        binding.apply {
            tietName.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    tvNext.isEnabled = s?.length != 0
                }
            })
            tvNext.setOnClickListener {
                if (tietName.text?.isNotEmpty() == true) {
                    viewModel.updateUserDetail(tietName.text.toString(), null, null)
                }
            }
            tvSkip.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.SkippedWalkThroughEvent(
                        Analytics.Event.Param.SkippedScreenName("User Name")
                    )
                )
                findNavController().navigate(OnBoardNameFragmentDirections.toOnBoardDetailsFragment(""))
            }
        }
    }

    private fun handleNavigation(isUserUpdated: Boolean) {
        findNavController().navigate(OnBoardNameFragmentDirections.toOnBoardDetailsFragment(binding.tietName.text.toString()))
    }

}