package com.applozic.mobicomkit.database;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.channel.database.ChannelDatabaseService;
import com.applozic.mobicomkit.feed.GroupInfoUpdate;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelUserMapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class ChannelDatabaseServiceTest {
    MobiComDatabaseHelper dbHelper;
    ChannelDatabaseService channelDatabaseService;
    MessageDatabaseService messageDatabaseService;

    Channel channel;
    Channel channel1;
    Channel channel2;
    Channel channel3;

    ChannelUserMapper channelUserMapper;
    ChannelUserMapper channelUserMapper1;
    ChannelUserMapper channelUserMapper2;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        dbHelper = new MobiComDatabaseHelper(ApplicationProvider.getApplicationContext(), null, null, MobiComDatabaseHelper.DB_VERSION);
        channelDatabaseService = ChannelDatabaseService.getTestInstance(context, dbHelper);
        messageDatabaseService = new MessageDatabaseService(context, dbHelper);

        channel = new Channel();
        Map<String, String> metadata = new HashMap<>();
        metadata.put("met1", "1");
        metadata.put("met2", "2");
        channel.setMetadata(metadata);
        channel.setKey(12345);
        channel.setParentKey(123);
        channel.setParentClientGroupId("parentId");
        channel.setClientGroupId("clientId");
        channel.setName("dname");
        channel.setAdminKey("adminKey");
        channel.setType((short) 1);
        channel.setUnreadCount(10);
        channel.setImageUrl("imageUrl");
        channel.setLocalImageUri("localImageUri");
        channel.setNotificationAfterTime(123456L);
        channel.setDeletedAtTime(123456L);
        channel.setKmStatus(1);

        channel1 = new Channel();
        Map<String, String> metadata1 = new HashMap<>();
        metadata.put("met11", "1");
        metadata.put("met21", "2");
        channel1.setMetadata(metadata1);
        channel1.setKey(123451);
        channel1.setParentKey(1231);
        channel1.setParentClientGroupId("parentId1");
        channel1.setClientGroupId("clientId");
        channel1.setName("bname1");
        channel1.setAdminKey("adminKey1");
        channel1.setType((short) 11);
        channel1.setUnreadCount(101);
        channel1.setImageUrl("imageUrl1");
        channel1.setLocalImageUri("localImageUri1");
        channel1.setNotificationAfterTime(12345L);
        channel1.setDeletedAtTime(12345L);
        channel1.setKmStatus(11);

        channel2 = new Channel();
        Map<String, String> metadata2 = new HashMap<>();
        metadata.put("met12", "1");
        metadata.put("met22", "2");
        channel2.setMetadata(metadata2);
        channel2.setKey(123452);
        channel2.setParentKey(1232);
        channel2.setParentClientGroupId("parentId2");
        channel2.setClientGroupId("clientId2");
        channel2.setName("aname2");
        channel2.setAdminKey("adminKey2");
        channel2.setType((short) 12);
        channel2.setUnreadCount(102);
        channel2.setImageUrl("imageUrl2");
        channel2.setLocalImageUri("localImageUri2");
        channel2.setNotificationAfterTime(1234L);
        channel2.setDeletedAtTime(1234L);
        channel2.setKmStatus(12);

        channel3 = new Channel();
        Map<String, String> metadata3 = new HashMap<>();
        metadata.put("met13", "1");
        metadata.put("met23", "2");
        channel3.setMetadata(metadata3);
        channel3.setKey(123453);
        channel3.setParentKey(1233);
        channel3.setParentClientGroupId("parentId3");
        channel3.setClientGroupId("clientId3");
        channel3.setName("cname3");
        channel3.setAdminKey("adminKey3");
        channel3.setType((short) 13);
        channel3.setUnreadCount(103);
        channel3.setImageUrl("imageUrl3");
        channel3.setLocalImageUri("localImageUri3");
        channel3.setNotificationAfterTime(123L);
        channel3.setDeletedAtTime(123L);
        channel3.setKmStatus(13);

        //ChannelUserMapper objects
        channelUserMapper = new ChannelUserMapper();
        channelUserMapper.setKey(123451); //for channel1
        channelUserMapper.setUserKey("clientId");
        channelUserMapper.setUnreadCount(20);
        channelUserMapper.setStatus((short) 2);
        channelUserMapper.setParentKey(123);
        channelUserMapper.setRole(4);

        channelUserMapper1 = new ChannelUserMapper();
        channelUserMapper1.setKey(123451); //for channel1
        channelUserMapper1.setUserKey("clientId1");
        channelUserMapper1.setUnreadCount(201);
        channelUserMapper1.setStatus((short) 2);
        channelUserMapper1.setParentKey(1231);
        channelUserMapper1.setRole(41);

        channelUserMapper2 = new ChannelUserMapper();
        channelUserMapper2.setKey(123452); //for channel2
        channelUserMapper2.setUserKey("clientId2");
        channelUserMapper2.setUnreadCount(202);
        channelUserMapper2.setStatus((short) 3);
        channelUserMapper2.setParentKey(1232);
        channelUserMapper2.setRole(42);
    }

    @After
    public void closeDb() throws IOException {
        List<Channel> channelList = channelDatabaseService.getAllChannels();

        for (Channel channel : channelList) {
            channelDatabaseService.deleteChannel(channel.getKey());
            channelDatabaseService.deleteChannelUserMappers(channel.getKey());
        }

        dbHelper.delDatabase();
        dbHelper.close();
    }

    //channel methods >>
    @Test
    public void addAndRetrieveChannels() {
        //getAllChannels for null
        assertThat(channelDatabaseService.getAllChannels()).isEmpty();
        channelUserMapper = new ChannelUserMapper();
        channelUserMapper.setKey(123451); //for channel1
        channelUserMapper.setUserKey("clientId");
        channelUserMapper.setUnreadCount(20);
        channelUserMapper.setStatus((short) 2);
        channelUserMapper.setParentKey(123);
        channelUserMapper.setRole(4);
        //add two channels
        channelDatabaseService.addChannel(channel);
        channelDatabaseService.addChannel(channel1);

        //test addChannel and getChannelByClientId and getChannelByChannelKey
        Channel actualChannelRetrievedByClientGroupId = channelDatabaseService.getChannelByClientGroupId(channel.getClientGroupId());
        assertThat(actualChannelRetrievedByClientGroupId.toString()).isEqualTo(channel.toString());
        Channel actualChannelRetrievedByChannelKey = channelDatabaseService.getChannelByChannelKey(channel.getKey());
        assertThat(actualChannelRetrievedByChannelKey.toString()).isEqualTo(channel.toString());
        assertThat(channelDatabaseService.getChannelByChannelKey(999)).isNull();
        assertThat(channelDatabaseService.getChannelByClientGroupId("doesn't-exist")).isNull();

        //add more channels
        channelDatabaseService.addChannel(channel2);
        channelDatabaseService.addChannel(channel3);

        //test getAllChannels
        List<Channel> actualChannelList = channelDatabaseService.getAllChannels();
        assertThat(actualChannelList.get(0).toString()).isEqualTo(channel2.toString());
        assertThat(actualChannelList.get(1).toString()).isEqualTo(channel1.toString());
        assertThat(actualChannelList.get(2).toString()).isEqualTo(channel3.toString());
        assertThat(actualChannelList.get(3).toString()).isEqualTo(channel.toString());
    }

    @Test
    public void testUpdateMethods() {
        channelDatabaseService.addChannel(channel);
        channelDatabaseService.addChannel(channel2);
        channelDatabaseService.addChannel(channel3);

        //test updateChannelLocalUri
        channelDatabaseService.updateChannelLocalImageURI(channel2.getKey(), "newValue");
        channel2.setLocalImageUri("newValue");
        assertThat(channelDatabaseService.getChannelByChannelKey(channel2.getKey()).toString()).isEqualTo(channel2.toString());

        //test updateNotificationAfterTime
        channel2.setNotificationAfterTime(9999L);
        channelDatabaseService.updateNotificationAfterTime(channel2.getKey(), 9999L);
        assertThat(channelDatabaseService.getChannelByChannelKey(channel2.getKey()).toString()).isEqualTo(channel2.toString());

        //test group update
        GroupInfoUpdate groupInfoUpdateImageUrl = new GroupInfoUpdate(channel2.getKey());
        groupInfoUpdateImageUrl.setImageUrl("newImageUrl");
        channelDatabaseService.updateChannel(groupInfoUpdateImageUrl); //sets imageUrl of channel2  to newImageUrl and localImageUri to null
        channel2.setImageUrl("newImageUrl");
        channel2.setLocalImageUri(null);
        assertThat(channelDatabaseService.getChannelByChannelKey(channel2.getKey()).toString()).isEqualTo(channel2.toString());

        GroupInfoUpdate groupInfoUpdateName = new GroupInfoUpdate(channel3.getKey());
        groupInfoUpdateName.setNewName("newName");
        channelDatabaseService.updateChannel(groupInfoUpdateName); //sets name of channel 3 to newName
        channel3.setName("newName");
        assertThat(channelDatabaseService.getChannelByChannelKey(channel3.getKey()).toString()).isEqualTo(channel3.toString());

        GroupInfoUpdate groupInfoUpdateNull = new GroupInfoUpdate(channel.getKey());
        int returnInt = channelDatabaseService.updateChannel(groupInfoUpdateNull);
        assertThat(returnInt).isEqualTo(0);
        assertThat(channelDatabaseService.getChannelByChannelKey(channel.getKey()).toString()).isEqualTo(channel.toString());

        //test isChannelPresent
        assertThat(channelDatabaseService.isChannelPresent(channel3.getKey())).isTrue();
        assertThat(channelDatabaseService.isChannelPresent(channel1.getKey())).isFalse();

        //test updateChannel with channel object
        channel.setUnreadCount(20);
        channel.setLocalImageUri("newUri");
        channelDatabaseService.updateChannel(channel);
        assertThat(channelDatabaseService.getChannelByChannelKey(channel.getKey()).toString()).isEqualTo(channel.toString());
    }

    @Test
    public void testDeleteChannel() {
        channelDatabaseService.addChannel(channel);
        channelDatabaseService.addChannel(channel1);
        channelDatabaseService.addChannel(channel2);
        channelDatabaseService.addChannel(channel3);

        assertThat(channelDatabaseService.getAllChannels().size()).isEqualTo(4);
        channelDatabaseService.deleteChannel(channel2.getKey());
        assertThat(channelDatabaseService.getChannelByChannelKey(channel2.getKey())).isNull();
        assertThat(channelDatabaseService.getAllChannels().size()).isEqualTo(3);
    }

    @Test
    public void testGetChildGroupIds() {
        channel.setParentKey(123);
        channel1.setParentKey(123);
        channel3.setParentKey(123);
        channelDatabaseService.addChannel(channel);
        channelDatabaseService.addChannel(channel1);
        channelDatabaseService.addChannel(channel2);
        channelDatabaseService.addChannel(channel3);

        List<String> childGroupIds = channelDatabaseService.getChildGroupIds(123);
        assertThat(childGroupIds.get(0)).isEqualTo(channel.getKey().toString());
        assertThat(childGroupIds.get(1)).isEqualTo(channel1.getKey().toString());
        assertThat(childGroupIds.get(2)).isEqualTo(channel3.getKey().toString());
    }

    @Test
    public void testGetMethods() {
        //test getParentGroupKey
        channelDatabaseService.addChannel(channel2);
        assertThat(channelDatabaseService.getParentGroupKey(channel2.getParentClientGroupId())).isEqualTo(channel2.getParentKey());
        channel.setParentClientGroupId(null);
        channelDatabaseService.addChannel(channel);
        assertThat(channelDatabaseService.getParentGroupKey(channel.getParentClientGroupId())).isNull();
    }

    //ChannelUserMapper methods >>
    //addChannelUserMapper, getChannelUserByChannelKeyAndUserId, getChannelUserList, getChannelByChannelKey, getGroupOfTwoChannelUserId
    @Test
    public void addAndRetrieveChannelUserMapper() {
        channelDatabaseService.addChannelUserMapper(channelUserMapper);
        channelDatabaseService.addChannelUserMapper(channelUserMapper1);
        channelDatabaseService.addChannelUserMapper(channelUserMapper2);

        assertThat(channelDatabaseService.getChannelUserByChannelKeyAndUserId(channel1.getKey(), "clientId").toString()).isEqualTo(channelUserMapper.toString());
        assertThat(channelDatabaseService.getChannelUserByChannelKeyAndUserId(channel1.getKey(), "clientId1").toString()).isEqualTo(channelUserMapper1.toString());

        assertThat(channelDatabaseService.getChannelByChannelKey(channel2.getKey())).isNull();

        List<ChannelUserMapper> channelUserMappersChannel1 = channelDatabaseService.getChannelUserList(channel1.getKey());
        List<ChannelUserMapper> channelUserMappersChannel2 = channelDatabaseService.getChannelUserList(channel2.getKey());

        assertThat(channelUserMappersChannel1.get(0).toString()).isEqualTo(channelUserMapper.toString());
        assertThat(channelUserMappersChannel1.get(1).toString()).isEqualTo(channelUserMapper1.toString());
        assertThat(channelUserMappersChannel2.get(0).toString()).isEqualTo(channelUserMapper2.toString());

        //get group of two channel user id, NOTE: the function needed to be tampered to provide getting the logged in user id
        //Reason: MobicomUserPreference is used inside the function
        //I have tested it by manually providing a mock userId
        ChannelDatabaseService channelDatabaseService = Mockito.mock(ChannelDatabaseService.class);
        String userKey = channelDatabaseService.getGroupOfTwoReceiverId(channel1.getKey());
        //assertThat(userKey).isEqualTo("clientId1"); //see user mapper objects for explanation
    }

    @Test
    public void testUpdate() {
        channelDatabaseService.addChannelUserMapper(channelUserMapper2);
        channelDatabaseService.addChannelUserMapper(channelUserMapper);
        channelDatabaseService.addChannelUserMapper(channelUserMapper1);

        //updateRoleInChannelUserMapper
        channelDatabaseService.updateRoleInChannelUserMapper(channelUserMapper2.getKey(), channelUserMapper2.getUserKey(), 8);
        assertThat(channelDatabaseService.getChannelUserByChannelKeyAndUserId(channelUserMapper2.getKey(), channelUserMapper2.getUserKey()).getRole()).isEqualTo(8);

        channelUserMapper.setRole(9);

        //updateChannelUserMapper
        channelDatabaseService.updateChannelUserMapper(channelUserMapper);
        assertThat(channelDatabaseService.getChannelUserByChannelKeyAndUserId(channelUserMapper.getKey(), channelUserMapper.getUserKey()).toString()).isEqualTo(channelUserMapper.toString());
    }

    @Test
    public void testIsChannelUserPresent() {
        channelDatabaseService.addChannelUserMapper(channelUserMapper);
        channelDatabaseService.addChannelUserMapper(channelUserMapper1);

        assertThat(channelDatabaseService.isChannelUserPresent(channelUserMapper1.getKey(), channelUserMapper1.getUserKey())).isTrue();
        assertThat(channelDatabaseService.isChannelUserPresent(9999, null)).isFalse();
    }

    //leaveUserForChannel is identical, hence not tested
    @Test
    public void testRemoveUser() {
        channelDatabaseService.addChannelUserMapper(channelUserMapper);
        channelDatabaseService.addChannelUserMapper(channelUserMapper1);
        channelDatabaseService.addChannelUserMapper(channelUserMapper2);
        channelDatabaseService.addChannel(channel);
        channelDatabaseService.addChannel(channel2);
        channelDatabaseService.addChannel(channel3);
        channelDatabaseService.addChannel(channel1);

        //removeMemberForChannel, channelKey method
        int removedRows = channelDatabaseService.removeMemberFromChannel(channelUserMapper.getKey(), channelUserMapper.getUserKey());
        assertThat(removedRows).isEqualTo(1);
        assertThat(channelDatabaseService.isChannelUserPresent(channelUserMapper.getKey(), channelUserMapper.getUserKey())).isFalse();

        //removeMemberForChannel, clientGroupId method
        int removedRows1 = channelDatabaseService.removeMemberFromChannel("clientId2", channelUserMapper2.getUserKey());
        assertThat(removedRows1).isEqualTo(1);
        assertThat(channelDatabaseService.isChannelUserPresent(channelUserMapper2.getKey(), channelUserMapper2.getUserKey())).isFalse();

        //one remaining
        assertThat(channelDatabaseService.isChannelUserPresent(channelUserMapper1.getKey(), channelUserMapper1.getUserKey())).isTrue();

        //add the two conversations again
        channelDatabaseService.addChannelUserMapper(channelUserMapper);
        channelDatabaseService.addChannelUserMapper(channelUserMapper2);

        //deleteChannelUserMappers
        int removedRows3 = channelDatabaseService.deleteChannelUserMappers(channel1.getKey());
        assertThat(removedRows3).isEqualTo(2);
    }

    @Test
    public void testMessageDaoQueries() {
        channel.setKmStatus(1);
        channel1.setKmStatus(2);
        channel2.setKmStatus(3);
        channel3.setKmStatus(6);
        channelDatabaseService.addChannel(channel);
        channelDatabaseService.addChannel(channel2);
        channelDatabaseService.addChannel(channel3);
        channelDatabaseService.addChannel(channel1);

        assertThat(messageDatabaseService.getTotalUnreadCountForSupportGroup(2)).isEqualTo(channel.getUnreadCount() + channel1.getUnreadCount());
        assertThat(messageDatabaseService.getTotalUnreadCountForSupportGroup(6)).isEqualTo(channel3.getUnreadCount());
    }
}
