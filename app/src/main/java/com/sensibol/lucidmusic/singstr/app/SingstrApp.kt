package com.sensibol.lucidmusic.singstr.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.applozic.mobicomkit.uiwidgets.uilistener.ALProfileClickListener
import com.applozic.mobicommons.people.channel.Channel
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.sensibol.android.base.displayName
import com.sensibol.lucidmusic.singstr.BuildConfig
import com.sensibol.lucidmusic.singstr.BuildConfig.WEBENGAGE_LICENSE_KEY
import com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks
import com.webengage.sdk.android.WebEngageConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
internal class SingstrApp : Application(), ALProfileClickListener{

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onCreate() {
        Timber.v("onCreate $displayName")
        super.onCreate()

        val webEngageConfig = WebEngageConfig.Builder()
            .setWebEngageKey(WEBENGAGE_LICENSE_KEY)
            .setDebugMode(BuildConfig.DEBUG || "stage" == BuildConfig.FLAVOR)
            .build()
        registerActivityLifecycleCallbacks(WebEngageActivityLifeCycleCallbacks(this, webEngageConfig))

        Firebase.analytics.setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)

        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS)
        }
    }

    override fun onClick(context: Context?, userId: String?, channel: Channel?, isToolbar: Boolean) {
        Timber.d("messageUserId: $userId")
    }

}