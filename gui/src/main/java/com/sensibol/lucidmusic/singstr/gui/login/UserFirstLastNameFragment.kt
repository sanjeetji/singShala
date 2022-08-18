package com.sensibol.lucidmusic.singstr.gui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.UserHandle
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentUserFirstLastNameBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
internal class UserFirstLastNameFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_user_first_last_name
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentUserFirstLastNameBinding::inflate
    override val binding get(): FragmentUserFirstLastNameBinding = super.binding as FragmentUserFirstLastNameBinding


    private val loginViewModel: LoginViewModel by activityViewModels()


    var firstNameStr:String = ""
    var lastNameStr:String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel.apply {
            failure(failure, ::handleFailure)
            observe(userHandle,::userHandleResult)
        }
    }

    private fun userHandleResult(userHandleData: UserHandle) {
        if (userHandleData.data.userHandle.isNotEmpty()){
            loginViewModel.apply {
                firstName = firstNameStr
                lastName = lastNameStr
                userHandleValue = userHandleData.data.userHandle
            }
            findNavController().navigate(UserFirstLastNameFragmentDirections.toUserNameFragment()
            )
        }
    }

    override fun onInitView() {
        binding.apply {

            tvLastName.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (s?.length!! >2){
                        tvNextBtn.isEnabled = true
                        tvNextBtn.background = ContextCompat.getDrawable(tvNextBtn.context, R.drawable.bg_rounded_card_bue_login_with_otp)
                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            } )

            lastNameStr = tvLastName.text.toString()
            tvNextBtn.setOnClickListener {

                firstNameStr = tvFirstName.text.toString()

                if (firstNameStr.isEmpty()){
                    Toast.makeText(requireContext(),"First name can't be blank",Toast.LENGTH_SHORT).show()
                }else{
                    lastNameStr = tvLastName.text.toString()
                    loginViewModel.userHandle(firstNameStr,lastNameStr)
                }
            }
        }
    }


}