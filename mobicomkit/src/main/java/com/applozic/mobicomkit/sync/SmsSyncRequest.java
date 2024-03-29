package com.applozic.mobicomkit.sync;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicommons.json.JsonMarker;
import com.applozic.mobicomkit.api.conversation.Message;

import java.util.List;

@ApplozicInternal
public class SmsSyncRequest extends JsonMarker {

    private List<Message> smsList;

    public List<Message> getSmsList() {
        return smsList;
    }

    public void setSmsList(List<Message> smsList) {
        this.smsList = smsList;
    }


}


