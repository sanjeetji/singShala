package com.applozic.mobicomkit.uiwidgets.async;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.task.AlAsyncTask;

/**
 * Created by sunil on 15/9/16.
 */
public class ApplozicChannelDeleteTask extends AlAsyncTask<Void, Boolean> {


    private final TaskListener taskListener;
    private final Context context;
    private String response;
    private Exception mException;
    private Channel channel;

    public ApplozicChannelDeleteTask(Context context, TaskListener listener, Channel channel) {
        this.context = context;
        this.channel = channel;
        this.taskListener = listener;
    }

    @Override
    protected Boolean doInBackground() {
        try {
            response = ChannelService.getInstance(context).processChannelDeleteConversation(channel, context);
            return !TextUtils.isEmpty(response) && MobiComKitConstants.SUCCESS.equals(response);
        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (result && this.taskListener != null) {
            this.taskListener.onSuccess(response);
        } else if (!result && this.taskListener != null) {
            this.taskListener.onFailure(response, mException);
        }
        this.taskListener.onCompletion();
    }

    public interface TaskListener {

        void onSuccess(String response);

        void onFailure(String response, Exception exception);

        void onCompletion();
    }

}
