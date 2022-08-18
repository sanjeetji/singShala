package com.applozic.mobicomkit.api.conversation.schedule;

import android.util.Log;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MobiComMessageService;

import java.util.TimerTask;

@ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS)
public class MessageSenderTimerTask extends TimerTask {

    private static final String TAG = "MessageSenderTimerTask";

    private MobiComMessageService mobiComMessageService;
    private Message message;
    private String to;

    public MessageSenderTimerTask(MobiComMessageService mobiComMessageService, Message message, String to) {
        this.mobiComMessageService = mobiComMessageService;
        this.message = message;
        this.to = to;
    }

    @Override
    public void run() {
        Log.i(TAG, "Sending message to: " + to + " from MessageSenderTimerTask");
        mobiComMessageService.processMessage(message, to, 0);
    }
}