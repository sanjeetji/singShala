package com.applozic.mobicomkit.listners;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicommons.people.contact.Contact;

//Cleanup: can be removed
@ApplozicInternal
public interface AlContactListener {
    void onGetContact(Contact contact);
}
