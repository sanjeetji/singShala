package com.applozic.mobicomkit;

import android.app.ActivityManager;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.UserDetail;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.MessageClientService;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.api.conversation.service.ConversationService;
import com.applozic.mobicomkit.api.people.UserWorker;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.sync.SyncUserDetailsResponse;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;
import com.google.gson.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.exceptions.base.MockitoException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ConversationServiceTest {
    public Context context;
    public MobiComConversationService mobiComConversationService;

    @Mock
    public MessageClientService messageClientService;
    @Mock
    public MessageDatabaseService messageDatabaseService;
    @Mock
    public AppContactService appContactService;
    @Mock
    public ConversationService conversationService;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();

        MockitoAnnotations.openMocks(this);

        mobiComConversationService = new MobiComConversationService(context);
        mobiComConversationService.setContactService(appContactService);
        mobiComConversationService.setMessageClientService(messageClientService);
        mobiComConversationService.setMessageDatabaseService(messageDatabaseService);
        mobiComConversationService.setConversationService(conversationService);
    }

    @Test
    public void getLatestMessageListWithNoArgs() {
        try {
            Type type = new TypeToken<List<Message>>(){}.getType();
            List<Message> messageList = (List<Message>) GsonUtils.getObjectFromJson(MockedConstants.getLatestMessageListResponse_WithNoArgs, type);
            Mockito.when(messageDatabaseService.getMessages(null, null, null)).thenReturn(messageList);
            Assert.assertEquals(messageList, mobiComConversationService.getLatestMessagesGroupByPeople());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLatestMessageListWithWrongArgs() {
        try {
            Type type = new TypeToken<List<Message>>(){}.getType();
            List<Message> messageList = (List<Message>) GsonUtils.getObjectFromJson(MockedConstants.getLatestMessageListResponse_WithWrongArgs, type);
            Mockito.when(messageDatabaseService.getMessages(0011L, "nullnull", 12311)).thenReturn(messageList);
            Assert.assertEquals(messageList, mobiComConversationService.getLatestMessagesGroupByPeople(0011L, "nullnull", 12311));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLatestMessageListWithSearchString() {
        try {
            Type type = new TypeToken<List<Message>>(){}.getType();
            List<Message> messageList = (List<Message>) GsonUtils.getObjectFromJson(MockedConstants.getLatestMessageListResponse_WithSearchString, type);
            Mockito.when(messageDatabaseService.getMessages(null, "h", null)).thenReturn(messageList);
            Assert.assertEquals(messageList, mobiComConversationService.getLatestMessagesGroupByPeople(null, "h", null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getLatestMessageListThrowError() {

        final String demoExceptionMessage = "Demo Exception";

        class DemoException extends MockitoException {
            public DemoException(String message) {
                super(message);
            }

            @Override
            public String getMessage() {
                return demoExceptionMessage;
            }
        }

        try {
            Mockito.when(messageDatabaseService.getMessages(null, null, null)).thenThrow(new DemoException("Demo Exception"));
            mobiComConversationService.getLatestMessagesGroupByPeople();
        }catch (DemoException ex) {
            Assert.assertEquals(demoExceptionMessage, ex.getMessage());
        }
    }

    @Test
    public void getMessageListForOneToOneChat() {
        try {
            Type type = new TypeToken<List<Message>>(){}.getType();
            List<Message> messageList = (List<Message>) GsonUtils.getObjectFromJson(MockedConstants.getMessagesForContactResponse, type);
            Mockito.when(messageDatabaseService.getMessages(null, null, null, null, null)).thenReturn(messageList);
            Assert.assertEquals(messageList, mobiComConversationService.getMessages(null,  null, null, null, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void processUserDetails() {
        try {
            List<UserDetail> userDetails = new ArrayList<UserDetail>();
            userDetails.add(new UserDetail());
            SyncUserDetailsResponse response = new SyncUserDetailsResponse();
            response.setGeneratedAt("GeneratedAtString");
            response.setStatus("success");
            response.setResponse(userDetails);

            Mockito.when(messageClientService.getUserDetailsList(ArgumentMatchers.anyString())).thenReturn(response);
            Mockito.when(appContactService.getContactById("")).thenReturn(new Contact());
            Mockito.doNothing().when(appContactService).upsert(ArgumentMatchers.any(Contact.class));
            mobiComConversationService.processLastSeenAtStatus();

            Assert.assertEquals(response.getGeneratedAt(), MobiComUserPreference.getInstance(context).getLastSeenAtSyncTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testDeleteSyncWithNullArgsReturnEmptyResponse() {
        try {
            Assert.assertEquals("", mobiComConversationService.deleteSync(null, null, null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteSyncWithContactReturnSuccess() {
        try {
            Contact contact = new Contact();
            contact.setUserId("testUserId");

            Mockito.when(messageClientService.syncDeleteConversationThreadFromServer(contact, null)).thenReturn("success");

            Assert.assertEquals("success", mobiComConversationService.deleteSync(contact, null, null));

            // Check if method is called...
            Mockito.verify(messageDatabaseService, Mockito.times(1)).deleteConversation(ArgumentMatchers.anyString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteSyncWithChannelReturnSuccess() {
        try {
            Channel channel = new Channel();
            channel.setKey(000);
            Mockito.when(messageClientService.syncDeleteConversationThreadFromServer(null, channel)).thenReturn("success");

            Assert.assertEquals("success", mobiComConversationService.deleteSync(null, channel, null));

            Mockito.verify(messageDatabaseService, Mockito.times(1)).deleteChannelConversation(ArgumentMatchers.anyInt());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        try {
            wait(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.getClass())) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testReadConversation() {
        try {
            Assert.assertFalse(isMyServiceRunning(UserWorker.class, context));
            Mockito.when(appContactService.getContactById(ArgumentMatchers.anyString())).thenReturn(null);
            Mockito.when(messageDatabaseService.updateReadStatusForContact(ArgumentMatchers.anyString())).thenReturn(0);
            mobiComConversationService.read(null, null);

            Assert.assertTrue(isMyServiceRunning(UserWorker.class, context));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteMessageFromDeviceWithMessage() {
        try {
            Mockito.when(messageDatabaseService.getMessage(ArgumentMatchers.anyString())).thenReturn(new Message());

            mobiComConversationService.deleteMessageFromDevice("TestString", "000");
            Mockito.verify(messageDatabaseService, Mockito.times(1)).deleteMessage(ArgumentMatchers.any(Message.class), ArgumentMatchers.anyString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteMessageFromDeviceWithNullMessage() {
        try {
            Mockito.when(messageDatabaseService.getMessage(ArgumentMatchers.anyString())).thenReturn(null);
            Mockito.when(messageDatabaseService.deleteMessage(ArgumentMatchers.any(Message.class), ArgumentMatchers.anyString())).thenReturn(null);
            mobiComConversationService.deleteMessageFromDevice("TestString", "000");
            Mockito.verify(messageDatabaseService, Mockito.times(0)).deleteMessage(ArgumentMatchers.any(Message.class), ArgumentMatchers.anyString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteMessage() {
        try {
            Message message = (Message) GsonUtils.getObjectFromJson(MockedConstants.Message, Message.class);
            Mockito.when(messageDatabaseService.deleteMessage(ArgumentMatchers.any(Message.class), ArgumentMatchers.anyString())).thenReturn("checkMe");
            Mockito.when(messageClientService.deleteMessage(ArgumentMatchers.any(Message.class), ArgumentMatchers.nullable(Contact.class))).thenReturn("success");

            mobiComConversationService.deleteMessage(message, new Contact());

            Mockito.verify(messageDatabaseService, Mockito.times(1)).deleteMessage(ArgumentMatchers.any(Message.class), ArgumentMatchers.nullable(String.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mockSetFilePaths(Message mockMessage, final Message message) {
        Mockito.doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                ArrayList<String> arrayList = (ArrayList<String>) args[0];
                message.setFilePaths(arrayList);
                return null;
            }
        }).when(mockMessage).setFilePaths(ArgumentMatchers.any(List.class));
    }

    @Test
    public void testSetFilePathWithCorrectFilePath() {
        try {
            final Message message = (Message) GsonUtils.getObjectFromJson(MockedConstants.MessageWithFilePath, Message.class);
            message.setFilePaths(new ArrayList<String>());
            Assert.assertEquals(0, message.getFilePaths().size());
            Message mockMessage = Mockito.mock(Message.class);
            Mockito.when(mockMessage.getFileMetas()).thenReturn(message.getFileMetas());
            Mockito.when(mockMessage.getCreatedAtTime()).thenReturn(message.getCreatedAtTime());

            mockSetFilePaths(mockMessage, message);

            mobiComConversationService.setFilePathifExist(mockMessage);
            Assert.assertEquals(1, message.getFilePaths().size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetFilePathWithWrongFilePath() {
        try {
            Message message = (Message) GsonUtils.getObjectFromJson(MockedConstants.Message, Message.class);
            message.setFilePaths(new ArrayList<String>());
            Assert.assertEquals(0, message.getFilePaths().size());

            Message mockMessage = Mockito.mock(Message.class);
            Mockito.when(mockMessage.getFileMetas()).thenReturn(message.getFileMetas());
            Mockito.when(mockMessage.getCreatedAtTime()).thenReturn(message.getCreatedAtTime());

            mockSetFilePaths(mockMessage, message);

            mobiComConversationService.setFilePathifExist(mockMessage);

            Assert.assertEquals(0, message.getFilePaths().size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

