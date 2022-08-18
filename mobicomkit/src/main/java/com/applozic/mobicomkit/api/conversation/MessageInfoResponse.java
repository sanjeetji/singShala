package com.applozic.mobicomkit.api.conversation;

import com.applozic.mobicommons.json.JsonMarker;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * Response for message read/delivered information requests to the server.
 */
public class MessageInfoResponse extends JsonMarker {

    @SerializedName("response")
    List<MessageInfo> messageInfoList;

    public List<MessageInfo> getMessageInfoList() {
        return messageInfoList;
    }

    public void setMessageInfoList(List<MessageInfo> messageInfoList) {
        this.messageInfoList = messageInfoList;
    }

    /**
     * Returns the list of people that have the message delivered.
     *
     * <p>This information is in context of the API call which must have been for a particular message.</p>
     *
     * @return the required list
     */
    public List<MessageInfo> getDeliverdToUserList() {

        if (this.messageInfoList == null) {
            return null;
        }
        List<MessageInfo> deliverdToUserList = new ArrayList<MessageInfo>();

        for (MessageInfo messageInfo : messageInfoList) {
            if (messageInfo.isDelivered()) {
                deliverdToUserList.add(messageInfo);
            }
        }
        return deliverdToUserList;
    }

    /**
     * Returns the list of people that have read the message.
     *
     * <p>This information is in context of the API call which must have been for a particular message.</p>
     *
     * @return the required list
     */
    public List<MessageInfo> getReadByUserList() {

        if (this.messageInfoList == null) {
            return null;
        }
        List<MessageInfo> readList = new ArrayList<MessageInfo>();

        for (MessageInfo messageInfo : messageInfoList) {
            if (messageInfo.isRead()) {
                readList.add(messageInfo);
            }
        }
        return readList;
    }

}
