package com.sensibol.lucidmusic.singstr.gui.app.onboard

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.CheckUserExists
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentPickUsernameBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel

@AndroidEntryPoint
class PickUsernameFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_pick_username
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentPickUsernameBinding::inflate
    override val binding: FragmentPickUsernameBinding get() = super.binding as FragmentPickUsernameBinding

    private val viewModel: PickUsernameViewModel by viewModels()

    private val arg: PickUsernameFragmentArgs by navArgs()

    override fun onInitViewModel() {
        super.onInitViewModel()
        var checkValue = arg.firstName
        if (arg.lastName.isNotBlank()) {
            checkValue = arg.firstName + arg.lastName
        }
        viewModel.checkUserExits(checkValue)
    }

    override fun onInitView() {

        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(userExits, ::handleUserExistence)
            observe(updateUser){
                findNavController().navigate(PickUsernameFragmentDirections.toDobGenderFragment(arg.firstName))
            }
        }

        binding.apply {

            tilUserName.editText?.setText(arg.firstName + arg.lastName)
            progressBar.visibility = View.VISIBLE

            tilUserName.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if(s.toString().trim().isNotBlank()){
                        progressBar.visibility = View.VISIBLE
                        viewModel.checkUserExits(s.toString().trim())
                    }else{
                        binding.apply {
                            tvNextBtn.isEnabled = false
                            progressBar.visibility = View.GONE
                            tvUsernameExistence.visibility = View.GONE
                            ivUsernameExistence.visibility = View.GONE
                        }
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }
            })

            tvNextBtn.setOnClickListener {
                val userName = tilUserName.editText?.text?.toString()?.trim()
                userName?.let {
                    progressBar.visibility = View.VISIBLE
                    viewModel.updateUserDetail(arg.firstName, userName)
                }
            }
        }
    }

    private fun handleUserExistence(existence: CheckUserExists) {
        binding.apply {
            progressBar.visibility = View.GONE
            tvUsernameExistence.visibility = View.VISIBLE
            ivUsernameExistence.visibility = View.VISIBLE
        }

        if (existence.data.userExists) {
            binding.apply {
                tvNextBtn.isEnabled = false
                ivUsernameExistence.setImageResource(R.drawable.cross_icon)
                tvUsernameExistence.setTextColor(ContextCompat.getColor(requireContext(), R.color.bg_red))
                tvUsernameExistence.text = "username not available, enter different one"
            }
        } else {
            binding.apply {
                tvNextBtn.isEnabled = true
                ivUsernameExistence.setImageResource(R.drawable.icon_right)
                tvUsernameExistence.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_user_existence))
                tvUsernameExistence.text = "This username is available"
            }
        }

    }
}