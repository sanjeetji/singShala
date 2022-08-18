package com.sensibol.lucidmusic.singstr.gui.app.profile.self.settings

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.applozic.mobicomkit.Applozic
import com.applozic.mobicomkit.listners.AlLogoutHandler
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.tasks.Task
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.fragment.BaseFragment
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.domain.model.ProSubscription
import com.sensibol.lucidmusic.singstr.domain.model.SystemProperties
import com.sensibol.lucidmusic.singstr.gui.*
import com.sensibol.lucidmusic.singstr.gui.app.SingstrViewModel
import com.sensibol.lucidmusic.singstr.gui.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class SettingsFragment : BaseFragment() {
    override val layoutResId: Int = R.layout.fragment_settings
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding = FragmentSettingsBinding::inflate
    override val binding get() : FragmentSettingsBinding = super.binding as FragmentSettingsBinding

    private lateinit var reviewManager: ReviewManager
    private lateinit var reviewInfo: ReviewInfo

    private val singstrViewModel: SingstrViewModel by activityViewModels()

    private val viewModel: SettingsViewModel by viewModels()

    @Inject
    @AppBuildName
    lateinit var versionName: String

    var modifiedFileSize: String = ""

    private lateinit var reportMessage: EditText

    override fun onInitView() {
        getReviewInfo()
        viewModel.apply {
            failure(failure, ::handleFailure)
            observe(cacheSizeBytes, ::showCacheSize)
            observe(deleteUserProfile, ::handleDeletedUser)
            getCacheSizeBytes()
        }
        
        binding.apply {
            singstrViewModel.apply {
                failure(failure, ::handleFailure)
                observe(userSubscription, ::showProSubscription)
                getUserSubscription()
            }
            clAbout.setOnClickListener() {
                val urlLink = "https://singshala.com/about-us/"
                findNavController().navigate(SettingsFragmentDirections.toWebViewFragment(urlLink))
            }
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }
            clNotification.setOnClickListener {
                findNavController().navigate(SettingsFragmentDirections.toNotificationSettingFragment())
            }

            clAppreview.setOnClickListener {
                val flow: Task<Void> = reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener { Toast.makeText(requireContext(), "InApp Rating complete", Toast.LENGTH_SHORT).show() }
            }
            clSupport.setOnClickListener {
                with(Intent(Intent.ACTION_SENDTO)) {
                    type = "text/plain"
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(SystemProperties.SUPPORT_EMAIL))
                    startActivity(this)
                }
            }
            clReachWhatsapp.setOnClickListener {
                with(Intent(Intent.ACTION_VIEW)) {
                    data = Uri.parse("https://wa.me/${SystemProperties.SUPPORT_WHATSAPP}")
                    startActivity(this)
                }
            }
            clLogout.setOnClickListener {
                messagingLogout()
            }
            tvAppVersion.text = versionName

            clClearCache.setOnClickListener {
                viewModel.deleteCache()
            }

            clReportABug.setOnClickListener {
                showReportABugDialog()
            }

//            clDeleteAccount.visibility = GONE
            clDeleteAccount.setOnClickListener {
                showCustomDialog()
            }

        }
    }

    private fun handleDeletedUser(message: String) {
        LoginManager.getInstance().logOut()
        Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
        messagingLogout()
    }

    private fun getReviewInfo() {
        reviewManager = ReviewManagerFactory.create(requireContext())
        val manager: Task<ReviewInfo> = reviewManager.requestReviewFlow()
        manager.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                reviewInfo = task.result
            } else {
                Toast.makeText(requireContext(), "InApp ReviewFlow failed to start", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProSubscription(proSubscription: ProSubscription) {
        if (proSubscription.subscribed) {
            binding.tvProMembershipChild.text = "Renewal Date: ${convertDatePatternDetailAnalysis(proSubscription.validity)}"
        } else {
            binding.tvProMembershipChild.visibility = GONE
        }
        binding.clProMembership.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.toProSubscriptionPlanFragment())
        }
    }

    private fun showReportABugDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialogue_report_bug, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        val alertDialog = mBuilder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogView.findViewById<TextView>(R.id.etReasonReport).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dialogView.findViewById<TextView>(R.id.btnReportBug).isEnabled = s?.length != 0
                if (dialogView.findViewById<TextView>(R.id.btnReportBug).isEnabled) {
                    dialogView.findViewById<TextView>(R.id.btnReportBug).text = "Submit Report"
                } else {
                    dialogView.findViewById<TextView>(R.id.btnReportBug).text = "Report"
                }
            }
        })
        reportMessage = dialogView.findViewById(R.id.etReasonReport)
        dialogView.findViewById<TextView>(R.id.btnReportBug).setOnClickListener {
            viewModel.reportBug(reportMessage.text.toString())
            alertDialog.dismiss()
        }
    }

    private fun showCacheSize(cacheSize: Long) {
        Timber.d("Cache size: $cacheSize")
        cacheSize.toFloat()
        modifiedFileSize = if (cacheSize < 1024) {
            cacheSize.toString() + "B"
        } else if (cacheSize > 1024 && cacheSize < (1024 * 1024)) {
            ((cacheSize / 1024 * 100.0).roundToInt() / 100.0).toString() + "KB"
        } else {
            ((cacheSize / (1024 * 1024) * 100.0).roundToInt() / 100.0).toString() + "MB"
        }
        binding.tvClearCacheChild.text = "Cache size $modifiedFileSize"
    }

    private fun cacheCleared(cacheCleared: Long) {
        cacheCleared.toFloat()
        binding.apply {
            val modifiedCacheClearedSize = if (cacheCleared < 1024) {
                cacheCleared.toString() + "B"
            } else if (cacheCleared > 1024 && cacheCleared < (1024 * 1024)) {
                ((cacheCleared / 1024 * 100.0).roundToInt() / 100.0).toString() + "KB"
            } else {
                ((cacheCleared / (1024 * 1024) * 100.0).roundToInt() / 100.0).toString() + "MB"
            }
            binding.tvClearCacheChild.text = "Cache size $modifiedCacheClearedSize"
        }
    }

    private fun messagingLogout(){
        Applozic.logoutUser(requireContext(), object : AlLogoutHandler {
            override fun onSuccess(context: Context?) {
                LoginManager.getInstance().logOut()
                appLogout()
            }

            override fun onFailure(exception: Exception?) {
                // Logout failed
            }
        })
    }

    private fun appLogout(){
        singstrViewModel.apply {
            //TODO: improvement needed
            lifecycleScope.launch(Dispatchers.IO) {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(SystemProperties.GSO_REQ_ID_TOKEN)
                    .requestEmail()
                    .build()

                val client = GoogleApiClient.Builder(requireActivity())
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build()
                val connect = client.blockingConnect()

                if (connect.isSuccess && client.isConnected) {
                    client.clearDefaultAccountAndReconnect().await()
                }
                context?.let { GoogleSignIn.getClient(it, gso) }?.signOut()
                withContext(Dispatchers.Main) {
                    observe(isLogoutSuccessful) { triggerAppRestart(requireContext()) }
                    logout()
                }
            }
        }

    }

    private fun showCustomDialog() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_delete_your_account, null)
        val mBuilder = AlertDialog.Builder(activity).setView(dialogView)
        val deleteCover = dialogView.findViewById<TextView>(R.id.tvDelete)
        val keepCover = dialogView.findViewById<TextView>(R.id.btnNo)
        val alertDialog = mBuilder.show()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteCover.setOnClickListener {
            viewModel.loadDeleteUser()
            alertDialog.dismiss()
        }
        keepCover.setOnClickListener {
            alertDialog.dismiss()
        }
    }



}