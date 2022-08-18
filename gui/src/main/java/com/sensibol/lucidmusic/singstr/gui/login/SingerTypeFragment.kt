package com.sensibol.lucidmusic.singstr.gui.login

import android.content.Intent
import android.os.Bundle
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
import com.sensibol.lucidmusic.singstr.domain.database.AppDatabase
import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.Singup
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrActivity
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentSingerCategoryBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentSingerTypeBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
internal class SingerTypeFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_singer_type
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentSingerTypeBinding::inflate
    override val binding get() : FragmentSingerTypeBinding = super.binding as FragmentSingerTypeBinding

    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel.apply {
            failure(failure, ::handleFailure)
            observe(singupUser,::showSignupResult)
            observe(phoneToken, :: navigateToHomeScreen)
        }
    }

    private fun navigateToHomeScreen(authToken: AuthToken) {
        loginViewModel.saveAuthToken(authToken)
        loginViewModel.setUserOnBoarded(true)
        requireActivity().finish()
        startActivity(Intent(requireContext(), SingstrActivity::class.java))
    }

    private fun navigateToSingstrApp() {
        requireActivity().finish()
        startActivity(Intent(requireContext(), SingstrActivity::class.java))
    }

    private fun showSignupResult(singup: Singup) {
        if (singup.message.equals("SUCCESS")){
            if (singup.data != null){
                loginViewModel.generateAccessPhoneToken(loginViewModel.phoneNumber)
            }
        }
    }

    override fun onInitView() {
        binding.apply {
            cardViewOne.setOnClickListener {
                tvNextBtn.visibility = VISIBLE
                loginViewModel.singerType = "Beginner"
                cardViewOne.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_singer_category)
                cardViewTwo.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_white_singer_category)
                cardViewThree.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_white_singer_category)
            }
            cardViewTwo.setOnClickListener {
                tvNextBtn.visibility = VISIBLE
                loginViewModel.singerType = "Intermediate"
                cardViewTwo.foreground = ContextCompat.getDrawable(cardViewTwo.context, R.drawable.bg_rounded_singer_category)
                cardViewOne.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_white_singer_category)
                cardViewThree.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_white_singer_category)
            }
            cardViewThree.setOnClickListener {
                tvNextBtn.visibility = VISIBLE
                loginViewModel.singerType = "Advanced"
                cardViewThree.foreground = ContextCompat.getDrawable(cardViewThree.context, R.drawable.bg_rounded_singer_category)
                cardViewOne.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_white_singer_category)
                cardViewTwo.foreground = ContextCompat.getDrawable(cardViewOne.context, R.drawable.bg_rounded_white_singer_category)
            }

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            tvNextBtn.setOnClickListener {

                loginViewModel.performSignUp(1,"")
            }
        }
    }
}