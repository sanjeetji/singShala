package com.applozic.mobicomkit.listners;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.exception.ApplozicException;

import java.util.List;

/**
 * Callback for when retrieving a message list.
 */
public interface MessageListHandler {
    void onResult(List<Message> messageList, ApplozicException e);
}
