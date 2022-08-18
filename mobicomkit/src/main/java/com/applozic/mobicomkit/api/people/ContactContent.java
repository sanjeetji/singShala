package com.applozic.mobicomkit.api.people;

import com.applozic.mobicomkit.annotations.ApplozicInternal;

@ApplozicInternal
public class ContactContent {

    private String contactNumber;
    private Short appVersion;
    private String email;

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Short getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(Short appVersion) {
        this.appVersion = appVersion;
    }
}
