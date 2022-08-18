package com.applozic.mobicomkit.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.ApplozicWorker;
import com.applozic.mobicommons.commons.core.utils.Utils;

@ApplozicInternal
public class ConnectivityReceiver extends BroadcastReceiver {
    private static final String TAG = "ConnectivityReceiver";

    private static final String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    private static final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String REBOOT_COMPLETED = "android.intent.action.QUICKBOOT_POWERON";

    private static boolean firstConnect = true;

    @Override
    public void onReceive(@NonNull final Context context, @NonNull Intent intent) {
        String action = intent.getAction();
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(action));
        Utils.printLog(context, TAG, action);

        if (!MobiComUserPreference.getInstance(context).isLoggedIn()) {
            return;
        }

        if (BOOT_COMPLETED.equalsIgnoreCase(action) || REBOOT_COMPLETED.equalsIgnoreCase(action)) {
            ApplozicWorker.enqueueWorkNetworkAvailable(context);
        }

        if (CONNECTIVITY_CHANGE.equalsIgnoreCase(action)) {
            if (!Utils.isInternetAvailable(context)) {
                firstConnect = true;
                return;
            }
            ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
            if (cm != null) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    if (firstConnect) {
                        firstConnect = false;
                        ApplozicWorker.enqueueWorkNetworkAvailable(context);
                    }
                }
            }
        }
    }
}


