package com.applozic.mobicomkit;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.applozic.mobicomkit.api.account.user.UserClientService;
import com.applozic.mobicomkit.api.account.user.UserDetail;
import com.applozic.mobicomkit.api.account.user.UserService;
import com.applozic.mobicomkit.api.notification.MuteUserResponse;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicomkit.feed.RegisteredUsersApiResponse;
import com.applozic.mobicomkit.feed.SyncBlockUserApiResponse;
import com.applozic.mobicomkit.sync.SyncUserBlockListFeed;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.contact.Contact;
import com.google.common.truth.Truth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.applozic.mobicomkit.MockedConstants.syncUserBlockListFeed;
import static com.applozic.mobicomkit.MockedConstants.testUserId;
import static com.applozic.mobicomkit.MockedConstants.userDetailsApiResponse;
import static com.applozic.mobicomkit.MockedConstants.userId1;
import static com.applozic.mobicomkit.MockedConstants.userId2;

@RunWith(AndroidJUnit4.class)
public class UserServiceTest { //TODO: add test that compares two contact/other objects fully, null and 0/"" checks
    UserService userService;

    @Mock
    UserClientService userClientService;
    @Mock
    AppContactService appContactService;

    @Before
    public void setup() {
        userClientService = Mockito.mock(UserClientService.class);
        appContactService = Mockito.mock(AppContactService.class);

        userService = UserService.getInstance(ApplicationProvider.getApplicationContext());

        userService.setBaseContactService(appContactService);
        userService.setUserClientService(userClientService);
    }

