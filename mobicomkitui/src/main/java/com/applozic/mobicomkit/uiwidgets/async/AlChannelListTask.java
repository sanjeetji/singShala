package com.applozic.mobicomkit.uiwidgets.async;

import android.content.Context;

import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.task.AlAsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

public class AlChannelListTask extends AlAsyncTask<Void, List<Channel>> {
    private WeakReference<Context> context;
    private TaskListener callback;

    public AlChannelListTask(Context context, TaskListener callback) {
        this.context = new WeakReference<>(context);
        this.callback = callback;
    }

    @Override
    protected List<Channel> doInBackground() {
        return ChannelService.getInstance(context.get()).getAllChannelList();
    }

    @Override
    protected void onPostExecute(List<Channel> channelList) {
        super.onPostExecute(channelList);
        if (callback != null) {
            if (channelList == null) {
                callback.onFailure("Failed to fetch the channel list");
            } else {
                callback.onSuccess(channelList);
            }
        }
    }

    public interface TaskListener {
        void onSuccess(List<Channel> channelList);

        void onFailure(String error);
    }
}
