package com.applozic.mobicomkit.api.mention;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.MentionMetadataModel;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.channel.database.ChannelDatabaseService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.channel.ChannelUserMapper;
import com.applozic.mobicommons.people.contact.Contact;
import com.google.gson.JsonParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Helper class for 'Mentions'.
 * Contains most of the mentions logic.
 */
@ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS)
public final class MentionHelper {
    public static final Pattern MENTION_PATTERN = Pattern.compile("@(\\w+#\\d\\d\\d)");
    @ColorInt public static final int DETAILED_CONVERSATION_SPAN_COLOR = 0xFF5959FF;

    public static @NonNull List<Mention> getMentionsListForChannel(Context context, Integer channelKey) {
        ChannelDatabaseService channelDatabaseService = ChannelDatabaseService.getInstance(context);
        List<ChannelUserMapper> channelUserMapperList = channelDatabaseService.getChannelUserList(channelKey);
        if (channelUserMapperList == null) {
            return new ArrayList<>();
        }
        List<Mention> mentionUsersList = new ArrayList<>();
        AppContactService appContactService = new AppContactService(context);
        String currentUserId = MobiComUserPreference.getInstance(context).getUserId();
        for (ChannelUserMapper channelUserMapper : channelUserMapperList) {
            Contact contact = appContactService.getContactById(channelUserMapper.getUserKey());

            if (channelUserMapper.getUserKey().equals(currentUserId)) {
                continue;
            }

            if (contact != null && !TextUtils.isEmpty(contact.getUserId())) {
                mentionUsersList.add(new Mention(contact.getUserId(), contact.getDisplayName(), !TextUtils.isEmpty(contact.getLocalImageUrl()) ? contact.getLocalImageUrl() : contact.getImageURL()));
            } else {
                mentionUsersList.add(new Mention(channelUserMapper.getUserKey()));
            }
        }
        return mentionUsersList;
    }

    public static int getMentionIdentifierCode(@NonNull String uniqueIdentifier) {
        int uniqueCode = Math.abs(uniqueIdentifier.hashCode()) % 1000; //get a three digit code
        return uniqueCode < 100 //less than three digits
                ? uniqueCode + 100 : uniqueCode;
    }

    public static String getMentionIdentifierString(String commonIdentifier, @NonNull String uniqueIdentifier) {
        final char SPACE = ' ';
        final char SPACE_REPLACEMENT = '_';
        final char SEPARATOR = '#';
        String identifier = !TextUtils.isEmpty(commonIdentifier) ? commonIdentifier : uniqueIdentifier;
        StringBuilder mentionIdentifierSpacesRemoved = new StringBuilder();
        if (identifier.contains(" ")) {
            for (int i = 0; i < identifier.length(); i++) {
                char charToInsert =identifier.charAt(i) == SPACE ? SPACE_REPLACEMENT : identifier.charAt(i);
                mentionIdentifierSpacesRemoved.append(charToInsert);
            }
        } else {
            mentionIdentifierSpacesRemoved.append(identifier);
        }
        return mentionIdentifierSpacesRemoved.append(SEPARATOR).append(getMentionIdentifierCode(identifier)).toString().toLowerCase(Locale.getDefault());
    }

