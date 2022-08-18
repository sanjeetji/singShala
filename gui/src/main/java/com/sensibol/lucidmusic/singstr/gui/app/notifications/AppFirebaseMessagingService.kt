package com.sensibol.lucidmusic.singstr.gui.app.notifications

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.applozic.mobicomkit.Applozic
import com.applozic.mobicomkit.api.account.register.RegisterUserClientService
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sensibol.lucidmusic.singstr.gui.app.SingstrActivity.Companion.fcm_token_service
import com.webengage.sdk.android.WebEngage
import timber.log.Timber

internal class AppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        WebEngage.get().setRegistrationID(token)
        Timber.d("onNewToken: FirebaseMessagingToken $token")
        sendToken(token)
        if (MobiComUserPreference.getInstance(this).isRegistered) {
            try {
                RegisterUserClientService(this).updatePushNotificationId(token)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val data: Map<String, String> = remoteMessage.data
        // Applozic message handling you can copy paste this in your file
        if (Applozic.isApplozicNotification(this, remoteMessage.data)) {
            Timber.d("Applozic notification processed")
        } else if (data.containsKey("source") && "webengage" == data["source"]) {
            WebEngage.get().receive(data)
        }
    }

    private fun sendToken(token: String) {
        val intent = Intent(fcm_token_service)
        intent.putExtra("new_fcm_token", token)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }
}