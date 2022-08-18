package com.applozic.mobicomkit.api.conversation;

import android.content.Context;
import android.os.Process;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.account.user.UserService;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.json.GsonUtils;

import java.util.concurrent.CountDownLatch;

/**
 * Message, Contact and Channel syncing.
 */
@ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS) //ApplozicInternal: default if possible
public class ConversationWorker extends Worker {
    private static final String TAG = "ConversationWorker";

    public static final String SYNC = "AL_SYNC";
    public static final String AL_MESSAGE = "AL_MESSAGE";
    public static final String MESSAGE_METADATA_UPDATE = "MessageMetadataUpdate";
    public static final String MUTED_USER_LIST_SYNC = "MutedUserListSync";

    private final MobiComMessageService mobiComMessageService;
    private final CountDownLatch countDownLatch;

    public ConversationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mobiComMessageService = new MobiComMessageService(context, MessageWorker.class);
        countDownLatch = new CountDownLatch(1);
    }

    static public void enqueueWork(Context context, Message message, boolean sync, boolean messageMetadataUpdate, boolean mutedUserListSync) {
        Data.Builder dataBuilder = new Data.Builder()
                .putBoolean(SYNC, sync)
                .putBoolean(MESSAGE_METADATA_UPDATE, messageMetadataUpdate)
                .putBoolean(MUTED_USER_LIST_SYNC, mutedUserListSync);
        if(message != null) {
            dataBuilder.putString(AL_MESSAGE, GsonUtils.getJsonFromObject(message, Message.class));
        }
        Data data = dataBuilder.build();
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest conversationWorkerRequest = new OneTimeWorkRequest.Builder(ConversationWorker.class)
                .setInputData(data).setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueue(conversationWorkerRequest);
    }

    /**
     * Will sync messages and other data locally. See {@link MobiComMessageService#syncMessages()}.
     *
     * @param context the context
     */
    static public void enqueueWorkSync(Context context) {
        enqueueWork(context, null, true, false, false);
    }

    /**
     * Will sync metadata of messages from last sync time.
     *
     * @param context the context
     */
    static public void enqueueWorkMessageMetadataUpdate(Context context) {
        enqueueWork(context, null, false, true, false);
    }

    /**
     * Will sync list of muted users from last sync time.
     *
     * @param context the context
     */
    static public void enqueueWorkMutedUserListSync(Context context) {
        enqueueWork(context, null, false, false, true);
    }

    /**
     * Will sync metadata message contact and channel and send message broadcast for individual message.
     *
     * @param context the context
     */
    static public void enqueueWorkInstantMessage(Context context, Message message) {
        enqueueWork(context, message, false, false, false);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        boolean sync = data.getBoolean(SYNC, false);
        boolean metadataSync = data.getBoolean(MESSAGE_METADATA_UPDATE, false);
        boolean mutedUserListSync = data.getBoolean(MUTED_USER_LIST_SYNC, false);
        Message message = null;
        try {
            message = (Message) GsonUtils.getObjectFromJson(data.getString(AL_MESSAGE), Message.class);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        Utils.printLog(getApplicationContext(), TAG, "Syncing messages work started with sync = " + sync + ".");

        if (mutedUserListSync) {
            Utils.printLog(getApplicationContext(), TAG, "Muted user list sync started...");
            try {
                Thread thread = new Thread(new MutedUserListSync(countDownLatch));
                thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
                thread.start();
                countDownLatch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Result.success();
        }

        if (metadataSync) {
            Utils.printLog(getApplicationContext(), TAG, "Syncing messages service started for metadata update...");
            mobiComMessageService.syncMessageForMetadataUpdate();
            return Result.success();
        }

        if (message != null) {
            Utils.printLog(getApplicationContext(), TAG, "Syncing messages data for individual message...");
            mobiComMessageService.syncMessageDataAndSendBroadcastFor(message);
            return Result.success();
        }

        if (sync) {
            Utils.printLog(getApplicationContext(), TAG, "Syncing message, channel, contact and other data...");
            mobiComMessageService.syncMessages();
            return Result.success();
        }

        return Result.success();
    }

    private class MutedUserListSync implements Runnable {
        CountDownLatch countDownLatch;

        public MutedUserListSync(CountDownLatch countDownLatch) {
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            try {
                UserService.getInstance(getApplicationContext()).processSyncUserBlock();
                UserService.getInstance(getApplicationContext()).getMutedUserList();

                countDownLatch.countDown();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}