    public static MentionPair getServerSendReadyMentionPair(String notServerSendReadyMessageString, ArrayList<MentionMetadataModel> notServerSendReadyMentionMetadataList) {
        if (TextUtils.isEmpty(notServerSendReadyMessageString)) {
            return new MentionPair(Utils.EMPTY_STRING, null);
        }

        if (notServerSendReadyMentionMetadataList == null || notServerSendReadyMentionMetadataList.isEmpty()) {
            return new MentionPair(notServerSendReadyMessageString, null);
        }

        try {
            //sorting in ascending order is required so that server ready mention indexes can be calculated
            //reason: when replacing mention identifiers with server send ready @userId, the indexes in the metadata model remain no longer valid
            //because the lengths of the replacements and originals differ
            Collections.sort(notServerSendReadyMentionMetadataList, new Comparator<MentionMetadataModel>() {
                @Override
                public int compare(MentionMetadataModel o1, MentionMetadataModel o2) {
                    int comparatorValue = 0;
                    if (o1.indices[0] < o2.indices[0]) {
                        comparatorValue = -1;
                    } else if (o1.indices[0] > o2.indices[0]) {
                        comparatorValue = +1;
                    }
                    return comparatorValue;
                }
            });
        } catch (NullPointerException | IndexOutOfBoundsException exception) {
            exception.printStackTrace();
            return new MentionPair(notServerSendReadyMessageString, null);
        }

        ArrayList<MentionMetadataModel> serverSendReadyMentionMetadataList = new ArrayList<>();
        String serverSendReadyMessageString = notServerSendReadyMessageString;

        //replacing mentions identifiers in edit text string and side by side also creating new metadata model list
        for (MentionMetadataModel oldMentionMetadataModel : notServerSendReadyMentionMetadataList) {
            String mentionIdentifier = MentionHelper.getMentionIdentifierString(oldMentionMetadataModel.displayName, oldMentionMetadataModel.userId);

            int indexOfMentionIdentifierStart = serverSendReadyMessageString.indexOf(mentionIdentifier);

            if (indexOfMentionIdentifierStart == -1) {
                continue;
            }

            serverSendReadyMessageString = serverSendReadyMessageString.replaceFirst(mentionIdentifier, oldMentionMetadataModel.userId);

            MentionMetadataModel serverSendReadyMentionMetadataModel = new MentionMetadataModel();
            serverSendReadyMentionMetadataModel.userId = oldMentionMetadataModel.userId;
            serverSendReadyMentionMetadataModel.displayName = oldMentionMetadataModel.displayName;
            int[] indices = new int[2];
            indices[0] = indexOfMentionIdentifierStart - 1; // - 1 because we need index from mention token start character (@)
            indices[1] = indexOfMentionIdentifierStart + serverSendReadyMentionMetadataModel.userId.length();
            serverSendReadyMentionMetadataModel.indices = indices;

            serverSendReadyMentionMetadataList.add(serverSendReadyMentionMetadataModel);
        }

        return new MentionPair(serverSendReadyMessageString, serverSendReadyMentionMetadataList);
    }

    public static Spannable getMessageSpannableStringForMentionsDisplay(Context context, Message message, boolean isDetailedConversationList, String detailedSpanColor) {
        if (message == null) {
            return new SpannableString(Utils.EMPTY_STRING);
        } else {
            int backgroundSpanColor = 0;
            if (!TextUtils.isEmpty(detailedSpanColor)) {
                try {
                    backgroundSpanColor = Color.parseColor(detailedSpanColor);
                } catch (IllegalArgumentException exception) {
                    exception.printStackTrace();
                }
            }
            return getMessageSpannableStringForMentionsDisplay(context, message.getMessage(), getMentionsDataFromMessageMetadata(message.getMetadata()), isDetailedConversationList, backgroundSpanColor == 0 ? DETAILED_CONVERSATION_SPAN_COLOR : backgroundSpanColor);
        }
    }

