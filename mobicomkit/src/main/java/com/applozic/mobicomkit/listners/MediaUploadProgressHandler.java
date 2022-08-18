package com.applozic.mobicomkit.listners;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.exception.ApplozicException;

/**
 * Callbacks for message attachment media upload.
 */
public interface MediaUploadProgressHandler {
    void onUploadStarted(ApplozicException e, String oldMessageKey);

    void onProgressUpdate(int percentage, ApplozicException e, String oldMessageKey);

    void onCancelled(ApplozicException e, String oldMessageKey);

    void onCompleted(ApplozicException e, String oldMessageKey);

    void onSent(Message message, String oldMessageKey);
}
