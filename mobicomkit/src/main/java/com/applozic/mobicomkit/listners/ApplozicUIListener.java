package com.applozic.mobicomkit.listners;

import com.applozic.mobicomkit.api.conversation.Message;

import java.util.Set;

/**
 * This class contains callbacks for all major Applozic events.
 *
 * <p>These are used when working with capturing Applozic events using {@link com.applozic.mobicomkit.broadcast.AlEventManager}.</p>
 */
public interface ApplozicUIListener {
    /**
     * Will be called whenever an Applozic {@link Message} is sent to the server by the current user.
     *
     * @param message the message object.
     */
    void onMessageSent(Message message);

    /**
     * Will be called whenever an Applozic {@link Message} is received from the server for the current user.
     *
     * @param message the message object.
     */
    void onMessageReceived(Message message);

    /**
     * Not used. Please ignore.
     */
    void onLoadMore(boolean loadMore);

    /**
     * Will be called whenever a new message is synced from the server.
     *
     * <p>This includes a new message being received, sent or updated.</p>
     *
     * @param message the message object
     * @param key the key of the synced message
     */
    void onMessageSync(Message message, String key);

    /**
     * Will be called whenever a message is deleted.
     *
     * @param messageKey the key of the message deleted.
     * @param userId null
     */
    void onMessageDeleted(String messageKey, String userId);

    /**
     * Will be called whenever a message is delivered.
     *
     * @param message the message object
     * @param userId the user that got the message delivered to it
     */
    void onMessageDelivered(Message message, String userId);

    /**
     * Will be called when all message sent to a contact have been delivered.
     *
     * @param userId the id of the user to which all the messages have been delivered.
     */
    void onAllMessagesDelivered(String userId);

    /**
     * Will be called when all message sent to a contact have been read.
     *
     * @param userId the id of the user by which all the messages have been read.
     */
    void onAllMessagesRead(String userId);

    /**
     * Will be called whenever a conversation(group or 1-to-1 chat) is deleted.
     *
     * @param userId the 1-to-1 chat that was deleted. null/empty if channel was deleted
     * @param channelKey the channel that was deleted. null/0 if 1-to-1 chat was deleted
     * @param response status of the api response
     */
    void onConversationDeleted(String userId, Integer channelKey, String response);

    /**
     * Will be called whenever the typing status of a user is changed.
     *
     * @param userId the user the typing status corresponds to
     * @param isTyping true if user is typing
     */
    void onUpdateTypingStatus(String userId, String isTyping);

    /**
     * Will be called whenever the last scene of a user is updated.
     *
     * <p>Use {@link com.applozic.mobicomkit.api.account.user.UserClientService#getUserDetails(Set)}.</p>
     *
     * @param userId the user id of the user whose last seen was updated
     */
    void onUpdateLastSeen(String userId);

    /**
     * Called when MQTT is disconnected.
     */
    void onMqttDisconnected();

    /**
     * Called when MQTT is connected.
     */
    void onMqttConnected();

    /**
     * Called when the user comes online.
     */
    void onUserOnline();

    /**
     * Called when the user goes offline.
     */
    void onUserOffline();

    /**
     * Called when the user is activated.
     * @param isActivated true if activated
     */
    void onUserActivated(boolean isActivated);

    /**
     * Called when channels are synced with the server.
     */
    void onChannelUpdated();

    /**
     * Called when a conversation is fully read/unread count is 0.
     *
     * @param userId the id of the group/user chat that is read (it's group id in case of isGroup = true)
     * @param isGroup is the conversation read for a group or 1-to-1 chat. true for group.
     */
    void onConversationRead(String userId, boolean isGroup);

    /**
     * Called when a user's details are updated and synced with the server.
     *
     * <p>Use {@link com.applozic.mobicomkit.api.account.user.UserClientService#getUserDetails(Set)}.</p>
     *
     * @param userId the user id of the user whose details have been updated
     */
    void onUserDetailUpdated(String userId);

    /**
     * Called when a message metadata ia updated and synced with the server.
     *
     * @param keyString the key of the message
     */
    void onMessageMetadataUpdated(String keyString);

    /**
     * Called when a user is muted/un-muted for the current user.
     *
     * @param mute muted/un-muted true/false
     * @param userId the user muted/un-muted
     */
    void onUserMute(boolean mute, String userId);

    /**
     * Called when a channel/group is muted for the current user.
     *
     * @param groupId the channel muted
     */
    void onGroupMute(Integer groupId);
}
