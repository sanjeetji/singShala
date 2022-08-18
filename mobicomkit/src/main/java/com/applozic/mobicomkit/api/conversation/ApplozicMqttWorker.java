package com.applozic.mobicomkit.api.conversation;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.ApplozicMqttService;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

import java.util.concurrent.TimeUnit;

/**
 * Handles all the real-time MQTT related code. Example: typing, subscribing etc.
 */
@ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS) //ApplozicInternal: default if possible (maybe keep it at the root)
public class ApplozicMqttWorker extends Worker {
    public static final String TAG = "ApplozicMqttWorker";

    public static final String SUBSCRIBE = "subscribe";
    public static final String SUBSCRIBE_TO_TYPING = "subscribeToTyping";
    public static final String UN_SUBSCRIBE_TO_TYPING = "unSubscribeToTyping";
    public static final String DEVICE_KEY_STRING = "deviceKeyString";
    public static final String USER_KEY_STRING = "userKeyString";
    public static final String CONNECTED_PUBLISH = "connectedPublish";
    public static final String CONTACT = "contact";
    public static final String CHANNEL = "channel";
    public static final String TYPING = "typing";
    public static final String STOP_TYPING = "STOP_TYPING";
    public static final String CONNECT_TO_SUPPORT_GROUP_TOPIC = "connectToSupportGroupTopic";
    public static final String DISCONNECT_FROM_SUPPORT_GROUP_TOPIC = "disconnectFromSupportGroupTopic";
    public static final String USE_ENCRYPTED_TOPIC = "useEncryptedTopic";

    public ApplozicMqttWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private static void enqueueWork(Context context, Data data, int minutes) {
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        OneTimeWorkRequest.Builder builder = new OneTimeWorkRequest.Builder(ApplozicMqttWorker.class)
                .setInputData(data)
                .setConstraints(constraints);
        if (minutes > 0 && minutes < 60) {
            builder.setInitialDelay(minutes, TimeUnit.MINUTES);
        }
        OneTimeWorkRequest mqttWorkerRequest = builder.build();
        WorkManager.getInstance(context).enqueue(mqttWorkerRequest);
    }

    public static void enqueueWorkDisconnectPublish(Context context, String deviceKeyString, String userKeyString, boolean useEncrypted) {
        Data.Builder dataBuilder = new Data.Builder();
        if (!TextUtils.isEmpty(userKeyString)) {
            dataBuilder.putString(ApplozicMqttWorker.USER_KEY_STRING, userKeyString);
        }
        if (!TextUtils.isEmpty(deviceKeyString)) {
            dataBuilder.putString(ApplozicMqttWorker.DEVICE_KEY_STRING, deviceKeyString);
        }
        dataBuilder.putBoolean(ApplozicMqttWorker.USE_ENCRYPTED_TOPIC, useEncrypted);

        Utils.printLog(context, TAG, "Enqueue work disconnect publish...");
        enqueueWork(context, dataBuilder.build(), 0);
    }

    public static void enqueueWorkSubscribeAndConnectPublishAfter(Context context, boolean useEncrypted, int minutes) {
        Utils.printLog(context, TAG, "Enqueue work subscribe and connect publish after " + minutes + " minutes...");
        Data.Builder dataBuilder = new Data.Builder();
        dataBuilder.putBoolean(ApplozicMqttWorker.SUBSCRIBE, true);
        dataBuilder.putBoolean(ApplozicMqttWorker.USE_ENCRYPTED_TOPIC, useEncrypted);
        enqueueWork(context, dataBuilder.build(), minutes);
    }

    //Cleanup: can be removed
    public static void enqueueWorkSubscribeToSupportGroup(Context context, boolean useEncrypted) {
        Data.Builder dataBuilder = new Data.Builder();
        dataBuilder.putBoolean(ApplozicMqttWorker.CONNECT_TO_SUPPORT_GROUP_TOPIC, true);
        dataBuilder.putBoolean(ApplozicMqttWorker.USE_ENCRYPTED_TOPIC, useEncrypted);

        Utils.printLog(context, TAG, "Enqueue work subscribe to support group...");
        enqueueWork(context, dataBuilder.build(), 0);
    }

