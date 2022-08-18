package com.applozic.mobicomkit.api.people;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.UserDetail;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MessageClientService;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.SyncCallService;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

import static com.applozic.mobicomkit.api.conversation.ApplozicConversation.isMessageStatusPublished;

/**
 * Worker for processing user related data (including contacts and channels) such as read, syncing user details etc.
 */
@ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS)
public class UserWorker extends Worker {
    private static final String TAG = "UserWorker";

    public static final String USER_ID = "userId";
    public static final String USER_LAST_SEEN_AT_STATUS = "USER_LAST_SEEN_AT_STATUS";
    public static final String CONTACT = "contact";
    public static final String CHANNEL = "channel";
    public static final String UNREAD_COUNT = "UNREAD_COUNT";
    public static final String PAIRED_MESSAGE_KEY_STRING = "PAIRED_MESSAGE_KEY_STRING";

    MessageClientService messageClientService;
    MobiComConversationService mobiComConversationService;
    MessageDatabaseService messageDatabaseService;

    @ApplozicInternal
    public UserWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        messageClientService = new MessageClientService(getApplicationContext());
        messageDatabaseService = new MessageDatabaseService(getApplicationContext());
        mobiComConversationService = new MobiComConversationService(getApplicationContext());
    }

    static public void enqueueWork(Context context, String userId, Contact contact, Channel channel, String pairedMessageKeyString, int unreadCount, boolean updateLastSeenStatus) {
        Data.Builder dataBuilder = new Data.Builder();
        if (!TextUtils.isEmpty(userId)) {
            dataBuilder.putString(USER_ID, userId);
        }
        if (contact != null) {
            dataBuilder.putString(CONTACT, GsonUtils.getJsonFromObject(contact, Contact.class));
        }
        if (channel != null) {
            dataBuilder.putString(CHANNEL, GsonUtils.getJsonFromObject(channel, Channel.class));
        }
        if (!TextUtils.isEmpty(pairedMessageKeyString)) {
            dataBuilder.putString(PAIRED_MESSAGE_KEY_STRING, pairedMessageKeyString);
        }
        dataBuilder.putInt(UNREAD_COUNT, unreadCount);
        dataBuilder.putBoolean(USER_LAST_SEEN_AT_STATUS, updateLastSeenStatus);
        Data data = dataBuilder.build();

        OneTimeWorkRequest messageWorkerRequest = new OneTimeWorkRequest.Builder(UserWorker.class)
                .setInputData(data)
                .build();
        WorkManager.getInstance(context).enqueue(messageWorkerRequest);
    }

    //ApplozicInternal: private
    public void checkAndSaveLoggedUserDeletedDataToSharedPref() {
        String userId = MobiComUserPreference.getInstance(getApplicationContext()).getUserId();
        if(TextUtils.isEmpty(userId)) {
            return;
        }

        UserDetail[] userDetails = new MessageClientService(getApplicationContext()).getUserDetails(userId);
        if(userDetails == null) {
            return;
        }

        for (UserDetail userDetail : userDetails) {
            if (userId.equals(userDetail.getUserId()) && userDetail.getDeletedAtTime() != null) {
                SyncCallService.getInstance(getApplicationContext()).processLoggedUserDelete(); //this will add a user deleted entry to MobicomUserPreferences
            }
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        Utils.printLog(getApplicationContext(), TAG, "Started user worker...");
        Data data = getInputData();
        int unreadCount = data.getInt(UNREAD_COUNT, 0);
        String contactJson = data.getString(CONTACT);
        Contact contact = null;
        if (!TextUtils.isEmpty(contactJson)) {
            contact = (Contact) GsonUtils.getObjectFromJson(contactJson, Contact.class);
        }
        String channelJson = data.getString(CHANNEL);
        Channel channel = null;
        if (!TextUtils.isEmpty(channelJson)) {
            channel = (Channel) GsonUtils.getObjectFromJson(channelJson, Channel.class);
        }
        String messageKey = data.getString(PAIRED_MESSAGE_KEY_STRING);

        if (contact != null) {
            Utils.printLog(getApplicationContext(), TAG, "Updating read status local for contact...");
            messageDatabaseService.updateReadStatusForContact(contact.getContactIds());
        } else if (channel != null) {
            Utils.printLog(getApplicationContext(), TAG, "Updating read status local for channel...");
            messageDatabaseService.updateReadStatusForChannel(String.valueOf(channel.getKey()));
        }

        if (unreadCount != 0 || !TextUtils.isEmpty(messageKey) && !isMessageStatusPublished(getApplicationContext(), messageKey, Message.Status.READ.getValue())) {
            Utils.printLog(getApplicationContext(), TAG, "Updating read status in server...");
            messageClientService.updateReadStatus(contact, channel);
        } else {
            String userId = data.getString(USER_ID);
            if (!TextUtils.isEmpty(userId)) {
                Utils.printLog(getApplicationContext(), TAG, "Syncing user details...");
                SyncCallService.getInstance(getApplicationContext()).processUserStatus(userId);
            } else if (data.getBoolean(USER_LAST_SEEN_AT_STATUS, false)) {
                Utils.printLog(getApplicationContext(), TAG, "Processing last seen at status for all users...");
                mobiComConversationService.processLastSeenAtStatus();
            }
        }
        Utils.printLog(getApplicationContext(), TAG, "Adding logged in user deleted data to shared pref if required...");
        checkAndSaveLoggedUserDeletedDataToSharedPref();
        return Result.success();
    }
}
