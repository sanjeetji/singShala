package com.applozic.mobicomkit.api.conversation;

import android.content.Context;
import android.os.Handler;
import android.os.Process;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.conversation.schedule.ScheduleMessageService;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.json.GsonUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Worker to send a message to the server (also creates in db) and also sync pending messages.
 */
@ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS)
public class MessageWorker extends Worker {
    private static final String TAG = "MessageWorker";

    private final MessageClientService messageClientService;
    private final CountDownLatch countDownLatch;
    private static final Map<Long, Handler> uploadQueueMap = new HashMap<>();

    public MessageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        messageClientService = new MessageClientService(context);
        countDownLatch = new CountDownLatch(1);
    }

    static public void enqueueWork(Context context, Message message, String displayName, Handler handler) {
        Data.Builder dataBuilder = new Data.Builder();
        if (message != null) {
            dataBuilder.putString(MobiComKitConstants.MESSAGE_JSON_INTENT, GsonUtils.getJsonFromObject(message, Message.class));
            if (handler != null) {
                uploadQueueMap.put(message.getCreatedAtTime(), handler);
            }
        }
        if (!TextUtils.isEmpty(displayName)) {
            dataBuilder.putString(MobiComKitConstants.DISPLAY_NAME, displayName);
        }
        Data data = dataBuilder.build();
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest messageWorkerRequest = new OneTimeWorkRequest.Builder(MessageWorker.class)
                .setInputData(data).setConstraints(constraints)
                .build();
        WorkManager.getInstance(context).enqueue(messageWorkerRequest);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Data data = getInputData();
            final Message message = (Message) GsonUtils.getObjectFromJson(data.getString(MobiComKitConstants.MESSAGE_JSON_INTENT), Message.class);
            final String displayName = data.getString(MobiComKitConstants.DISPLAY_NAME);

            if (message == null) {
                return Result.failure();
            }

            Utils.printLog(getApplicationContext(), TAG, "Sending message thread started...");
            MessageSender messageSender = new MessageSender(message, uploadQueueMap.get(message.getCreatedAtTime()), displayName, countDownLatch, messageClientService);
            Thread thread = new Thread(messageSender);
            thread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();

            countDownLatch.await();
        } catch (Exception exception) {
            Utils.printLog(getApplicationContext(), TAG, "Sending message failure...");
            exception.printStackTrace();
            return Result.failure();
        }
        Utils.printLog(getApplicationContext(), TAG, "Sending message thread finished, possible success...");
        return Result.success();
    }

    private static class MessageSender implements Runnable {
        private final Message message;
        private final Handler handler;
        private final String userDisplayName;
        private final CountDownLatch countDownLatch;
        private final MessageClientService messageClientService;

        public MessageSender(Message message, Handler handler, String userDisplayName, CountDownLatch countDownLatch, MessageClientService messageClientService) {
            this.message = message;
            this.handler = handler;
            this.userDisplayName = userDisplayName;
            this.countDownLatch = countDownLatch;
            this.messageClientService = messageClientService;
        }

        @Override
        public void run() {
            try {
                messageClientService.sendMessageToServer(message, handler, ScheduleMessageService.class, userDisplayName);
                messageClientService.syncPendingMessages(true);
                uploadQueueMap.remove(message.getCreatedAtTime());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                countDownLatch.countDown();
            }
        }
    }
}
