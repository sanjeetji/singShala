package com.sensibol.lucidmusic.singstr.gui.login

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.AuthToken
import com.sensibol.lucidmusic.singstr.domain.model.CheckUserExists
import com.sensibol.lucidmusic.singstr.gui.R
import com.sensibol.lucidmusic.singstr.gui.app.SingstrActivity
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentVerifyOtpBinding
import com.sensibol.lucidmusic.singstr.gui.handleFailure
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
internal class VerifyOtpFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_verify_otp
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentVerifyOtpBinding::inflate
    override val binding get(): FragmentVerifyOtpBinding = super.binding as FragmentVerifyOtpBinding

    private val loginViewModel: LoginViewModel by activityViewModels()

    private lateinit var alertDialog: AlertDialog

    private lateinit var sentCode: String
    private var mUserCode: String = ""

    private lateinit var timer: CountDownTimer
    private var mTimeLeftInMillis: Long = 60000

    private lateinit var smsRetriever: SMSBroadcastReceiver

    companion object {
        private const val SMS_CONSENT_REQUEST = 9002
    }

    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private val mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {

            phoneAuthCredential.smsCode?.let {
                mUserCode = it
            }
            Timber.d("onVerificationCompleted code $mUserCode")
            if (mUserCode.isNotBlank()) {
                binding.otpView.setOTP(mUserCode)
                Timber.d("onVerificationCompleted code start auto $mUserCode")
                verifyCode()
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            Timber.d("onVerificationCompleted ${e.message}")
            if (this@VerifyOtpFragment::alertDialog.isInitialized) {
                alertDialog.dismiss()
                Timber.d("onVerificationCompleted dialog closed ${e.message}")
            }
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            sentCode = verificationId
        }

        override fun onCodeAutoRetrievalTimeOut(s: String) {
            super.onCodeAutoRetrievalTimeOut(s)
            if (this@VerifyOtpFragment::alertDialog.isInitialized) {
                alertDialog.dismiss()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel.apply {
            failure(failure, ::handleFailure)
            observe(checkUserExisten, ::checkUserExistenResult)
            observe(phoneToken, ::navigateToHomeScreen)
        }

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(this@VerifyOtpFragment::timer.isInitialized){
                    timer.cancel()
                    mCallbacks.onCodeAutoRetrievalTimeOut("manual")
                    onBackPressedCallback.remove()
                    requireActivity().onBackPressed()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onInitView() {
        binding.apply {
            tvPhoneNumber.text = loginViewModel.phoneNumber
            progressBar.visibility = GONE
            sendVerificationCode(loginViewModel.phoneNumber)
            if (!this@VerifyOtpFragment::alertDialog.isInitialized) {
                showAutoVerificationDialog()
            }
            startSmsUserConsent()

            tvVerifyOtp.setOnClickListener {
                otpView.getEnterOTP().let {
                    if(it.length == 6){
                        mUserCode = it
                        verifyCode()
                    }
                }
            }

//            tvVerifyOtp.setOnClickListener {
//                if (otpView.getEnterOTP().length == 6) {
////                    tvVerifyOtp.isEnabled = true
////                    tvVerifyOtp.isClickable = true
//                    tvVerifyOtp.background = ContextCompat.getDrawable(tvVerifyOtp.context, R.drawable.bg_rounded_card_bue_login_with_otp)
//                    mUserCode = otpView.getEnterOTP()
//                    verifyCode()
//                } else {
//                    tvVerifyOtp.isEnabled = true
//                    tvVerifyOtp.isClickable = false
//                }
//            }

            tvChangeNumber.setOnClickListener {
                requireActivity().onBackPressed()
            }

            ivBack.setOnClickListener {
                requireActivity().onBackPressed()
            }

            tvResend.setOnClickListener {
                mTimeLeftInMillis = 60000
                sendVerificationCode(loginViewModel.phoneNumber)
                startTimer()
                showAutoVerificationDialog()
            }

            otpView.setListener(object: OtpViewListener{
                override fun onEnterOtp(otp: String) {
                    if(otp.length == 6)
                        tvVerifyOtp.isEnabled = true
                }

            })

        }
    }

    private fun sendVerificationCode(mobile: String) {

        val options = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
            .setPhoneNumber(mobile)
            .setTimeout(0, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(mCallbacks)
            .build()
        Timber.d("sendVerificationCode")
        PhoneAuthProvider.verifyPhoneNumber(options)

        if (!this@VerifyOtpFragment::timer.isInitialized) {
            startTimer()
        }

    }

    private fun verifyCode() {
        if (this@VerifyOtpFragment::sentCode.isInitialized) {
            binding.progressBar.visibility = VISIBLE
            val credential = PhoneAuthProvider.getCredential(sentCode, mUserCode)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance()
            .signInWithCredential(credential).addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    FirebaseAuth.getInstance()
                        .currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                loginViewModel.checkUserExisten(loginViewModel.phoneNumber)
                            }
                        }

                } else {
                    binding.progressBar.visibility = GONE
                    var message = "Something is wrong, we will fix it soon..."
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        message = "Invalid code entered..."
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun checkUserExistenResult(checkUserExists: CheckUserExists) {
        if (checkUserExists.data.userExists) {
            loginViewModel.generateAccessPhoneToken(loginViewModel.phoneNumber)
        } else {
            timer.cancel()
            loginViewModel.performSignUp(1, "")
            findNavController().navigate(VerifyOtpFragmentDirections.toUserFirstLastNameFragment())
        }
    }

    private fun navigateToHomeScreen(authToken: AuthToken) {
        timer.cancel()
        loginViewModel.saveAuthToken(authToken)
        requireActivity().finish()
        startActivity(Intent(requireContext(), SingstrActivity::class.java))
    }

    private fun showAutoVerificationDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_verify_otp, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        val tvDelete = dialogView.findViewById<TextView>(R.id.tvDelete)
        tvDelete.text = "Please wait, We are trying to auto-verify the verification code sent to ${loginViewModel.phoneNumber}"
        alertDialog = mBuilder.show()
        alertDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.setCancelable(false)

        dialogView.findViewById<TextView>(R.id.tv_change_phone_no).setOnClickListener {
            alertDialog.dismiss()
            requireActivity().onBackPressed()
        }

        dialogView.findViewById<TextView>(R.id.tv_enter_manually).setOnClickListener {
            alertDialog.dismiss()
            requireContext().unregisterReceiver(smsRetriever)
        }
    }

    override fun onStart() {
        super.onStart()
//        val intentFilter = IntentFilter().apply {
//            addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
//        }
        registerBroadcastReceiver()
//        requireContext().registerReceiver(smsRetriever, intentFilter)
    }

    override fun onStop() {
        try{
            requireContext().unregisterReceiver(smsRetriever)
        }catch (e: Exception){
            Timber.d("smsRetriever not registered $e")
        }
        super.onStop()
    }

    private fun registerBroadcastReceiver() {
        smsRetriever = SMSBroadcastReceiver()
        smsRetriever.smsBroadcastReceiverListener = object : SMSBroadcastReceiver.SmsBroadcastReceiverListener {
            override fun onSuccess(intent: Intent?) {
                startActivityForResult(intent, SMS_CONSENT_REQUEST)
            }

            override fun onFailure() {
                Timber.e("registerBroadcastReceiver error")
            }
        }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        requireContext().registerReceiver(smsRetriever, intentFilter)

    }

    private fun startSmsUserConsent() {
        val client = SmsRetriever.getClient(requireActivity())
        client.startSmsUserConsent(null).addOnSuccessListener {
            Timber.d("startSmsUserConsent success")
        }.addOnFailureListener {
            Timber.d("startSmsUserConsent failure")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SMS_CONSENT_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                if (this@VerifyOtpFragment::alertDialog.isInitialized)
                    alertDialog.dismiss()
                val msg = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMsg(msg.toString())
            }
        }
    }

    private fun getOtpFromMsg(msg: String) {
        if (msg.contains("singShala")) {
            val otp = msg.subSequence(0, 6).toString()
            binding.otpView.setOTP(otp)
            mUserCode = otp;
            verifyCode()
        } else {
            Toast.makeText(requireContext(), "Could not auto-verify otp, please enter it.", Toast.LENGTH_LONG).show()
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(mTimeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {

                mTimeLeftInMillis = millisUntilFinished
                binding.apply {
                    tvResend.isEnabled = false
                    tvResend.alpha = 0.5f
                    tvTime.text = "%02d:%02d".format(0, millisUntilFinished / 1000)
                }

            }

            override fun onFinish() {
                binding.tvResend.apply {
                    isEnabled = true
                    alpha = 1f
                }
            }
        }
        timer.start()
    }

}