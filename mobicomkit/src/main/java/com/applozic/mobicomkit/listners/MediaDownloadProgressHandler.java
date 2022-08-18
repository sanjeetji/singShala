package com.applozic.mobicomkit.listners;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.exception.ApplozicException;

/**
 * Callbacks for message attachment media download.
 */
public interface MediaDownloadProgressHandler {
    void onDownloadStarted();

    void onProgressUpdate(int percentage, ApplozicException e);

    void onCompleted(Message message, ApplozicException e);
}