    @Test
    public void processUserBlockSuccess() {
        ApiResponse expectedFailureApiResponse = new ApiResponse();
        expectedFailureApiResponse.setStatus("success");

        Mockito.when(userClientService.userBlock(testUserId, true)).thenReturn(expectedFailureApiResponse);

        ApiResponse actualSuccessApiResponse = userService.processUserBlock(testUserId, true);

        Truth.assertThat(actualSuccessApiResponse).isNotNull();
        Truth.assertThat(actualSuccessApiResponse.getStatus()).isEqualTo("success"); //in applozic as of the date this test is written, only the status of the ApiResponse is used
        Mockito.verify(appContactService, Mockito.times(1)).updateUserBlocked(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void processUserBlockFailureNull() {
        Mockito.when(userClientService.userBlock(testUserId, true)).thenReturn(null);

        ApiResponse actualFailureApiResponse = userService.processUserBlock(testUserId, true);

        Truth.assertThat(actualFailureApiResponse).isNull();
        Mockito.verify(appContactService, Mockito.never()).updateUserBlocked(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void processUserBlockFailure() {
        ApiResponse expectedFailureApiResponse = new ApiResponse();
        expectedFailureApiResponse.setStatus("anything but success");

        Mockito.when(userClientService.userBlock(testUserId, true)).thenReturn(expectedFailureApiResponse);

        ApiResponse actualFailureApiResponse = userService.processUserBlock(testUserId, true);

        Truth.assertThat(actualFailureApiResponse).isNull();
        Mockito.verify(appContactService, Mockito.never()).updateUserBlocked(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void processSyncUserBlock() {
        SyncBlockUserApiResponse expectedSyncBlockUserApiResponse = new SyncBlockUserApiResponse();
        SyncUserBlockListFeed expectedSyncUserBlockListFeed = syncUserBlockListFeed();
        expectedSyncBlockUserApiResponse.setStatus("success");
        expectedSyncBlockUserApiResponse.setResponse(expectedSyncUserBlockListFeed);

        Mockito.when(userClientService.getSyncUserBlockList(ArgumentMatchers.anyString())).thenReturn(expectedSyncBlockUserApiResponse);

        //to go inside if for blocked to list
        Mockito.when(appContactService.isContactExists(expectedSyncUserBlockListFeed.getBlockedToUserList().get(0).getBlockedTo())).thenReturn(true);
        //to go inside if for blocked by list
        Mockito.when(appContactService.isContactExists(expectedSyncUserBlockListFeed.getBlockedToUserList().get(0).getBlockedBy())).thenReturn(true);

        userService.processSyncUserBlock();

        //2 for blocked to
        Mockito.verify(appContactService, Mockito.times(2)).updateUserBlocked(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());

        //2 for blocked by
        Mockito.verify(appContactService, Mockito.times(2)).updateUserBlockedBy(ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());

        //for each SyncUserBlockFeed
        Mockito.verify(appContactService, Mockito.times(1)).isContactExists(expectedSyncUserBlockListFeed.getBlockedToUserList().get(0).getBlockedTo());
        Mockito.verify(appContactService, Mockito.times(1)).isContactExists(expectedSyncUserBlockListFeed.getBlockedToUserList().get(1).getBlockedTo());
        Mockito.verify(appContactService, Mockito.times(1)).isContactExists(expectedSyncUserBlockListFeed.getBlockedByUserList().get(0).getBlockedBy());
        Mockito.verify(appContactService, Mockito.times(1)).isContactExists(expectedSyncUserBlockListFeed.getBlockedByUserList().get(1).getBlockedBy());

        //2 else {} paths, one for each list (blocked by and blocked to)
        Mockito.verify(appContactService, Mockito.times(2)).upsert(ArgumentMatchers.any(Contact.class));
    }

    @Test
    public void processUsers() {
        UserDetail userDetail = new UserDetail();
        userDetail.setUserId(testUserId);
        userDetail.setImageLink("Image Link");

        userService.processUser(userDetail, Contact.ContactType.APPLOZIC);

        Mockito.doAnswer(invocation -> {
            Contact contactToUpsert = invocation.getArgument(0);

            Truth.assertThat(contactToUpsert).isNotNull();
            Truth.assertThat(contactToUpsert.getUserId()).isEqualTo(testUserId);
            Truth.assertThat(contactToUpsert.getDisplayName()).isNull();
            Truth.assertThat(contactToUpsert.getImageURL()).isEqualTo(userDetail.getImageLink());
            return null;
        }).when(appContactService).upsert(ArgumentMatchers.any(Contact.class));

        Mockito.verify(appContactService, Mockito.times(1)).upsert(ArgumentMatchers.any(Contact.class));
    }

    @Test
    public void getContactFromUserDetails() {
        UserDetail[] expectedUserDetails = (UserDetail[]) GsonUtils.getObjectFromJson(userDetailsApiResponse, UserDetail[].class);

        Contact actualContact = userService.getContactFromUserDetail(expectedUserDetails[0]);

        Mockito.verify(appContactService, Mockito.times(1)).upsert(ArgumentMatchers.any(Contact.class));

        Truth.assertThat(actualContact.getUserId()).isEqualTo(expectedUserDetails[0].getUserId());
        Truth.assertThat(actualContact.getDisplayName()).isEqualTo(expectedUserDetails[0].getDisplayName());
        Truth.assertThat(actualContact.isConnected()).isEqualTo(expectedUserDetails[0].isConnected());
        Truth.assertThat(actualContact.getLastSeenAt()).isEqualTo(expectedUserDetails[0].getLastSeenAtTime());
        Truth.assertThat(actualContact.getImageURL()).isEqualTo(expectedUserDetails[0].getImageLink());
        Truth.assertThat(actualContact.getUnreadCount()).isAnyOf(expectedUserDetails[0].getUnreadCount(), 0); //needs to be fixed
    }

    @Test
    public void processUserDetails() {
        Set<String> userIdSet = new HashSet<>();
        userIdSet.add(userId1);
        userIdSet.add(userId2);

        Mockito.when(userClientService.getUserDetails(userIdSet)).thenReturn(userDetailsApiResponse);

        userService.processUserDetails(userIdSet);

        UserDetail[] expectedUserDetails = (UserDetail[]) GsonUtils.getObjectFromJson(userDetailsApiResponse, UserDetail[].class);

        Mockito.verify(appContactService, Mockito.times(2)).upsert(ArgumentMatchers.any(Contact.class));

        Mockito.doAnswer(invocation -> {
            Contact contact = invocation.getArgument(0);
            Truth.assertThat(contact.getUserId()).isAnyOf(expectedUserDetails[0].getUserId(), expectedUserDetails[1].getUserId());
            return null;
        }).when(appContactService).upsert(ArgumentMatchers.any(Contact.class));
    }

    @Test
    public void processUserDetail() {
        UserDetail userDetail1 = new UserDetail();
        userDetail1.setUserId(userId1);
        UserDetail userDetail2 = new UserDetail();
        userDetail2.setUserId(userId2);

        Set<UserDetail> userDetailSet = new HashSet<>();

        userDetailSet.add(userDetail1);
        userDetailSet.add(userDetail2);

        userService.processUserDetail(userDetailSet);

        Mockito.verify(appContactService, Mockito.times(2)).upsert(ArgumentMatchers.any());
    }

    @Test
    public void getUserListBySearch() throws Exception {
        List<UserDetail> expectedUserDetails = Arrays.asList((UserDetail[]) GsonUtils.getObjectFromJson(userDetailsApiResponse, UserDetail[].class));

        ApiResponse expectedApiResponse = new ApiResponse();
        expectedApiResponse.setStatus("success");
        expectedApiResponse.setResponse(expectedUserDetails);
        Mockito.when(userClientService.getUsersBySearchString(ArgumentMatchers.anyString())).thenReturn(expectedApiResponse);

        List<Contact> expectedContactList = new ArrayList<>();

        for (UserDetail userDetail : expectedUserDetails) { //*A
            expectedContactList.add(userService.getContactFromUserDetail(userDetail)); //has been tested, NOTE: this will also call baseContactService.upsert()
        }

        List<Contact> actualContactList = userService.getUserListBySearch("Test Search"); //*B

        Truth.assertThat(actualContactList.size()).isEqualTo(expectedContactList.size());
        Truth.assertThat(actualContactList.get(0).toString()).isEqualTo(expectedContactList.get(0).toString());
        Truth.assertThat(actualContactList.get(1).toString()).isEqualTo(expectedContactList.get(1).toString());

        //2 for *A, 2 for *B
        Mockito.verify(appContactService, Mockito.times(4)).upsert(ArgumentMatchers.any());
    }

    @Test
    public void updateUserDisplayNameSuccess() {
        //Note: return value from userService.updateUserDisplayName() is not used
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus("success");

        Mockito.when(userClientService.updateUserDisplayName(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(apiResponse);

        ApiResponse actualApiResponse = userService.updateUserDisplayName(testUserId, "Display Name");

        Mockito.verify(appContactService, Mockito.times(1)).updateMetadataKeyValueForUserId(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Truth.assertThat(apiResponse).isEqualTo(actualApiResponse);
    }

    @Test
    public void updateUserDisplayNameFailureNull() {
        //Note: return value from userService.updateUserDisplayName() is not used
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus("anything but success");

        Mockito.when(userClientService.updateUserDisplayName(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(apiResponse);

        ApiResponse actualApiResponse = userService.updateUserDisplayName(testUserId, "Display Name");

        Mockito.verify(appContactService, Mockito.never()).updateMetadataKeyValueForUserId(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Truth.assertThat(actualApiResponse).isEqualTo(apiResponse);
    }

    @Test
    public void updateUserDisplayNameFailure() {
        //Note: return value from userService.updateUserDisplayName() is not used
        Mockito.when(userClientService.updateUserDisplayName(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(null);

        ApiResponse actualApiResponse = userService.updateUserDisplayName(testUserId, "Display Name");

        Mockito.verify(appContactService, Mockito.never()).updateMetadataKeyValueForUserId(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Truth.assertThat(actualApiResponse).isNull();
    }

    @Test
    public void processMuteUserResponse() throws InterruptedException {
        MuteUserResponse muteUserResponse = new MuteUserResponse();
        muteUserResponse.setUserId(testUserId);
        //TODO: method as no null check for MuteUserResponse, add that and then add test case

        userService.processMuteUserResponse(muteUserResponse);

        Mockito.verify(appContactService, Mockito.times(1)).upsert(ArgumentMatchers.any(Contact.class));
    }

    @Test
    public void updateUserWithResponse() {
        ApiResponse expectedApiResponse = new ApiResponse();
        expectedApiResponse.setStatus("success");

        Mockito.when(userClientService.updateDisplayNameORImageLink("displayName", "profileImageLink", "status", "contactNumber", "emailId", null, "userId")).thenReturn(expectedApiResponse);
        Mockito.when(appContactService.getContactById(ArgumentMatchers.anyString())).thenReturn(new Contact());

        ApiResponse apiResponse = userService.updateUserWithResponse("displayName", "profileImageLink", "localURL", "status", "contactNumber", "emailId", null, "userId");

        Truth.assertThat(apiResponse).isEqualTo(expectedApiResponse);

        Mockito.verify(appContactService, Mockito.times(1)).upsert(ArgumentMatchers.any(Contact.class));
    }

    @Test
    public void updateDisplayNameORImageLink() {
        ApiResponse expectedApiResponse = new ApiResponse();
        expectedApiResponse.setStatus("success");

        Mockito.when(userClientService.updateDisplayNameORImageLink("displayName", "profileImageLink", "status", "contactNumber", "emailId", null, "userId")).thenReturn(expectedApiResponse);
        Mockito.when(appContactService.getContactById(ArgumentMatchers.anyString())).thenReturn(new Contact());

        String status = userService.updateDisplayNameORImageLink("displayName", "profileImageLink", "localURL", "status", "contactNumber", "emailId", null, "userId");

        Truth.assertThat(status).isEqualTo(expectedApiResponse.getStatus());

        Mockito.verify(appContactService, Mockito.times(1)).upsert(ArgumentMatchers.any(Contact.class));
    }

    @Test
    public void getMutedUserList() {
        MuteUserResponse muteUserResponse1 = new MuteUserResponse();
        muteUserResponse1.setUserId("user1");
        MuteUserResponse muteUserResponse2 = new MuteUserResponse();
        muteUserResponse2.setUserId("user2");
        MuteUserResponse muteUserResponse3 = new MuteUserResponse();
        muteUserResponse3.setUserId("user3");

        MuteUserResponse[] expectedMuteUserResponseList = new MuteUserResponse[] {muteUserResponse1, muteUserResponse2, muteUserResponse3};

        Mockito.when(userClientService.getMutedUserList()).thenReturn(expectedMuteUserResponseList);

        List<MuteUserResponse> muteUserResponseList = userService.getMutedUserList();

        Truth.assertThat(muteUserResponseList).isEqualTo(Arrays.asList(expectedMuteUserResponseList));

        Mockito.verify(appContactService, Mockito.times(expectedMuteUserResponseList.length)).upsert(ArgumentMatchers.any(Contact.class));
    }

    @Test
    public void muteUserNotifications() {
        ApiResponse expectedApiResponse = new ApiResponse();
        expectedApiResponse.setStatus("success");

        Mockito.when(userClientService.muteUserNotifications(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(expectedApiResponse);

        ApiResponse apiResponse = userService.muteUserNotifications(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong());

        Truth.assertThat(apiResponse).isEqualTo(expectedApiResponse);
        Truth.assertThat(apiResponse.isSuccess()).isTrue();
    }

    @Test
    public void getRegisteredUsersList() {
        RegisteredUsersApiResponse expectedRegisteredUsersApiResponse = new RegisteredUsersApiResponse();
        UserDetail[] expectedUserDetails = (UserDetail[]) GsonUtils.getObjectFromJson(userDetailsApiResponse, UserDetail[].class);
        Set<UserDetail> userDetailSet = new HashSet<>(Arrays.asList(expectedUserDetails));
        expectedRegisteredUsersApiResponse.setLastFetchTime(12345L);
        expectedRegisteredUsersApiResponse.setTotalUnreadCount(100);
        expectedRegisteredUsersApiResponse.setUsers(userDetailSet);

        String response = GsonUtils.getJsonFromObject(expectedRegisteredUsersApiResponse, RegisteredUsersApiResponse.class);

        Mockito.when(userClientService.getRegisteredUsers(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(response);

        RegisteredUsersApiResponse registeredUsersApiResponse = userService.getRegisteredUsersList(ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt());

        Truth.assertThat(registeredUsersApiResponse.toString()).isEqualTo(expectedRegisteredUsersApiResponse.toString());

        Mockito.verify(appContactService, Mockito.times(expectedRegisteredUsersApiResponse.getUsers().size())).upsert(ArgumentMatchers.any(Contact.class));
    }

    @Test
    public void getOnlineUsers() {
        Map<String, String> expectedUserMapList = new HashMap<>();
        expectedUserMapList.put("user1", "true");
        expectedUserMapList.put("user2", "false");
        expectedUserMapList.put("user3", "true");
        expectedUserMapList.put("user4", "true");

        String[] expectedUserIdList = new String[] {"user1", "user2", "user3", "user4"};

        Mockito.when(userClientService.getOnlineUserList(ArgumentMatchers.anyInt())).thenReturn(expectedUserMapList);

        String[] userIdArray = userService.getOnlineUsers(ArgumentMatchers.anyInt());

        Truth.assertThat(userIdArray).isEqualTo(expectedUserIdList);

        Mockito.verify(appContactService, Mockito.times(expectedUserIdList.length)).upsert(ArgumentMatchers.any());
    }
}
