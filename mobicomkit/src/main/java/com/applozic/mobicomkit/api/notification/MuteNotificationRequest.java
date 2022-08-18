package com.applozic.mobicomkit.api.notification;

import android.text.TextUtils;

import com.applozic.mobicommons.json.JsonMarker;
import com.google.gson.annotations.SerializedName;

/**
 * Model class used for sending a mute notification request to the server.
 *
 * <p>Set either the userId or the groupId/clientGroupId along with the
 * time for which the notification is to be disabled.</p>
 */
public class MuteNotificationRequest extends JsonMarker {
    /**
     * User id of the contact/user to mute the notifications from.
     */
    String userId;

    /**
     * Group id of the channel (/group) to mute the notifications for.
     *
     * <p>Use either this or clientGroupId.</p>
     */
    @SerializedName("id")
    Integer groupId;

    /**
     * Client group id of the channel (/group) to mute the notifications for.
     *
     * <p>Use either this or groupId.</p>
     */
    String clientGroupId;

    /**
     * Time interval in milliseconds for which the notification is to be disabled.
     */
    Long notificationAfterTime;

    public MuteNotificationRequest(String clientGroupId, Long notificationAfterTime) {
        this.clientGroupId = clientGroupId;
        this.notificationAfterTime = notificationAfterTime;
    }

    public MuteNotificationRequest(Integer groupId, Long notificationAfterTime) {
        this.groupId = groupId;
        this.notificationAfterTime = notificationAfterTime;
    }

    public MuteNotificationRequest(Long notificationAfterTime, String userId) {
        this.notificationAfterTime = notificationAfterTime;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return groupId;
    }

    public void setId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getClientGroupId() {
        return clientGroupId;
    }

    public void setClientGroupId(String clientGroupId) {
        this.clientGroupId = clientGroupId;
    }

    public Long getNotificationAfterTime() {
        return notificationAfterTime;
    }

    public void setNotificationAfterTime(Long notificationAfterTime) {
        this.notificationAfterTime = notificationAfterTime;
    }

    /**
     * Checks if the mute notification request is valid.
     *
     * <p>Notification after time cannot be null or 0. Either userId, groupId or clientGroupId
     * must be non-empty/non-null.</p>
     *
     * @return true/false accordingly
     */
    public boolean isRequestValid() {

        return !((notificationAfterTime == null || notificationAfterTime <= 0) ||
                (TextUtils.isEmpty(userId) && TextUtils.isEmpty(clientGroupId) && groupId == null));
    }
}
