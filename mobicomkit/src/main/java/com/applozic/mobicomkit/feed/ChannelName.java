package com.applozic.mobicomkit.feed;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicommons.json.JsonMarker;

@ApplozicInternal //Cleanup: can be removed
public class ChannelName extends JsonMarker {

    private Integer groupId;
    private String newName;

    public ChannelName(String newName, Integer groupId) {
        this.newName = newName;
        this.groupId = groupId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
