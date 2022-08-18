package com.sensibol.lucidmusic.singstr.gui.splash

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.sensibol.android.base.gui.activity.BaseActivity
import com.sensibol.android.base.gui.failure
import com.sensibol.android.base.gui.observe
import com.sensibol.lucidmusic.singstr.gui.*
import com.sensibol.lucidmusic.singstr.gui.app.SingstrActivity
import com.sensibol.lucidmusic.singstr.gui.databinding.ActivitySplashBinding
import com.sensibol.lucidmusic.singstr.gui.databinding.DialogueAppExpiryBinding
import com.sensibol.lucidmusic.singstr.gui.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
internal class SplashActivity : BaseActivity() {

    override val layoutResId = R.layout.activity_splash
    override val bindingInflater: (LayoutInflater) -> ViewBinding = ActivitySplashBinding::inflate
    override val binding: ActivitySplashBinding get() = super.binding as ActivitySplashBinding

    private val splashViewModel: SplashViewModel by viewModels()

    @Inject
    @AppBuildName
    lateinit var versionName: String

    @Inject
    @AppReleaseTimestamp
    lateinit var releaseTimestamp: String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel.apply {
            failure(failure, ::handleFailure)
            observe(isForceUpdateNeeded, ::showForceUpdateDialog)
            observe(isUserLoggedIn, ::moveToNextScreen)

            doApplicationStartup()
        }
        window.statusBarColor = this.let { ContextCompat.getColor(it, R.color.bg_page) }
        binding.apply {
            tvVersionInfo.text = "$versionName\n$releaseTimestamp"
            ivStageRibbon.visibility = if ("stage" == BuildConfig.FLAVOR) View.VISIBLE else View.GONE
        }
    }

    private fun showForceUpdateDialog(unused: Unit) {
        Timber.d("App expired. Force update needed.")

        with(AlertDialog.Builder(this).show()) {
            val binding = DialogueAppExpiryBinding.inflate(layoutInflater)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setContentView(binding.root)
            setCancelable(false)
            binding.apply {
                btnUpdate.setOnClickListener {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                }
                btnCancel.setOnClickListener {
                    dismiss()
                    finish()
                }
            }
        }
    }

    private fun moveToNextScreen(isUserLoggedIn: Boolean) {
        Timber.d("moveToNextScreen: isUserLoggedIn=$isUserLoggedIn")
        startActivity(
            Intent(
                applicationContext,
                if (isUserLoggedIn) {
                    SingstrActivity::class.java
                } else {
                    LoginActivity::class.java
                }
            )
        )
        finish()
    }
}