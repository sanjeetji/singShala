package com.applozic.mobicomkit.api.notification;

import android.content.Context;

import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicommons.task.AlAsyncTask;

/**
 * Mutes notification for a user or channel for a specified amount of time.
 * Asynchronous wrapper over {@link ChannelService#muteNotifications(MuteNotificationRequest)}.
 */
public class MuteNotificationAsync extends AlAsyncTask<Void, Boolean> {

    private final MuteNotificationAsync.TaskListener taskListener;
    private final Context context;
    private ApiResponse apiResponse;
    private Exception mException;
    private MuteNotificationRequest muteNotificationRequest;

    public MuteNotificationAsync(Context context, MuteNotificationAsync.TaskListener listener, MuteNotificationRequest request) {
        this.context = context;
        this.taskListener = listener;
        this.muteNotificationRequest = request;
    }

    @Override
    protected Boolean doInBackground() {
        try {
            apiResponse = ChannelService.getInstance(context).muteNotifications(muteNotificationRequest);
        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (result && this.taskListener != null) {
            this.taskListener.onSuccess(apiResponse);
        } else if (mException != null && this.taskListener != null) {
            this.taskListener.onFailure(apiResponse, mException);
        }
        this.taskListener.onCompletion();
    }

    public interface TaskListener {

        void onSuccess(ApiResponse apiResponse);

        void onFailure(ApiResponse apiResponse, Exception exception);

        void onCompletion();
    }


}