    private static @NonNull Spannable getMessageSpannableStringForMentionsDisplay(Context context, String messageStringWithMentionsUserId, List<MentionMetadataModel> mentionMetadataModels, boolean isDetailedConversationList, int detailedSpanColor) {
        if (TextUtils.isEmpty(messageStringWithMentionsUserId)) {
            return new SpannableString(Utils.EMPTY_STRING);
        }

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(messageStringWithMentionsUserId);

        if (mentionMetadataModels == null) {
            return spannableStringBuilder;
        }

        try {
            //the list must be sorted in descending order position of mention to avoid the side-effect of replacing with indexes
            Collections.sort(mentionMetadataModels, new Comparator<MentionMetadataModel>() {
                @Override
                public int compare(MentionMetadataModel o1, MentionMetadataModel o2) {
                    int comparatorValue = 0;
                    if (o1.indices[0] > o2.indices[0]) {
                        comparatorValue = -1;
                    } else if (o1.indices[0] < o2.indices[0]) {
                        comparatorValue = +1;
                    }
                    return comparatorValue;
                }
            });
        } catch (NullPointerException | IndexOutOfBoundsException exception) {
            exception.printStackTrace();
            return spannableStringBuilder;
        }

        for (MentionMetadataModel metadataModel : mentionMetadataModels) {
            if (metadataModel == null || TextUtils.isEmpty(metadataModel.userId) || metadataModel.indices == null || metadataModel.indices.length < 2) {
                continue;
            }

            int start = metadataModel.indices[0];
            Contact contact = new AppContactService(context).getContactById(metadataModel.userId);
            String userIdOrDisplayName = !TextUtils.isEmpty(contact.getDisplayName()) ? contact.getDisplayName() : contact.getUserId();
            int end = metadataModel.indices[1];
            int replacedEnd = metadataModel.indices[0] + userIdOrDisplayName.length();
            int length = spannableStringBuilder.length();

            if (start >= 0 && start < length && end >= 0 && end - 1 < length) {
                spannableStringBuilder.replace(start + 1, end, userIdOrDisplayName);
            } else {
                continue;
            }

            int replacedLength = spannableStringBuilder.length();
            if (start < replacedLength && replacedEnd >= 0 && replacedEnd < replacedLength) {
                CharacterStyle characterStyle = isDetailedConversationList ? new ReceivedDetailedConversationMessageMentionDisplaySpan(detailedSpanColor) : new ReceivedQuickConversationMessageMentionDisplaySpan();
                spannableStringBuilder.setSpan(characterStyle, start, replacedEnd + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannableStringBuilder;
    }

    public static @NonNull Map<String, String> createMessageMetadata(@Nullable List<MentionMetadataModel> mentionMetadataModels) {
        Map<String, String> messageMentionMetadata = new HashMap<>();

        if (mentionMetadataModels == null || mentionMetadataModels.isEmpty()) {
            return messageMentionMetadata;
        }

        String jsonString = GsonUtils.getJsonFromObject(mentionMetadataModels, List.class);

        messageMentionMetadata.put(MentionMetadataModel.AL_MEMBER_MENTION, jsonString);

        return messageMentionMetadata;
    }

    public static @NonNull List<MentionMetadataModel> getMentionsDataFromMessageMetadata(@Nullable Map<String, String> messageMetadata) {
        List<MentionMetadataModel> mentionMetadataModels = new ArrayList<>();
        if (messageMetadata == null) {
            return mentionMetadataModels;
        }
        try {
            String mentionMetadataModelsString = messageMetadata.get(MentionMetadataModel.AL_MEMBER_MENTION);
            if (!TextUtils.isEmpty(mentionMetadataModelsString)) {
                MentionMetadataModel[] mentionMetadataModelsArray = (MentionMetadataModel[]) GsonUtils.getObjectFromJson(mentionMetadataModelsString, MentionMetadataModel[].class);
                mentionMetadataModels = Arrays.asList(mentionMetadataModelsArray);
            }
        } catch (JsonParseException exception) {
            exception.printStackTrace();
            return mentionMetadataModels;
        }
        return mentionMetadataModels;
    }

    @ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS)
    public static class MentionPair {
        private final String serverReadyMentionsString;
        private final ArrayList<MentionMetadataModel> serverReadyMentionsMetadataList;

        public ArrayList<MentionMetadataModel> getServerReadyMentionsMetadataList() {
            return serverReadyMentionsMetadataList;
        }

        public String getServerReadyMentionsString() {
            return serverReadyMentionsString;
        }

        public MentionPair(String serverReadyMentionsString, ArrayList<MentionMetadataModel> serverReadyMentionsMetadataList) {
            this.serverReadyMentionsString = serverReadyMentionsString;
            if (serverReadyMentionsMetadataList == null) {
                serverReadyMentionsMetadataList = new ArrayList<>();
            }
            this.serverReadyMentionsMetadataList = serverReadyMentionsMetadataList;
        }
    }

    public static boolean isLoggedInUserMentionedInChannelMessage(@NonNull Context context, @NonNull Message message) {
        String loggedInUserId = MobiComUserPreference.getInstance(context).getUserId();
        List<MentionMetadataModel> mentionMetadataModelList = getMentionsDataFromMessageMetadata(message.getMetadata());

        if (mentionMetadataModelList.isEmpty()) {
            return false;
        }

        boolean isLoggedInUserMentioned = false;
        for (MentionMetadataModel metadataModel : mentionMetadataModelList) {
            if (loggedInUserId.equals(metadataModel.userId)) {
                isLoggedInUserMentioned = true;
                break;
            }
        }

        return message.getGroupId() != null && message.getGroupId() != 0 && isLoggedInUserMentioned;
    }

    @ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS)
    public static class ReceivedQuickConversationMessageMentionDisplaySpan extends StyleSpan {
        public ReceivedQuickConversationMessageMentionDisplaySpan() {
            super(Typeface.BOLD_ITALIC);
        }
    }

    @ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS)
    public static class ReceivedDetailedConversationMessageMentionDisplaySpan extends BackgroundColorSpan {
        public ReceivedDetailedConversationMessageMentionDisplaySpan() {
            super(DETAILED_CONVERSATION_SPAN_COLOR);
        }

        public ReceivedDetailedConversationMessageMentionDisplaySpan(int color) {
            super(color);
        }
    }
}
