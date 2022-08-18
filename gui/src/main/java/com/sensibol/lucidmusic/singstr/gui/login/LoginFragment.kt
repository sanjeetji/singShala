package com.sensibol.lucidmusic.singstr.gui.login

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.messaging.FirebaseMessaging
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.SystemProperties
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrActivity
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.app.analytics.Analytics
import com.sensibol.lucidmusic.singstr.gui.app.home.HomeFragment
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentLoginBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.security.MessageDigest
import javax.inject.Inject
import kotlin.math.abs


@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    override val layoutResId = R.layout.fragment_login
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentLoginBinding::inflate
    override val binding get() : FragmentLoginBinding = super.binding as FragmentLoginBinding

    private val loginViewModel: LoginViewModel by activityViewModels()

    // FIXME - SingstrViewModel in LoginActivity scope. Is it intended?
    private val singstrViewModel: SingstrViewModel by activityViewModels()

    @Inject
    internal lateinit var loginBannersAdapter: LoginBannersAdapter

    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginViewModel.apply {
            failure(failure, ::handleFailure)
            observe(isUserLoginSuccessful, { navigateToSingstrApp() })
        }
    }

    override fun onInitView() {

        ObjectAnimator.ofPropertyValuesHolder(
            binding.ivRotatingLogo,
            PropertyValuesHolder.ofFloat("scaleX", 0.9f),
            PropertyValuesHolder.ofFloat("scaleY", 0.9f)
        ).apply {
            duration = 5_000
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            start()
        }

        binding.apply {
            pbLoading.visibility = GONE
            ivRotatingLogo.startAnimation(AnimationUtils.loadAnimation(context, R.anim.clockwise))

            vpLogin.apply {
                adapter = loginBannersAdapter
                clipToPadding = false
                clipChildren = false
                offscreenPageLimit = 4
                setPageTransformer(CompositePageTransformer().apply {
                    addTransformer(MarginPageTransformer(resources.getDimension(R.dimen.home_carousel_banner_margin).toInt()))
                    addTransformer { page, position -> page.scaleY = 0.85f + (1 - abs(position)) * 0.15f }
                })
            }

            TabLayoutMediator(tabLayout, vpLogin) { tab, position ->
            }.attach()

            tvPhoneNumber.addTextChangedListener(object :TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(s: Editable?) {

                    val prefix = "+91 "
                    val count = s?.toString()?.length ?: 0
                    if (count < prefix.length) {
                        if (s.toString() != prefix.trim()) {
                            tvPhoneNumber.setText("$prefix$s")
                        } else {
                            tvPhoneNumber.setText(prefix)
                        }
                        tvPhoneNumber.setSelection(tvPhoneNumber.length())
                    }

                    if (tvPhoneNumber.text?.length == 14){
                        tvLoginWithOtp.background = ContextCompat.getDrawable(tvLoginWithOtp.context, R.drawable.bg_rounded_card_bue_login_with_otp)
                        tvLoginWithOtp.isEnabled = true
                    }else{
                        tvLoginWithOtp.background = ContextCompat.getDrawable(tvLoginWithOtp.context, R.drawable.bg_rounded_card_login_with_otp)
                        tvLoginWithOtp.isEnabled = false
                    }
                }

            })

            callbackManager =  CallbackManager.Factory.create()
            fbLoginPerform.apply {
                fragment = (this@LoginFragment)
                setPermissions(listOf("email, public_profile"))
                registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        Timber.d("Facebook accessToken ${loginResult?.accessToken}")
                        Timber.d("Facebook accessToken token ${loginResult?.accessToken?.token}")
                        loginViewModel.updateAuthTokenFromServer(
                            "Facebook", loginResult?.accessToken?.token.toString()
                        )
                    }

                    override fun onCancel() {
                        pbLoading.visibility = GONE
                    }

                    override fun onError(exception: FacebookException) {
                        Timber.e("Facebook login failed-> $exception")
                        pbLoading.visibility = GONE
                    }
                })
            }

            btnLoginFb.setOnClickListener {
                fbLoginPerform.performClick()
                pbLoading.visibility = VISIBLE
            }

            tvLoginWithOtp.setOnClickListener {
                val phoneNumber:String = tvPhoneNumber.text.toString().replace(" ","")
                loginViewModel.phoneNumber = phoneNumber
                findNavController().navigate(LoginFragmentDirections.toVerifyOtpFragment())
            }

            btnLoginGoogle.setOnClickListener {
                Analytics.logEvent(
                    Analytics.Event.LoginEvent(
                        Analytics.Event.Param.Method("google login")
                    )
                )
                pbLoading.visibility = VISIBLE

                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(SystemProperties.GSO_REQ_ID_TOKEN)
                    .requestEmail()
                    .build()

                startActivityForResult(GoogleSignIn.getClient(requireContext(), gso).signInIntent, RC_SIGN_IN)
            }
            val text = ("By logging in you accept all the Terms and Conditions and Privacy Policies of the product").toSpannable()

            // Set clickable span
            text[33..53] = object : ClickableSpan() {
                override fun onClick(view: View) {
                    val urlLink = "https://singshala.com/terms-of-use/"
                    findNavController().navigate(LoginFragmentDirections.toWebViewFragment(urlLink))
                }
            }
            text[58..74] = object : ClickableSpan() {
                override fun onClick(view: View) {
                    val urlLink = "https://singshala.com/privacy-policy/"
                    findNavController().navigate(LoginFragmentDirections.toWebViewFragment(urlLink))
                }
            }

            tvTnC.text = text
            tvTnC.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.d("onActivityResult: IN")
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode== Activity.RESULT_OK) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                account.idToken?.let {
                    Timber.d("GoogleToken--> $it")
                    loginViewModel.updateAuthTokenFromServer("Google", it)
//                    binding.btnLoginGoogle.visibility = INVISIBLE
                    binding.pbLoading.visibility = INVISIBLE
                }
            } catch (e: ApiException) {
                Timber.e(e, "onActivityResult")
                Toast.makeText(context, "Exception $e", Toast.LENGTH_SHORT).show()
                binding.pbLoading.visibility = INVISIBLE
            }
        }
        else{
            binding.pbLoading.visibility = INVISIBLE
//            Toast.makeText(context, "Please select an account to continue", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToSingstrApp() {
        Timber.d("Navigating to Singster App")
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                Timber.e("Couldn't read firebase messaging toke. ${it.exception}")
            } else {
                Timber.d("FirebaseMessagingToken: ${it.result}")
                val token = it.result
                if (token != null) {
                    singstrViewModel.subscribeNotification(token)

                    Timber.d("Messaging Data is $token")
                }
            }
        }

        requireActivity().finish()
        startActivity(Intent(requireContext(), SingstrActivity::class.java))
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private fun scheduleLoginBannerSwitch() {
        binding.vpLogin.apply {
            removeCallbacks(actionSwitchCarouselBanner)
            postDelayed(actionSwitchCarouselBanner, HomeFragment.BANNER_SWITCH_TIME_MS)
        }
    }
    private val loginPageChangeCallback: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            when (state) {
                ViewPager2.SCROLL_STATE_IDLE -> loginBannersAdapter
                else -> binding.vpLogin.removeCallbacks(actionSwitchCarouselBanner)
            }
        }
    }
    private val actionSwitchCarouselBanner: Runnable = Runnable {
        binding.vpLogin.apply {
            currentItem = if (currentItem == 4) 0 else currentItem + 1
        }
        scheduleLoginBannerSwitch()
    }

    override fun onPause() {
        binding.vpLogin.apply {
            removeCallbacks(actionSwitchCarouselBanner)
            unregisterOnPageChangeCallback(loginPageChangeCallback)
        }
        super.onPause()
    }
    override fun onResume() {
        super.onResume()
        scheduleCarouselBannerSwitch()
        binding.vpLogin.apply {
            registerOnPageChangeCallback(loginPageChangeCallback)
        }
    }
    private fun scheduleCarouselBannerSwitch() {
        binding.vpLogin.apply {
            removeCallbacks(actionSwitchCarouselBanner)
            postDelayed(actionSwitchCarouselBanner, HomeFragment.BANNER_SWITCH_TIME_MS)
        }
    }
}