    //Cleanup: can be removed
    public static void enqueueWorkUnSubscribeToSupportGroup(Context context, boolean useEncrypted) {
        Data.Builder dataBuilder = new Data.Builder();
        dataBuilder.putBoolean(ApplozicMqttWorker.DISCONNECT_FROM_SUPPORT_GROUP_TOPIC, true);
        dataBuilder.putBoolean(ApplozicMqttWorker.USE_ENCRYPTED_TOPIC, useEncrypted);

        Utils.printLog(context, TAG, "Enqueue work unsubscribe to support group...");
        enqueueWork(context, dataBuilder.build(), 0);
    }

    public static void enqueueWorkSubscribeToTyping(Context context, Channel channel, Contact contact) {
        Data.Builder dataBuilder = new Data.Builder();
        try {
            if (channel != null) {
                dataBuilder.putString(ApplozicMqttWorker.CHANNEL, GsonUtils.getJsonFromObject(channel, Channel.class));
            } else if (contact != null) {
                dataBuilder.putString(ApplozicMqttWorker.CONTACT, GsonUtils.getJsonFromObject(contact, Contact.class));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        dataBuilder.putBoolean(ApplozicMqttWorker.SUBSCRIBE_TO_TYPING, true);

        Utils.printLog(context, TAG, "Enqueue work subscribe to typing...");
        enqueueWork(context, dataBuilder.build(), 0);
    }

    public static void enqueueWorkUnSubscribeToTyping(Context context, Channel channel, Contact contact) {
        Data.Builder dataBuilder = new Data.Builder();
        try {
            if (channel != null) {
                dataBuilder.putString(ApplozicMqttWorker.CHANNEL, GsonUtils.getJsonFromObject(channel, Channel.class));
            } else if (contact != null) {
                dataBuilder.putString(ApplozicMqttWorker.CONTACT, GsonUtils.getJsonFromObject(contact, Contact.class));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        dataBuilder.putBoolean(ApplozicMqttWorker.UN_SUBSCRIBE_TO_TYPING, true);

        Utils.printLog(context, TAG, "Enqueue work unsubscribe to support group...");
        enqueueWork(context, dataBuilder.build(), 0);
    }

    public static void enqueueWorkPublishTypingStatus(Context context, Channel channel, Contact contact, boolean typingStarted) {
        Data.Builder dataBuilder = new Data.Builder();
        try {
            if (channel != null) {
                dataBuilder.putString(ApplozicMqttWorker.CHANNEL, GsonUtils.getJsonFromObject(channel, Channel.class));
            } else if (contact != null) {
                dataBuilder.putString(ApplozicMqttWorker.CONTACT, GsonUtils.getJsonFromObject(contact, Contact.class));
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        dataBuilder.putBoolean(ApplozicMqttWorker.TYPING, typingStarted);

        Utils.printLog(context, TAG, "Enqueue work publish typing status...");
        enqueueWork(context, dataBuilder.build(), 0);
    }

    public static void enqueueWorkConnectPublish(Context context) {
        Data.Builder dataBuilder = new Data.Builder();
        dataBuilder.putBoolean(ApplozicMqttWorker.CONNECTED_PUBLISH, true);

        Utils.printLog(context, TAG, "Enqueue work connect publish...");
        enqueueWork(context, dataBuilder.build(), 0);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        boolean useEncryptedTopic = data.getBoolean(USE_ENCRYPTED_TOPIC, false);
        boolean subscribe = data.getBoolean(SUBSCRIBE, false);
        Contact contact = null;
        Channel channel = null;
        try {
            contact = (Contact) GsonUtils.getObjectFromJson(data.getString(CONTACT), Contact.class);
            channel = (Channel) GsonUtils.getObjectFromJson(data.getString(CHANNEL), Channel.class);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        boolean subscribeToTyping = data.getBoolean(SUBSCRIBE_TO_TYPING, false);
        boolean unSubscribeToTyping = data.getBoolean(UN_SUBSCRIBE_TO_TYPING, false);
        boolean subscribeToSupportGroupTopic = data.getBoolean(CONNECT_TO_SUPPORT_GROUP_TOPIC, false);
        boolean unSubscribeToSupportGroupTopic = data.getBoolean(DISCONNECT_FROM_SUPPORT_GROUP_TOPIC, false);
        String userKeyString = data.getString(USER_KEY_STRING);
        String deviceKeyString = data.getString(DEVICE_KEY_STRING);
        boolean connectedStatus = data.getBoolean(CONNECTED_PUBLISH, false);
        boolean stop = data.getBoolean(STOP_TYPING, false);
        boolean typing = data.getBoolean(TYPING, false);

        if (subscribe) {
            if (isAppInBackground()) {
                Log.d(TAG, "App is in background, MQTT method call not required...");
                return Result.success();
            }
            ApplozicMqttService applozicMqttService = ApplozicMqttService.getInstance(getApplicationContext());
            applozicMqttService.connectClient(true);
            applozicMqttService.subscribe(useEncryptedTopic);
            applozicMqttService.publishClientStatus(MobiComUserPreference.getInstance(getApplicationContext()).getSuUserKeyString(), MobiComUserPreference.getInstance(getApplicationContext()).getDeviceKeyString(), "1");
        }

        if (subscribeToTyping) {
            ApplozicMqttService.getInstance(getApplicationContext()).subscribeToTypingTopic(channel);
            if (channel != null && Channel.GroupType.OPEN.getValue().equals(channel.getType())) {
                ApplozicMqttService.getInstance(getApplicationContext()).subscribeToOpenGroupTopic(channel);
            }
            return Result.success();
        }

        if (unSubscribeToTyping) {
            ApplozicMqttService.getInstance(getApplicationContext()).unSubscribeToTypingTopic(channel);
            if (channel != null && Channel.GroupType.OPEN.getValue().equals(channel.getType())) {
                ApplozicMqttService.getInstance(getApplicationContext()).unSubscribeToOpenGroupTopic(channel);
            }
            return Result.success();
        }

        if (subscribeToSupportGroupTopic) {
            ApplozicMqttService.getInstance(getApplicationContext()).subscribeToSupportGroup(useEncryptedTopic);
            return Result.success();
        }

        if (unSubscribeToSupportGroupTopic) {
            ApplozicMqttService.getInstance(getApplicationContext()).unSubscribeToSupportGroup(useEncryptedTopic);
            return Result.success();
        }

        if (!TextUtils.isEmpty(userKeyString) && !TextUtils.isEmpty(deviceKeyString)) {
            ApplozicMqttService.getInstance(getApplicationContext()).publishOfflineStatusUnsubscribeAndDisconnect(userKeyString, deviceKeyString, useEncryptedTopic);
        }

        if (connectedStatus) {
            ApplozicMqttService applozicMqttService = ApplozicMqttService.getInstance(getApplicationContext());
            applozicMqttService.connectClient(true);
            applozicMqttService.publishClientStatus(MobiComUserPreference.getInstance(getApplicationContext()).getSuUserKeyString(), MobiComUserPreference.getInstance(getApplicationContext()).getDeviceKeyString(), "1");
        }

        if (contact != null && stop) {
            ApplozicMqttService.getInstance(getApplicationContext()).typingStopped(contact, null);
        }

        if (contact != null && (contact.isBlocked() || contact.isBlockedBy())) {
            return Result.success();
        }

        if (contact != null || channel != null) {
            if (typing) {
                ApplozicMqttService.getInstance(getApplicationContext()).typingStarted(contact, channel);
            } else {
                ApplozicMqttService.getInstance(getApplicationContext()).typingStopped(contact, channel);
            }
        }

        return Result.success();
    }

    //this method will not work perfectly
    //however for now there is no other suitable method to check if app is in background without using the lifecycle library
    private boolean isAppInBackground() {
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        return myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
    }
}