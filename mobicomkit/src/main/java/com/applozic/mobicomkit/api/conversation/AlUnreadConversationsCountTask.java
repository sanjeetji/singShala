package com.applozic.mobicomkit.api.conversation;

import android.content.Context;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicommons.ApplozicService;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.task.AlAsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Gets the number of unread conversations for the current user.
 * This count is retrieved from the server.
 */
public class AlUnreadConversationsCountTask extends AlAsyncTask<Void, Integer> {
    private static final String TAG = "AlUnreadConversationsCountTask";

    private final AlUnreadConversationsCountTask.TaskListener callback;
    private final WeakReference<Context> weakReferenceContext;

    MessageDatabaseService messageDatabaseService;

    public AlUnreadConversationsCountTask(Context context, AlUnreadConversationsCountTask.TaskListener callback) {
        this.callback = callback;
        this.weakReferenceContext = new WeakReference<>(context);
        this.messageDatabaseService = new MessageDatabaseService(context);
    }

    @Override
    protected Integer doInBackground() {
        int unreadConversationCount = 0;
        try {
            List<Message> messageList = SyncCallService.getInstance(ApplozicService.getContextFromWeak(weakReferenceContext)).getLatestMessagesGroupByPeople(null, MobiComUserPreference.getInstance(ApplozicService.getContextFromWeak(weakReferenceContext)).getParentGroupKey());
            for (Message message : messageList) {
                int unreadCount;
                if (message.getGroupId() != null && message.getGroupId() != 0) {
                    unreadCount = messageDatabaseService.getUnreadMessageCountForChannel(message.getGroupId());
                } else {
                    unreadCount = messageDatabaseService.getUnreadMessageCountForContact(message.getTo());
                }

                if (unreadCount > 0) {
                    unreadConversationCount++;
                }
            }
        } catch (Exception exception) {
            Utils.printLog(ApplozicService.getContextFromWeak(weakReferenceContext), TAG, exception.getMessage());
            return null;
        }
        return unreadConversationCount;
    }

    @Override
    protected void onPostExecute(Integer unreadConversationCount) {
        super.onPostExecute(unreadConversationCount);
        if (callback != null) {
            if (unreadConversationCount != null) {
                callback.onSuccess(unreadConversationCount);
            } else {
                callback.onFailure("Failed to fetch the unread conversations count.");
            }
        }
    }

    public interface TaskListener {
        void onSuccess(Integer unreadConversationCount);
        void onFailure(String error);
    }
}
