package com.applozic.mobicomkit.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.ApplozicWorker;
import com.applozic.mobicommons.commons.core.utils.DateUtils;
import com.applozic.mobicommons.commons.core.utils.Utils;

@ApplozicInternal
public class TimeChangeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        if ("android.intent.action.TIME_SET".equals(intent.getAction()) || "android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction())) {
            if (!Utils.isAutomaticTimeEnabled(context, "android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction()))) {
                if (Utils.isDeviceInIdleState(context)) {
                    Thread timeChangeThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.printLog(context, "TimeChange", "This thread has been called on date change");
                            long diff = DateUtils.getTimeDiffFromUtc();
                            MobiComUserPreference.getInstance(context).setDeviceTimeOffset(diff);
                        }
                    });
                    timeChangeThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    timeChangeThread.start();
                } else {
                    ApplozicWorker.enqueueWorkTimeZoneChanged(context);
                }
            }
        }
    }
}
