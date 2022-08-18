package com.applozic.mobicomkit.feed;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicommons.json.JsonMarker;

import java.util.List;

@ApplozicInternal
public class UserDetailListFeed extends JsonMarker {

    private List<String> userIdList;
    private List<String> phoneNumberList;
    private boolean contactSync;

    public List<String> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }

    public List<String> getPhoneNumberList() {
        return phoneNumberList;
    }

    public void setPhoneNumberList(List<String> phoneNumberList) {
        this.phoneNumberList = phoneNumberList;
    }

    public boolean isContactSync() {
        return contactSync;
    }

    public void setContactSync(boolean contactSync) {
        this.contactSync = contactSync;
    }

    @Override
    public String toString() {
        return "UserDetailListFeed{" +
                "userIdList=" + userIdList +
                ", phoneNumberList=" + phoneNumberList +
                ", contactSync=" + contactSync +
                '}';
    }
}
