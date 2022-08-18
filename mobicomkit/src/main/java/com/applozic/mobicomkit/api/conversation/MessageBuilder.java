package com.applozic.mobicomkit.api.conversation;

import android.content.Context;

import com.applozic.mobicomkit.listners.MediaUploadProgressHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helper class that can be used to create send a {@link Message} object.
 */
public class MessageBuilder {

    private Message message;
    private Context context;

    public MessageBuilder(Context context) {
        this.message = new Message();
        this.context = context;
    }

    /**
     * Set the id of the receiver you wish to send the message to.
     *
     * @param to the user id of the user you want to send the message to
     * @return the message builder object
     */
    public MessageBuilder setTo(String to) {
        message.setTo(to);
        return this;
    }

    /**
     * Set the text of the message.
     *
     * @param message the message string/text content
     * @return the message builder object
     */
    public MessageBuilder setMessage(String message) {
        this.message.setMessage(message);
        return this;
    }

    /**
     * Set the type ({@link com.applozic.mobicomkit.api.conversation.Message.MessageType}
     * of the message.
     *
     * @param type the MessageType
     * @return the message builder object
     */
    public MessageBuilder setType(Short type) {
        message.setType(type);
        return this;
    }

    /**
     * Set the local path to any attachment.
     *
     * <p>Image, video, audio, documents etc. are supported.</p>
     *
     * @param filePath the local path to the file you want to be sent as
     *                 attachment with the message
     * @return the message builder object
     */
    public MessageBuilder setFilePath(String filePath) {
        List<String> pathList = new ArrayList<>();
        pathList.add(filePath);
        message.setFilePaths(pathList);
        return this;
    }

    /**
     * Set the content type ({@link com.applozic.mobicomkit.api.conversation.Message.ContentType}
     * of the message.
     *
     * @param contentType the ContentType
     * @return the message builder object
     */
    public MessageBuilder setContentType(short contentType) {
        message.setContentType(contentType);
        return this;
    }

    /**
     * Set the id of the {@link com.applozic.mobicommons.people.channel.Channel} (/group) you
     * wish to send this message to
     *
     * @param groupId the id of the Channel
     * @return the message builder object
     */
    public MessageBuilder setGroupId(Integer groupId) {
        message.setGroupId(groupId);
        return this;
    }

    /**
     * Set any metadata for the message.
     *
     * <p>This can be any extra key-value pair you wish to send with the message.</p>
     *
     * @param metadata A map containing the key-value metadata
     * @return the message builder object
     */
    public MessageBuilder setMetadata(Map<String, String> metadata) {
        message.setMetadata(metadata);
        return this;
    }

    /**
     * Set the client group id of the {@link com.applozic.mobicommons.people.channel.Channel} (/group) you
     * wish to send this message to
     *
     * <p>A Channel can be identified by either it's groupId or it's clientGroupId.</p>
     *
     * @param clientGroupId the client group id of the Channel
     * @return the message builder object
     */
    public MessageBuilder setClientGroupId(String clientGroupId) {
        message.setClientGroupId(clientGroupId);
        return this;
    }

    /**
     * If you want more attributes, you can create a {@link Message} object on your own and pass to
     * this method.
     *
     * <p>Refer to the {@link Message} class for detail.</p>
     *
     * @param message the message object
     * @return the message builder object
     */
    public MessageBuilder setMessageObject(Message message) {
        this.message = message;
        return this;
    }

    /**
     * Returns the message object.
     *
     * @return the message object.
     */
    public Message getMessageObject() {
        return message;
    }

    /**
     * Sends the message to the server. It will then be delivered to the respective user/group.
     */
    public void send() {
        new MobiComConversationService(context).sendMessageWithHandler(message, null);
    }

    /**
     * Sends the message to the server. It will then be delivered to the respective user/group.
     *
     * <p>You can optionally pass a {@link MediaUploadProgressHandler} that can be used to
     * receive callbacks for updates on the attachment upload.</p>
     *
     * @param handler the callback
     */
    public void send(MediaUploadProgressHandler handler) {
        new MobiComConversationService(context).sendMessageWithHandler(message, handler);
    }
}
