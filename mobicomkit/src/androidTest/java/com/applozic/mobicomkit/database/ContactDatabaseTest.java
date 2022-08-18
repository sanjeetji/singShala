package com.applozic.mobicomkit.database;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.applozic.mobicomkit.contact.database.ContactDatabase;
import com.applozic.mobicommons.people.contact.Contact;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ContactDatabaseTest {
    MobiComDatabaseHelper dbHelper;
    ContactDatabase contactDatabase;

    Contact contact1;
    Contact contact2;
    Contact contact3;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = new MobiComDatabaseHelper(context, null, null, MobiComDatabaseHelper.DB_VERSION);
        contactDatabase = new ContactDatabase(context, dbHelper);

        contact1 = new Contact();
        contact2 = new Contact();
        contact3 = new Contact();

        contact1.setUserId("userId");
        contact1.setImageURL("imageUrl");
        //contact1.setFirstName("name"); first name is not set when retrieving from db
        contact1.setBlocked(true);
        contact1.setBlockedBy(false);
        contact1.setApplicationId("applicationId");

        //these need to be added because of an inconsistency with null and 0
        contact1.setDeletedAtTime(0L);
        contact1.setContactId(0L);
        contact1.setUserTypeId((short) 0);
        contact1.setRoleType((short) 0);
        contact1.setNotificationAfterTime(0L);
        contact1.setLastSeenAt(0L);
        contact1.setContactType((short) 0);
        contact1.setUnreadCount(0);
        contact1.setLastMessageAtTime(0L);

        contact2 = new Contact();
        contact2.setUserId("userId1");
        contact2.setImageURL("imageUrl1");
        //contact2.setFirstName("name1"); first name is not set when retrieving from db
        contact2.setBlocked(false);
        contact2.setBlockedBy(true);
        contact2.setApplicationId("applicationId");

        //these need to be added because of an inconsistency with null and 0
        contact2.setDeletedAtTime(0L);
        contact2.setContactId(0L);
        contact2.setUserTypeId((short) 0);
        contact2.setRoleType((short) 0);
        contact2.setNotificationAfterTime(0L);
        contact2.setLastSeenAt(0L);
        contact2.setContactType((short) 0);
        contact2.setUnreadCount(0);
        contact2.setLastMessageAtTime(0L);

        contact3.setUserId("userId2");
        contact3.setImageURL("imageUrl2");
        //contact3.setFirstName("name2"); first name is not set when retrieving from db
        contact3.setBlocked(true);
        contact3.setBlockedBy(true);
        contact3.setApplicationId("applicationId");

        //these need to be added because of an inconsistency with null and 0
        contact3.setDeletedAtTime(0L);
        contact3.setContactId(0L);
        contact3.setUserTypeId((short) 0);
        contact3.setRoleType((short) 0);
        contact3.setNotificationAfterTime(0L);
        contact3.setLastSeenAt(0L);
        contact3.setContactType((short) 0);
        contact3.setUnreadCount(0);
        contact3.setLastMessageAtTime(0L);
    }

    @After
    public void closeDb() throws IOException {
        contactDatabase.deleteAllContact(contactDatabase.getAllContact());

        dbHelper.delDatabase();
        dbHelper.close();
    }

    @Test
    public void testAddAndRetrieve() {
        contactDatabase.addContact(contact1);
        contact1.setFullName(contactDatabase.getFullNameForUpdate(contact1));
        contact1.setDeletedAtTime(0L);

        //add and getContactById
        assertThat(contactDatabase.getContactById(contact1.getUserId()));
        assertThat(contactDatabase.getContactById("non-existant")).isNull();

        contact2.setFullName(contactDatabase.getFullNameForUpdate(contact2));
        contact3.setFullName(contactDatabase.getFullNameForUpdate(contact3));
        contact2.setDeletedAtTime(0L);
        contact3.setDeletedAtTime(0L);

        contactDatabase.addAllContact(Arrays.asList(contact2, contact3));
        List<Contact> contactList = contactDatabase.getAllContact(); //full name asc
        assertThat(contactList.get(0).toString()).isEqualTo(contact1.toString());
        assertThat(contactList.get(1).toString()).isEqualTo(contact2.toString());
        assertThat(contactList.get(2).toString()).isEqualTo(contact3.toString());
    }

    @Test
    public void testUpdates() {
        contactDatabase.addContact(contact1);
        contactDatabase.addContact(contact2);
        contact1.setImageURL("newImage");
        contact1.setLastSeenAt(5456L);
        contact1.setDeletedAtTime(78L);
        contact1.setFullName(contactDatabase.getFullNameForUpdate(contact1));
        contactDatabase.updateContact(contact1);

        assertThat(contactDatabase.getContactById(contact1.getUserId()).toString()).isEqualTo(contact1.toString());

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("test", "value");
        contact1.setMetadata(hashMap);
        contactDatabase.updateContact(contact1);
        assertThat(contactDatabase.getContactById(contact1.getUserId()).toString()).isEqualTo(contact1.toString());

        //update connection disconnection status
        assertThat(contactDatabase.getContactById(contact2.getUserId()).getLastSeenAt()).isEqualTo(0);
        contactDatabase.updateConnectedOrDisconnectedStatus(contact2.getUserId(), new Date(), true);
        contact2.setConnected(true);

        assertThat(contactDatabase.getContactById(contact2.getUserId()).isConnected()).isEqualTo(true);
        assertThat(contactDatabase.getContactById(contact2.getUserId()).getLastSeenAt()).isNotNull();
    }
}
