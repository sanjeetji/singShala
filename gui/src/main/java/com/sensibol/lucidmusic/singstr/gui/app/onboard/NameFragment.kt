package com.sensibol.lucidmusic.singstr.gui.app.onboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.User
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentNameBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentOnBoardDetailsBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
internal class NameFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_name
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentNameBinding::inflate
    override val binding: FragmentNameBinding get() = super.binding as FragmentNameBinding

    private val singstrViewModel: SingstrViewModel by activityViewModels()
    private val nameViewModel: NameViewModel by viewModels()

    override fun onInitView() {

        singstrViewModel.apply {
            failure(failure, ::handleFailure)
            observe(user, ::handleUserInfo)
        }

        nameViewModel.apply {
            failure(failure, ::handleFailure)
            observe(updateUser) {
                findNavController().navigate(NameFragmentDirections.toPickUsernameFragment(
                    binding.tilFirstName.editText?.text?.trim().toString(),
                    binding.tilLastName.editText?.text?.trim().toString()
                ))
            }
        }

        binding.apply {

            progressBar.visibility = View.GONE

            tvNextBtn.setOnClickListener {
                val firstName = tilFirstName.editText?.text?.trim().toString()
                if (firstName.isNotBlank()) {
                    progressBar.visibility = View.VISIBLE
                    nameViewModel.updateUserDetail(
                        tilFirstName.editText?.text?.trim().toString(),
                        tilLastName.editText?.text?.trim().toString()
                    )
                }
            }
        }
    }

    private fun handleUserInfo(user: User) {
        binding.apply {
            tilFirstName.editText?.setText(user.name)
            tilLastName.editText?.setText(user.lastName)
        }
    }
}