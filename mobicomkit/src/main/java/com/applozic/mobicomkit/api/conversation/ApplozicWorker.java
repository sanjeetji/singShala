package com.applozic.mobicomkit.api.conversation;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.UserService;
import com.applozic.mobicommons.commons.core.utils.DateUtils;
import com.applozic.mobicommons.commons.core.utils.Utils;

/**
 * This worker handles syncing messages and last seen on network change (if available).
 * It also handle updating time zone change data.
 */
@ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS) //ApplozicInternal: default if possible
public class ApplozicWorker extends Worker {
    private static final String TAG = "ApplozicWorker";

    public static final String CONTACT = "contact";
    public static final String CHANNEL = "channel";
    public static final String AL_SYNC_ON_CONNECTIVITY = "AL_SYNC_ON_CONNECTIVITY";
    public static final String AL_TIME_CHANGE_RECEIVER = "AL_TIME_CHANGE_RECEIVER";

    private final MobiComConversationService conversationService;
    private final MessageClientService messageClientService;

    public ApplozicWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.messageClientService = new MessageClientService(context);
        this.conversationService = new MobiComConversationService(context);
    }

    static public void enqueueWork(Context context, boolean connectivityChange, boolean timeChangeReceiver) {
        Data data = new Data.Builder()
                .putBoolean(AL_SYNC_ON_CONNECTIVITY, connectivityChange)
                .putBoolean(AL_TIME_CHANGE_RECEIVER, timeChangeReceiver)
                .build();
        OneTimeWorkRequest conversationWorkerRequest = new OneTimeWorkRequest.Builder(ApplozicMqttWorker.class)
                .setInputData(data)
                .build();
        WorkManager.getInstance(context).enqueue(conversationWorkerRequest);
    }

    static public void enqueueWorkNetworkAvailable(Context context) {
        Utils.printLog(context, TAG, "Enqueue work connectivity changed...");
        enqueueWork(context, true, false);
    }

    static public void enqueueWorkTimeZoneChanged(Context context) {
        Utils.printLog(context, TAG, "Enqueue work time zone changed...");
        enqueueWork(context, false, true);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        boolean connectivityChange = data.getBoolean(AL_SYNC_ON_CONNECTIVITY, false);
        boolean timeChangeReceiver = data.getBoolean(AL_TIME_CHANGE_RECEIVER, false);

        if (connectivityChange) {
            Utils.printLog(getApplicationContext(), TAG, "ConnectivityChange: This worker has been called on a network available connectivity change. Syncing pending messages and updating last seen.");
            SyncCallService.getInstance(getApplicationContext()).syncMessages(null);
            messageClientService.syncPendingMessages(true);
            messageClientService.syncDeleteMessages(true);
            conversationService.processLastSeenAtStatus();
            UserService.getInstance(getApplicationContext()).processSyncUserBlock();
        }

        if (timeChangeReceiver) {
            Utils.printLog(getApplicationContext(), TAG, "TimeChange: This worker has been called on a time zone change.");
            long diff = DateUtils.getTimeDiffFromUtc();
            MobiComUserPreference.getInstance(getApplicationContext()).setDeviceTimeOffset(diff);
        }

        return Result.success();
    }
}

