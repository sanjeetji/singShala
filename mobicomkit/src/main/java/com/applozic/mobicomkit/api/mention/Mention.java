package com.applozic.mobicomkit.api.mention;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applozic.mobicomkit.annotations.ApplozicInternal;

@ApplozicInternal
public class Mention {
    private final CharSequence userId;
    private final CharSequence displayName;
    private final String mentionIdentifier;
    private final String profileImage;

    public Mention(@NonNull CharSequence userId) {
        this(userId, null);
    }

    public Mention(@NonNull CharSequence userId, @Nullable CharSequence displayName) {
        this(userId, displayName, null);
    }

    public Mention(@NonNull CharSequence userId, @Nullable CharSequence displayName, @Nullable String profileImage) {
        this.userId = userId;
        this.displayName = displayName;
        this.profileImage = profileImage;
        this.mentionIdentifier = MentionHelper.getMentionIdentifierString(displayName != null ? displayName.toString() : null, userId.toString());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof Mention && ((Mention) obj).userId == userId;
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return userId.toString();
    }

    @NonNull
    public CharSequence getUserId() {
        return userId;
    }

    @Nullable
    public CharSequence getDisplayName() {
        return displayName;
    }

    @Nullable
    public String getProfileImage() {
        return profileImage;
    }

    @NonNull
    public String getMentionIdentifier() {
        return mentionIdentifier;
    }

    @NonNull
    public String getDisplayNameOrUserId() {
        return !TextUtils.isEmpty(displayName) ? displayName.toString() : userId.toString();
    }
}