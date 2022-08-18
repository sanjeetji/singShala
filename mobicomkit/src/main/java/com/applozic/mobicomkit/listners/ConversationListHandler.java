package com.applozic.mobicomkit.listners;

import android.content.Context;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.conversation.AlConversation;
import com.applozic.mobicomkit.exception.ApplozicException;

import java.util.List;

@ApplozicInternal
public interface ConversationListHandler {
    void onResult(Context context, List<AlConversation> conversationList, ApplozicException e);
}
