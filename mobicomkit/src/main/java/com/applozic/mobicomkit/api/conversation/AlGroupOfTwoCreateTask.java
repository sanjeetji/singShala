package com.applozic.mobicomkit.api.conversation;

import android.content.Context;

import androidx.annotation.NonNull;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.people.ChannelInfo;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;
import com.applozic.mobicommons.task.AlAsyncTask;

import java.util.ArrayList;
import java.util.List;

@ApplozicInternal
public class AlGroupOfTwoCreateTask extends AlAsyncTask<Integer, Channel> {
    private final ChannelService channelService;
    private final String loggedInUserId;
    private final Contact withUserContact;
    private final Integer localParentGroupKey;
    private final ChannelCreateListener channelCreateListener;

    public AlGroupOfTwoCreateTask(Context context, Integer parentGroupKey, Contact withUserContact, ChannelCreateListener channelCreateListener) {
        this.channelService = ChannelService.getInstance(context);
        this.withUserContact = withUserContact;
        this.localParentGroupKey = parentGroupKey;
        this.loggedInUserId = MobiComUserPreference.getInstance(context).getUserId();
        this.channelCreateListener = channelCreateListener;
    }

    @Override
    protected Channel doInBackground() {
        Channel channel = null;
        if (localParentGroupKey != null && localParentGroupKey != 0 && withUserContact != null) {
            List<String> userIdList = new ArrayList<>();
            userIdList.add(withUserContact.getContactIds());
            int result = loggedInUserId.compareTo(withUserContact.getContactIds());
            StringBuilder stringBuffer = new StringBuilder();
            if (result == 0) {
                stringBuffer.append(localParentGroupKey).append(":").append(loggedInUserId).append(":").append(withUserContact.getContactIds());
            } else if (result < 0) {
                stringBuffer.append(localParentGroupKey).append(":").append(loggedInUserId).append(":").append(withUserContact.getContactIds());
            } else {
                stringBuffer.append(localParentGroupKey).append(":").append(withUserContact.getContactIds()).append(":").append(loggedInUserId);
            }
            ChannelInfo channelInfo = new ChannelInfo(stringBuffer.toString(), userIdList);
            channelInfo.setClientGroupId(stringBuffer.toString());
            channelInfo.setType(Channel.GroupType.GROUPOFTWO.getValue());
            channelInfo.setParentKey(localParentGroupKey);
            channel = channelService.createGroupOfTwo(channelInfo);
        }
        return channel;
    }

    @Override
    protected void onPostExecute(Channel channel) {
        if (channel != null) {
            channelCreateListener.onSuccess(channel);
        } else {
            channelCreateListener.onFailure();
        }
    }

    public interface ChannelCreateListener {
        void onSuccess(@NonNull Channel channel);
        void onFailure();
    }
}
