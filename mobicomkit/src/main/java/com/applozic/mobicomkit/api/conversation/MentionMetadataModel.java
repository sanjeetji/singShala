package com.applozic.mobicomkit.api.conversation;

import com.applozic.mobicomkit.annotations.ApplozicInternal;

@ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS)
public class MentionMetadataModel {
    public static final String AL_MEMBER_MENTION = "AL_MEMBER_MENTION";
    public static final String AL_NOTIFICATION = "AL_NOTIFICATION";

    public int[] indices;
    public String userId;
    public String displayName;
}
