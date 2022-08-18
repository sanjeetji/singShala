package com.sensibol.lucidmusic.singstr.gui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.CheckUserExists
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrActivity
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentUserNameBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
internal class UserNameFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_user_name
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentUserNameBinding::inflate
    override val binding get(): FragmentUserNameBinding = super.binding as FragmentUserNameBinding

    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel.apply {
            failure(failure, ::handleFailure)
            observe(checkUserExisten,::checkUserExistenResult)
        }
    }

    override fun onInitView() {

        binding.apply {

            tilUserName.editText?.setText(loginViewModel.userHandleValue)

            if (tvUserName.text.toString().isNotEmpty()){
                tvNextBtn.isEnabled = true
                imgCancel.visibility = VISIBLE
                tvValidUserName.visibility = VISIBLE
                tvValidUserName.setTextColor(requireActivity().getResources().getColor(R.color.teal_500))
                tvValidUserName.text = "This username is available"
                imgCancel.setImageResource(R.drawable.icon_right)
                imgCancel.setColorFilter(ContextCompat.getColor(imgCancel.context, R.color.teal_500), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvNextBtn.background = ContextCompat.getDrawable(tvNextBtn.context, R.drawable.bg_rounded_card_bue_login_with_otp)
            }else{
                imgCancel.visibility = VISIBLE
                tvValidUserName.visibility = VISIBLE
                imgCancel.setImageResource(R.drawable.cross_icon)
                tvValidUserName.setTextColor(requireActivity().getResources().getColor(R.color.cross_color))
                tvValidUserName.text = "username not available, enter different one"
                imgCancel.setColorFilter(ContextCompat.getColor(imgCancel.context, R.color.cross_color), android.graphics.PorterDuff.Mode.MULTIPLY);
            }

            tvUserName.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    loginViewModel.checkUserExisten(s.toString())
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })



            tvNextBtn.setOnClickListener {
                findNavController().navigate(UserNameFragmentDirections.toUserGenderFragment())
            }

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }
        }
    }


    private fun checkUserExistenResult(checkUserExists: CheckUserExists) {
        if (checkUserExists.data.userExists){
            binding.apply {
                tvNextBtn.isEnabled = false
                imgCancel.visibility = VISIBLE
                tvValidUserName.visibility = VISIBLE
                imgCancel.setImageResource(R.drawable.cross_icon)
                tvValidUserName.setTextColor(requireActivity().getResources().getColor(R.color.cross_color))
                tvValidUserName.text = "username not available, enter different one"
                imgCancel.setColorFilter(ContextCompat.getColor(imgCancel.context, R.color.cross_color), android.graphics.PorterDuff.Mode.MULTIPLY);
                tvNextBtn.background = ContextCompat.getDrawable(tvNextBtn.context, R.drawable.bg_rounded_card_next)
            }
        }else{
           binding.apply {
               tvNextBtn.isEnabled = true
               imgCancel.visibility = VISIBLE
               tvValidUserName.visibility = VISIBLE
               tvValidUserName.setTextColor(requireActivity().getResources().getColor(R.color.teal_500))
               tvValidUserName.text = "This username is available"
               imgCancel.setImageResource(R.drawable.icon_right)
               imgCancel.setColorFilter(ContextCompat.getColor(imgCancel.context, R.color.teal_500), android.graphics.PorterDuff.Mode.MULTIPLY);
               tvNextBtn.background = ContextCompat.getDrawable(tvNextBtn.context, R.drawable.bg_rounded_card_bue_login_with_otp)
           }
        }
    }
}