package com.applozic.mobicomkit.uiwidgets.customization;

import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;

/**
 * This class is used to store the UI customization filters for the conversation list related UI.
 *
 * <p>The related classes include:
 * 1. {@link com.applozic.mobicomkit.uiwidgets.conversation.fragment.MobiComQuickConversationFragment}
 * 2. {@link com.applozic.mobicomkit.uiwidgets.conversation.adapter.QuickConversationAdapter}
 * 3. {@link com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity}</p>
 */
public class ConversationListCustomization {
    private boolean messageSenderNameVisible = false;

    public ConversationListCustomization(AlCustomizationSettings alCustomizationSettings) {
        messageSenderNameVisible = alCustomizationSettings.isShowSenderNameForGroupsInConversationList();
    }

    public void setMessageSenderNameVisible(boolean messageSenderNameVisible) {
        this.messageSenderNameVisible = messageSenderNameVisible;
    }

    public boolean isMessageSenderNameVisible() {
        return messageSenderNameVisible;
    }
}
