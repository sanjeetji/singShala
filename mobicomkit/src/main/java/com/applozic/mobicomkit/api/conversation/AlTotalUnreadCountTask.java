package com.applozic.mobicomkit.api.conversation;

import android.content.Context;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicommons.ApplozicService;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.task.AlAsyncTask;

import java.lang.ref.WeakReference;

/**
 * Gets the total unread count for the current user.
 * This count is retrieved from the server.
 */
public class AlTotalUnreadCountTask extends AlAsyncTask<Void, Integer> {
    private static final String TAG = "AlTotalUnreadCountTask";

    private final TaskListener callback;
    private final WeakReference<Context> weakReferenceContext;

    MessageDatabaseService messageDatabaseService;

    public AlTotalUnreadCountTask(Context context, TaskListener callback) {
        this.callback = callback;
        this.weakReferenceContext = new WeakReference<Context>(context);
        this.messageDatabaseService = new MessageDatabaseService(context);
    }

    @Override
    protected Integer doInBackground() {
        try {
            //If there was no previous server call done. Essentially, local db will be empty.
            if (!ApplozicClient.getInstance(ApplozicService.getContextFromWeak(weakReferenceContext)).wasServerCallDoneBefore(null, null, null)) {
                if (!Utils.isInternetAvailable(ApplozicService.getContextFromWeak(weakReferenceContext))) {
                    return null;
                }
                SyncCallService.getInstance(ApplozicService.getContextFromWeak(weakReferenceContext)).getLatestMessagesGroupByPeople(null, MobiComUserPreference.getInstance(ApplozicService.getContextFromWeak(weakReferenceContext)).getParentGroupKey());
            }
            return messageDatabaseService.getTotalUnreadCount();
        } catch (Exception e) {
            Utils.printLog(ApplozicService.getContextFromWeak(weakReferenceContext), TAG, e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(Integer unreadCount) {
        super.onPostExecute(unreadCount);
        if (callback != null) {
            if (unreadCount != null) {
                callback.onSuccess(unreadCount);
            } else {
                callback.onFailure("Failed to fetch the total unread count.");
            }
        }
    }

    public interface TaskListener {
        void onSuccess(Integer unreadCount);
        void onFailure(String error);
    }
}
