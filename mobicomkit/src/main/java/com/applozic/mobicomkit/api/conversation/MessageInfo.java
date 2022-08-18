package com.applozic.mobicomkit.api.conversation;

import com.applozic.mobicommons.json.JsonMarker;

/**
 * Model class for storing message read/delivered information.
 *
 * <p>This includes:
 * - The userId of concerned user.
 * - The status of the message for that user. See {@link Message.Status}.</p>
 */
public class MessageInfo extends JsonMarker {

    String userId;
    Short status;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public boolean isRead() {
        return Message.Status.READ.getValue().equals(getStatus()) || Message.Status.DELIVERED_AND_READ.getValue().equals(getStatus());
    }

    public boolean isDelivered() {
        return Message.Status.DELIVERED.getValue().equals(getStatus());
    }

}
