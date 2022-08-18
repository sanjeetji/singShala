package com.applozic.mobicomkit.listners;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicommons.people.channel.Channel;

//Cleanup: can be removed
@ApplozicInternal
public interface AlChannelListener {
    void onGetChannel(Channel channel);
}
