package com.applozic.mobicomkit.api.account.user;

import android.content.Context;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.feed.RegisteredUsersApiResponse;
import com.applozic.mobicommons.task.AlAsyncTask;

/**
 * Will get a list of users from the server for the given Applozic application.
 *
 * <p>Based on the flag {@link RegisteredUsersAsyncTask#callForRegistered}, it will either:
 * - Fetch registered users from a particular time.
 * - Fetch currently online users.</p>
 *
 * @see RegisteredUsersAsyncTask constructors for parameter details.
 */
public class RegisteredUsersAsyncTask extends AlAsyncTask<Void, Boolean> {

    private final TaskListener taskListener;
    Context context;
    int numberOfUsersToFetch;
    UserService userService;
    long lastTimeFetched;
    String[] userIdArray;
    RegisteredUsersApiResponse registeredUsersApiResponse;
    boolean callForRegistered;
    private Exception mException;
    private Message message;
    private String messageContent;

    /**
     * @deprecated Use {@link RegisteredUsersAsyncTask#RegisteredUsersAsyncTask(Context, TaskListener, int, long, Message, String, boolean)}.
     */
    @Deprecated
    public RegisteredUsersAsyncTask(Context context, TaskListener listener, int numberOfUsersToFetch, Message message, String messageContent) {
        this.message = message;
        this.context = context;
        this.taskListener = listener;
        this.messageContent = messageContent;
        this.numberOfUsersToFetch = numberOfUsersToFetch;
        this.userService = UserService.getInstance(context);
    }

    /**
     * @param context the context
     * @param listener for callback
     * @param numberOfUsersToFetch the number of users to fetch from the backend
     * @param lastTimeFetched last time fetch. pass a time if you want all registered users
     * @param message pass NULL
     * @param messageContent pass NULL
     * @param callForRegistered pass true if you want registered users, false if you want online users
     */
    public RegisteredUsersAsyncTask(Context context, TaskListener listener, int numberOfUsersToFetch, long lastTimeFetched, Message message, String messageContent, boolean callForRegistered) {
        this.callForRegistered = callForRegistered;
        this.message = message;
        this.taskListener = listener;
        this.context = context;
        this.messageContent = messageContent;
        this.numberOfUsersToFetch = numberOfUsersToFetch;
        this.lastTimeFetched = lastTimeFetched;
        this.userService = UserService.getInstance(context);
    }

    @Override
    protected Boolean doInBackground() {
        try {
            if (callForRegistered) {
                registeredUsersApiResponse = userService.getRegisteredUsersList(lastTimeFetched, numberOfUsersToFetch);
            } else {
                userIdArray = userService.getOnlineUsers(numberOfUsersToFetch);
            }
            return registeredUsersApiResponse != null || userIdArray != null;
        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (result && this.taskListener != null) {
            this.taskListener.onSuccess(registeredUsersApiResponse, userIdArray);
        } else if (!result && this.taskListener != null) {
            this.taskListener.onFailure(registeredUsersApiResponse, userIdArray, mException);
        }
        this.taskListener.onCompletion();
    }

    public interface TaskListener {

        void onSuccess(RegisteredUsersApiResponse registeredUsersApiResponse, String[] userIdArray);

        void onFailure(RegisteredUsersApiResponse registeredUsersApiResponse, String[] userIdArray, Exception exception);

        void onCompletion();
    }


}
