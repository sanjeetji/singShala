package com.applozic.mobicomkit.api.conversation.schedule;

import android.app.IntentService;
import android.content.Intent;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;

import java.util.Calendar;
import java.util.List;

@ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS)
public class ScheduleMessageService extends IntentService {

    public ScheduleMessageService() {
        super("MobiTexter Message Scheduler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Calendar c = Calendar.getInstance();
        long time = c.getTimeInMillis();
        MessageDatabaseService messageDatabaseService = new MessageDatabaseService(getApplicationContext());
        MobiComConversationService conversationService = new MobiComConversationService(getApplicationContext());
        List<Message> messages = messageDatabaseService.getScheduledMessages(time);
        for (Message message : messages) {
            message.setScheduledAt(null);
            conversationService.sendMessage(message);
            //Todo: broadcast for scheduled message fragment.
        }
        messageDatabaseService.deleteScheduledMessages(time);
    }

}
