package com.applozic.mobicomkit.api.conversation;

import java.util.Map;

/**
 * Model class used to store data required when updating a message's metadata.
 */
public class MessageMetadataUpdate {
    String key;
    Map<String, String> metadata;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
