package com.applozic.mobicomkit.channel.service;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import com.applozic.mobicomkit.annotations.ApplozicInternal;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.UserService;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.service.ConversationService;
import com.applozic.mobicomkit.api.notification.MuteNotificationRequest;
import com.applozic.mobicomkit.api.people.AlGetPeopleTask;
import com.applozic.mobicomkit.api.people.ChannelInfo;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.cache.MessageSearchCache;
import com.applozic.mobicomkit.channel.database.ChannelDatabaseService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.contact.BaseContactService;
import com.applozic.mobicomkit.feed.AlResponse;
import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicomkit.feed.ChannelFeed;
import com.applozic.mobicomkit.feed.ChannelFeedApiResponse;
import com.applozic.mobicomkit.feed.ChannelFeedListResponse;
import com.applozic.mobicomkit.feed.ChannelUsersFeed;
import com.applozic.mobicomkit.feed.GroupInfoUpdate;
import com.applozic.mobicomkit.listners.AlChannelListener;
import com.applozic.mobicomkit.sync.SyncChannelFeed;
import com.applozic.mobicommons.ApplozicService;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelUserMapper;
import com.applozic.mobicommons.task.AlTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This handles all things {@link Channel}.
 *
 * <p>Methods of this class use the respective local database and client classes to
 * perform operations for Applozic Channels/Groups.
 * Consider this class your go-to for working with channels.</p>
 */
public class ChannelService {

    public static boolean isUpdateTitle = false;
    private static ChannelService channelService;
    public Context context; //ApplozicInternal: private
    private ChannelDatabaseService channelDatabaseService;
    private ChannelClientService channelClientService;
    private BaseContactService baseContactService;
    private UserService userService;
    private String loggedInUserId;

    private ChannelService(Context context) {
        this.context = ApplozicService.getContext(context);
        channelClientService = ChannelClientService.getInstance(ApplozicService.getContext(context));
        channelDatabaseService = ChannelDatabaseService.getInstance(ApplozicService.getContext(context));
        userService = UserService.getInstance(ApplozicService.getContext(context));
        baseContactService = new AppContactService(ApplozicService.getContext(context));
        loggedInUserId = MobiComUserPreference.getInstance(context).getUserId();
    }

    public synchronized static ChannelService getInstance(Context context) {
        if (channelService == null) {
            channelService = new ChannelService(ApplozicService.getContext(context));
        }
        return channelService;
    }

    @VisibleForTesting
    public void setChannelClientService(ChannelClientService channelClientService) {
        this.channelClientService = channelClientService;
    }

    @VisibleForTesting
    public void setChannelDatabaseService(ChannelDatabaseService channelDatabaseService) {
        this.channelDatabaseService = channelDatabaseService;
    }

    @VisibleForTesting
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @VisibleForTesting
    public void setContactService(AppContactService appContactService) {
        this.baseContactService = appContactService;
    }

    @ApplozicInternal
    public Channel getChannelInfoFromLocalDb(Integer key) {
        return channelDatabaseService.getChannelByChannelKey(key);
    }

    /**
     * Gets a channel object for the given channel key.
     *
     * <p>Channel is initially retrieved locally. If it's not found
     * then a server call is done and the channel is synced locally and returned.</p>
     *
     * @param key the channel key
     * @return the channel object
     */
    public Channel getChannelInfo(Integer key) {
        if (key == null) {
            return null;
        }
        Channel channel = channelDatabaseService.getChannelByChannelKey(key);
        if (channel == null) {
            ChannelFeed channelFeed = channelClientService.getChannelInfo(key);
            if (channelFeed != null) {
                channelFeed.setUnreadCount(0);
                ChannelFeed[] channelFeeds = new ChannelFeed[1];
                channelFeeds[0] = channelFeed;
                processChannelFeedList(channelFeeds, false);
                channel = getChannel(channelFeed);
                return channel;
            }
        }
        return channel;
    }

    public static void clearInstance() {
        channelService = null;
    }

    @ApplozicInternal //Cleanup: can be removed
    public String getLoggedInUserId() {
        return loggedInUserId;
    }

    /**
     * Gets a channel object for the given client group id.
     *
     * <p>Channel is initially retrieved locally. If it's not found
     * then a server call is done and the channel is synced locally and returned.</p>
     *
     * @param clientGroupId the client group id
     * @return the channel object
     */
    public Channel getChannelInfo(String clientGroupId) {
        if (TextUtils.isEmpty(clientGroupId)) {
            return null;
        }
        Channel channel = channelDatabaseService.getChannelByClientGroupId(clientGroupId);
        if (channel == null) {
            ChannelFeed channelFeed = channelClientService.getChannelInfo(clientGroupId);
            if (channelFeed != null) {
                channelFeed.setUnreadCount(0);
                ChannelFeed[] channelFeeds = new ChannelFeed[1];
                channelFeeds[0] = channelFeed;
                processChannelFeedList(channelFeeds, false);
                channel = getChannel(channelFeed);
                return channel;
            }
        }
        return channel;
    }

    @ApplozicInternal
    public void createMultipleChannels(List<ChannelInfo> channelInfo) {
        List<ChannelFeed> channelFeeds = channelClientService.createMultipleChannels(channelInfo);
        if (channelFeeds != null) {
            processChannelList(channelFeeds);
        }
    }

    @ApplozicInternal //ApplozicInternal: or rename
    public void processChannelFeedList(ChannelFeed[] channelFeeds, boolean isUserDetails) {
        if (channelFeeds != null && channelFeeds.length > 0) {
            for (ChannelFeed channelFeed : channelFeeds) {
                processChannelFeed(channelFeed, isUserDetails);
            }
        }
    }

    //ApplozicInternal: private
    public void processChannelFeed(ChannelFeed channelFeed, boolean isUserDetails) {
        if (channelFeed != null) {
            Set<String> memberUserIds = null;
            if (channelFeed.getMembersName() != null) {
                memberUserIds = channelFeed.getMembersName();
            } else {
                memberUserIds = channelFeed.getContactGroupMembersId();
            }

            Channel channel = getChannel(channelFeed);
            if (channelDatabaseService.isChannelPresent(channel.getKey())) {
                channelDatabaseService.updateChannel(channel);
            } else {
                channelDatabaseService.addChannel(channel);
            }
            if (channelFeed.getConversationPxy() != null) {
                channelFeed.getConversationPxy().setGroupId(channelFeed.getId());
                ConversationService.getInstance(context).addConversation(channelFeed
                        .getConversationPxy());
            }
            if (memberUserIds != null && memberUserIds.size() > 0) {
                for (String userId : memberUserIds) {
                    ChannelUserMapper channelUserMapper = new ChannelUserMapper(channelFeed.getId
                            (), userId);
                    channelUserMapper.setParentKey(channelFeed.getParentKey());
                    if (channelDatabaseService.isChannelUserPresent(channelFeed.getId(), userId)) {
                        channelDatabaseService.updateChannelUserMapper(channelUserMapper);
                    } else {
                        channelDatabaseService.addChannelUserMapper(channelUserMapper);
                    }
                }
            }

            if (isUserDetails) {
                userService.processUserDetail(channelFeed.getUsers());
            }

            if (channelFeed.getGroupUsers() != null && channelFeed.getGroupUsers().size() > 0) {
                for (ChannelUsersFeed channelUsers : channelFeed.getGroupUsers()) {
                    if (channelUsers.getRole() != null) {
                        channelDatabaseService.updateRoleInChannelUserMapper(channelFeed.getId(),
                                channelUsers.getUserId(), channelUsers.getRole());
                    }
                }
            }

            if (channelFeed.getChildKeys() != null && channelFeed.getChildKeys().size() > 0) {
                processChildGroupKeys(channelFeed.getChildKeys());
            }
        }
    }

    @ApplozicInternal //Cleanup: can be removed
    public synchronized Channel getChannelByChannelKey(Integer channelKey) {
        if (channelKey == null) {
            return null;
        }
        return channelDatabaseService.getChannelByChannelKey(channelKey);
    }

    @ApplozicInternal //Cleanup: can be removed
    public List<ChannelUserMapper> getListOfUsersFromChannelUserMapper(Integer channelKey) {
        return channelDatabaseService.getChannelUserList(channelKey);
    }

    /**
     * Gets the Channel object for the given channel key.
     *
     * @param channelKey the channel key
     * @return the channel object (one with just the channel key if no channel is found)
     */
    public Channel getChannel(Integer channelKey) {
        Channel channel;
        channel = MessageSearchCache.getChannelByKey(channelKey);
        if (channel == null) {
            channel = channelDatabaseService.getChannelByChannelKey(channelKey);
        }
        if (channel == null) {
            channel = new Channel(channelKey);
        }
        return channel;
    }

    @ApplozicInternal
    public void updateChannel(Channel channel) {
        if (channelDatabaseService.getChannelByChannelKey(channel.getKey()) == null) {
            channelDatabaseService.addChannel(channel);
        } else {
            channelDatabaseService.updateChannel(channel);
        }
    }

    @ApplozicInternal //Cleanup: can be removed, code duplication for public api
    public List<Channel> getChannelList() {
        return channelDatabaseService.getAllChannels();
    }

    @ApplozicInternal
    public synchronized void syncChannels(boolean isMetadataUpdate) {
        final MobiComUserPreference userpref = MobiComUserPreference.getInstance(context);
        SyncChannelFeed syncChannelFeed = channelClientService.getChannelFeed(userpref
                .getChannelSyncTime());
        if (syncChannelFeed == null) {
            return;
        }
        if (syncChannelFeed.isSuccess()) {
            processChannelList(syncChannelFeed.getResponse());

            BroadcastService.sendChannelSyncBroadcastUpdate(context, isMetadataUpdate);
        }
        userpref.setChannelSyncTime(syncChannelFeed.getGeneratedAt());

    }

    @ApplozicInternal
    public synchronized void syncChannels() {
        syncChannels(false);
    }

    /**
     * @deprecated {@link AlResponse} is not longer used. It will be replaced by
     * {@link ApiResponse}.
     * Use {@link ChannelService#createChannelWithResponse(ChannelInfo)} instead.
     */
    @Deprecated
    @ApplozicInternal
    public synchronized AlResponse createChannel(final ChannelInfo channelInfo) {

        if (channelInfo == null) {
            return null;
        }

        AlResponse alResponse = new AlResponse();
        ChannelFeedApiResponse channelFeedResponse = null;

        try {
            channelFeedResponse = channelClientService.createChannelWithResponse(channelInfo);

            if (channelFeedResponse == null) {
                return null;
            }

            if (channelFeedResponse.isSuccess()) {
                alResponse.setStatus(AlResponse.SUCCESS);
                ChannelFeed channelFeed = channelFeedResponse.getResponse();

                if (channelFeed != null) {
                    ChannelFeed[] channelFeeds = new ChannelFeed[1];
                    channelFeeds[0] = channelFeed;
                    processChannelFeedList(channelFeeds, true);
                    alResponse.setResponse(getChannel(channelFeed));
                }
            } else {
                alResponse.setStatus(AlResponse.ERROR);
                alResponse.setResponse(channelFeedResponse.getErrorResponse());
            }
        } catch (Exception e) {
            alResponse.setStatus(AlResponse.ERROR);
            alResponse.setException(e);
        }

        return alResponse;
    }

    @ApplozicInternal
    public Channel getChannel(ChannelFeed channelFeed) {
        Channel channel = new Channel(channelFeed.getId(), channelFeed.getName(), channelFeed
                .getAdminName(), channelFeed.getType(), channelFeed.getUnreadCount(), channelFeed
                .getImageUrl());
        channel.setClientGroupId(channelFeed.getClientGroupId());
        channel.setNotificationAfterTime(channelFeed.getNotificationAfterTime());
        channel.setDeletedAtTime(channelFeed.getDeletedAtTime());
        channel.setMetadata(channelFeed.getMetadata());
        channel.setParentKey(channelFeed.getParentKey());
        channel.setParentClientGroupId(channelFeed.getParentClientGroupId());
        channel.setKmStatus(channel.generateKmStatus(loggedInUserId));
        return channel;
    }

    /**
     * Removes a given user from a channel.
     *
     * <p>The local database is also updated.</p>
     *
     * @param channelKey the channel key (to identify the channel)
     * @param userId the id of the user to add to the channels
     * @return the {@link ApiResponse} for the request
     */
    public String removeMemberFromChannelProcess(Integer channelKey, String userId) {
        if (channelKey == null && TextUtils.isEmpty(userId)) {
            return "";
        }
        ApiResponse apiResponse = channelClientService.removeMemberFromChannel(channelKey, userId);
        if (apiResponse == null) {
            return null;
        }
        if (apiResponse.isSuccess()) {
            channelDatabaseService.removeMemberFromChannel(channelKey, userId);
        }
        return apiResponse.getStatus();
    }

    /**
     * Sends a server request to remove a given user from a channel.
     *
     * <p>The local database is also updated.</p>
     *
     * @param clientGroupId the client group id (to identify the channel)
     * @param userId the id of the user to remove from the channel
     * @return the {@link ApiResponse} for the request
     */
    public String removeMemberFromChannelProcess(String clientGroupId, String userId) {
        if (clientGroupId == null && TextUtils.isEmpty(userId)) {
            return "";
        }
        ApiResponse apiResponse = channelClientService.removeMemberFromChannel(clientGroupId,
                userId);
        if (apiResponse == null) {
            return null;
        }
        if (apiResponse.isSuccess()) {
            channelDatabaseService.removeMemberFromChannel(clientGroupId, userId);
        }
        return apiResponse.getStatus();

    }

    @Deprecated
    @ApplozicInternal
    public String addMemberToChannelProcess(Integer channelKey, String userId) {
        if (channelKey == null && TextUtils.isEmpty(userId)) {
            return "";
        }
        ApiResponse apiResponse = channelClientService.addMemberToChannel(channelKey, userId);
        if (apiResponse == null) {
            return null;
        }
        if (apiResponse.isSuccess()) {
            ChannelUserMapper channelUserMapper = new ChannelUserMapper(channelKey, userId);
            channelDatabaseService.addChannelUserMapper(channelUserMapper);
        }
        return apiResponse.getStatus();
    }

    @Deprecated
    @ApplozicInternal
    public String addMemberToChannelProcess(String clientGroupId, String userId) {
        if (TextUtils.isEmpty(clientGroupId) && TextUtils.isEmpty(userId)) {
            return "";
        }
        ApiResponse apiResponse = channelClientService.addMemberToChannel(clientGroupId, userId);
        if (apiResponse == null) {
            return null;
        }
        return apiResponse.getStatus();
    }

    /**
     * Adds a given user to a channel.
     *
     * <p>The local database is also updated.</p>
     *
     * @param clientGroupId the client group id (to identify the channel)
     * @param userId the id of the user to add to the channel
     * @return the {@link ApiResponse} for the request
     */
    public ApiResponse addMemberToChannelProcessWithResponse(String clientGroupId, String userId) {
        if (TextUtils.isEmpty(clientGroupId) && TextUtils.isEmpty(userId)) {
            return null;
        }
        ApiResponse apiResponse = channelClientService.addMemberToChannel(clientGroupId, userId);
        if (apiResponse == null) {
            return null;
        }
        return apiResponse;
    }

    /**
     * Adds a given user to a channel.
     *
     * <p>The local database is also updated.</p>
     *
     * @param channelKey the channel key (to identify the channel)
     * @param userId the id of the user to add to the channel
     * @return the {@link ApiResponse} for the request
     */
    public ApiResponse addMemberToChannelProcessWithResponse(Integer channelKey, String userId) {
        if (channelKey == null && TextUtils.isEmpty(userId)) {
            return null;
        }
        ApiResponse apiResponse = channelClientService.addMemberToChannel(channelKey, userId);
        if (apiResponse == null) {
            return null;
        }
        if (apiResponse.isSuccess()) {
            ChannelUserMapper channelUserMapper = new ChannelUserMapper(channelKey, userId);
            channelDatabaseService.addChannelUserMapper(channelUserMapper);
        }
        return apiResponse;
    }

    /**
     * Adds a given user to multiple channels.
     *
     * <p>The local database is NOT updated.</p>
     *
     * @param clientGroupIds the list of client group ids (to identify the channels)
     * @param userId the id of the user to add to the channels
     * @return the {@link ApiResponse} for the request
     */
    public String addMemberToMultipleChannelsProcess(Set<String> clientGroupIds, String userId) {
        if (clientGroupIds == null && TextUtils.isEmpty(userId)) {
            return "";
        }
        ApiResponse apiResponse = channelClientService
                .addMemberToMultipleChannelsByClientGroupIds(clientGroupIds, userId);
        if (apiResponse == null) {
            return null;
        }
        return apiResponse.getStatus();
    }

    /**
     * Adds a given user to one or more channels.
     *
     * <p>The local database is NOT updated.</p>
     *
     * @param channelKeys the list of channel keys (to identify the channels)
     * @param userId the id of the user to add to the channels
     * @return the {@link ApiResponse} for the request
     */
    public String addMemberToMultipleChannelsProcessByChannelKeys(Set<Integer> channelKeys,
                                                                  String userId) {
        if (channelKeys == null && TextUtils.isEmpty(userId)) {
            return "";
        }
        ApiResponse apiResponse = channelClientService.addMemberToMultipleChannelsByChannelKey
                (channelKeys, userId);
        if (apiResponse == null) {
            return null;
        }
        return apiResponse.getStatus();
    }

    @ApplozicInternal
    public String leaveMemberFromChannelProcess(String clientGroupId, String userId) {
        if (TextUtils.isEmpty(clientGroupId)) {
            return "";
        }
        ApiResponse apiResponse = channelClientService.leaveMemberFromChannel(clientGroupId);
        if (apiResponse == null) {
            return null;
        }
        if (apiResponse.isSuccess()) {
            channelDatabaseService.leaveMemberFromChannel(clientGroupId, userId);
        }
        return apiResponse.getStatus();
    }

    @ApplozicInternal
    public String leaveMemberFromChannelProcess(Integer channelKey, String userId) {
        if (channelKey == null) {
            return "";
        }
        ApiResponse apiResponse = channelClientService.leaveMemberFromChannel(channelKey);
        if (apiResponse == null) {
            return null;
        }
        if (apiResponse.isSuccess()) {
            channelDatabaseService.leaveMemberFromChannel(channelKey, userId);
        }
        return apiResponse.getStatus();
    }

    /**
     * Updates the channel details, remotely and locally.
     *
     * <p>See {@link GroupInfoUpdate} for details on parameters.</p>
     *
     * @param groupInfoUpdate the object used to store data to update
     * @return the {@link ApiResponse} for the request
     */
    public String updateChannel(GroupInfoUpdate groupInfoUpdate) {
        if (groupInfoUpdate == null) {
            return null;
        }
        ApiResponse apiResponse = channelClientService.updateChannel(groupInfoUpdate);
        if (apiResponse == null) {
            return null;
        }
        if (apiResponse.isSuccess()) {
            channelDatabaseService.updateChannel(groupInfoUpdate);
        }
        return apiResponse.getStatus();
    }

    @ApplozicInternal
    public synchronized String createConversation(Integer groupId, String userId, String agentId,
                                                  String applicationId) {
        return channelClientService.createConversation(groupId, userId, agentId, applicationId);
    }

    @ApplozicInternal
    public synchronized void processChannelList(List<ChannelFeed> channelFeedList) {
        if (channelFeedList != null && channelFeedList.size() > 0) {
            for (ChannelFeed channelFeed : channelFeedList) {
                processChannelFeedForSync(channelFeed);
            }
        }
    }

    @ApplozicInternal
    public ChannelUserMapper getChannelUserMapper(Integer channelKey) {
        return channelDatabaseService.getChannelUserByChannelKey(channelKey);
    }

    /**
     * Updates the role for a user in a channel.
     *
     * <p>A role type of `1` is for an admin. A role type of `0` is for a non-admin.</p>
     *
     * @param channelKey the channel key
     * @param userId the user id whose role needs to be changed
     * @param role the role type integer
     * @return
     */
    public boolean updateRoleForUserInChannel(@NonNull Integer channelKey, @NonNull String userId, @NonNull Integer role) {
        GroupInfoUpdate groupInfoUpdate = new GroupInfoUpdate(channelKey);
        List<ChannelUsersFeed> channelUsersFeedList = new ArrayList<>();
        ChannelUsersFeed channelUsersFeed = new ChannelUsersFeed();
        channelUsersFeed.setUserId(userId);
        channelUsersFeed.setRole(role);
        channelUsersFeedList.add(channelUsersFeed);
        groupInfoUpdate.setUsers(channelUsersFeedList);
        if (groupInfoUpdate != null) {
            String response = channelService.updateChannel(groupInfoUpdate);
            if (!TextUtils.isEmpty(response) && MobiComKitConstants.SUCCESS.equals(response)) {
                channelService.updateRoleInChannelUserMapper(channelKey, userId, role);
                return true;
            }
        }
        return false;
    }

    @ApplozicInternal //Cleanup: remove, code duplication
    public void updateRoleInChannelUserMapper(Integer channelKey, String userId, Integer role) {
        channelDatabaseService.updateRoleInChannelUserMapper(channelKey, userId, role);
    }

    @ApplozicInternal //Cleanup: remove, code duplication
    public ChannelUserMapper getChannelUserMapperByUserId(Integer channelKey, String userId) {
        return channelDatabaseService.getChannelUserByChannelKeyAndUserId(channelKey, userId);
    }

    @ApplozicInternal //Cleanup: remove, code duplication
    public synchronized boolean processIsUserPresentInChannel(Integer channelKey) {
        return channelDatabaseService.isChannelUserPresent(channelKey, MobiComUserPreference
                .getInstance(context).getUserId());
    }

    @ApplozicInternal //Cleanup: remove, code duplication
    public synchronized boolean isUserAlreadyPresentInChannel(Integer channelKey, String userId) {
        return channelDatabaseService.isChannelUserPresent(channelKey, userId);
    }

    @ApplozicInternal
    public synchronized boolean processIsUserPresentInChannel(String clientGroupId) {
        Channel channel = channelDatabaseService.getChannelByClientGroupId(clientGroupId);
        return channelDatabaseService.isChannelUserPresent(channel.getKey(),
                MobiComUserPreference.getInstance(context).getUserId());
    }

    @ApplozicInternal
    public synchronized boolean isUserAlreadyPresentInChannel(String clientGroupId, String userId) {
        Channel channel = channelDatabaseService.getChannelByClientGroupId(clientGroupId);
        return channelDatabaseService.isChannelUserPresent(channel.getKey(), userId);
    }

    @ApplozicInternal
    public synchronized String processChannelDeleteConversation(Channel channel, Context context) {
        String response = new MobiComConversationService(context).deleteSync(null, channel, null);
        if (!TextUtils.isEmpty(response) && "success".equals(response)) {
            channelDatabaseService.deleteChannelUserMappers(channel.getKey());
            channelDatabaseService.deleteChannel(channel.getKey());
        }
        return response;

    }

    @ApplozicInternal
    public void updateChannelLocalImageURI(Integer channelKey, String localImageURI) {
        channelDatabaseService.updateChannelLocalImageURI(channelKey, localImageURI);
    }

    /**
     * Mutes notifications for a channel.
     *
     * <p>A muted channel will not receive push-notifications from the server.</p>
     *
     * <p>Request parameters can be passed inside the {@link MuteNotificationRequest} object.
     * Pass non-null/non-zero values for either {@link MuteNotificationRequest#setClientGroupId(String)}
     * or {@link MuteNotificationRequest#setId(Integer)}.
     * See {@link MuteNotificationRequest} for more details.</p>
     *
     * @param muteNotificationRequest the mute notification parameter object
     * @return the {@link ApiResponse} for the request
     */
    public ApiResponse muteNotifications(MuteNotificationRequest muteNotificationRequest) {

        ApiResponse apiResponse = channelClientService.muteNotification(muteNotificationRequest);

        if (apiResponse == null) {
            return null;
        }
        if (apiResponse.isSuccess()) {
            channelDatabaseService.updateNotificationAfterTime(muteNotificationRequest.getId(),
                    muteNotificationRequest.getNotificationAfterTime());
        }
        return apiResponse;
    }

    /**
     * Updates the time after which notifications should be received for a muted channel.
     *
     * @param groupId the group id
     * @param notificationAfterTime the time in milliseconds
     */
    @ApplozicInternal //Cleanup: this is not required. directly use channel database method
    public void updateNotificationAfterTime(Integer groupId, Long notificationAfterTime) {
        if (notificationAfterTime != null && groupId != null) {
            channelDatabaseService.updateNotificationAfterTime(groupId,
                    notificationAfterTime);
        }
    }

    /**
     * Will get a channel object from the database, corresponding to the passed client group id.
     *
     * @param clientGroupId the client group id. used to identify a channel (similar to with group id/channel key)
     * @return the channel object, null if no such channel exists in database
     */
    public Channel getChannelByClientGroupId(String clientGroupId) {
        if (TextUtils.isEmpty(clientGroupId)) {
            return null;
        }
        return channelDatabaseService.getChannelByClientGroupId(clientGroupId);
    }

    /**
     * Creates a new Applozic {@link Channel}.
     *
     * <p>Details on the creation are passed with the help of the {@link ChannelInfo} object.
     * Information about the newly created channel can be retrieved from {@link ChannelFeedApiResponse#getResponse()}.</p>
     *
     * @param channelInfo the channel info object
     * @return the {@link ChannelFeedApiResponse} for the request.
     */
    public ChannelFeedApiResponse createChannelWithResponse(ChannelInfo channelInfo) {
        ChannelFeedApiResponse channelFeedApiResponse = channelClientService
                .createChannelWithResponse(channelInfo);
        if (channelFeedApiResponse == null) {
            return null;
        }
        if (channelFeedApiResponse.isSuccess()) {
            ChannelFeed channelFeed = channelFeedApiResponse.getResponse();
            if (channelFeed != null) {
                ChannelFeed[] channelFeeds = new ChannelFeed[1];
                channelFeeds[0] = channelFeed;
                processChannelFeedList(channelFeeds, true);
            }
        }
        return channelFeedApiResponse;
    }

    /**
     * Adds a given user to a channel.
     *
     * <p>Data for this addition is updated both remotely and locally.</p>
     *
     * @param channelKey the channel key (to identify the channel)
     * @param userId the id of the user to add to the channel
     * @return the {@link ApiResponse} for the request
     */
    public ApiResponse addMemberToChannelWithResponseProcess(Integer channelKey, String userId) {
        if (channelKey == null && TextUtils.isEmpty(userId)) {
            return null;
        }
        ApiResponse apiResponse = channelClientService.addMemberToChannel(channelKey, userId);
        if (apiResponse == null) {
            return null;
        }
        if (apiResponse.isSuccess()) {
            ChannelUserMapper channelUserMapper = new ChannelUserMapper(channelKey, userId);
            channelDatabaseService.addChannelUserMapper(channelUserMapper);
        }
        return apiResponse;
    }

    @ApplozicInternal
    public String getGroupOfTwoReceiverUserId(Integer channelKey) {
        return channelDatabaseService.getGroupOfTwoReceiverId(channelKey);
    }

    @Deprecated
    @ApplozicInternal
    public Channel createGroupOfTwo(ChannelInfo channelInfo) {
        if (channelInfo == null) {
            return null;
        }
        if (!TextUtils.isEmpty(channelInfo.getClientGroupId())) {
            Channel channel = channelDatabaseService.getChannelByClientGroupId(channelInfo
                    .getClientGroupId());
            if (channel != null) {
                return channel;
            } else {
                ChannelFeedApiResponse channelFeedApiResponse = channelClientService
                        .createChannelWithResponse(channelInfo);
                if (channelFeedApiResponse == null) {
                    return null;
                }
                if (channelFeedApiResponse.isSuccess()) {
                    ChannelFeed channelFeed = channelFeedApiResponse.getResponse();
                    if (channelFeed != null) {
                        ChannelFeed[] channelFeeds = new ChannelFeed[1];
                        channelFeeds[0] = channelFeed;
                        processChannelFeedList(channelFeeds, true);
                        return getChannel(channelFeed);
                    }
                } else {
                    ChannelFeed channelFeed = channelClientService.getChannelInfo(channelInfo
                            .getClientGroupId());
                    if (channelFeed != null) {
                        ChannelFeed[] channelFeeds = new ChannelFeed[1];
                        channelFeeds[0] = channelFeed;
                        processChannelFeedList(channelFeeds, false);
                        return getChannel(channelFeed);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Create a {@link Channel.GroupType#GROUPOFTWO} channel.
     *
     * <p>A group-of-two type channel is implemented in the UI to look like a 1-to-1 chat.
     * It is meant to have two members only.</p>
     *
     * @param channelInfo the channel info that will be used to create the channel
     * @return the {@link AlResponse} from the server
     */
    public AlResponse createGroupOfTwoWithResponse(ChannelInfo channelInfo) {
        if (channelInfo == null) {
            return null;
        }

        AlResponse alResponse = new AlResponse();

        if (!TextUtils.isEmpty(channelInfo.getClientGroupId())) {
            Channel channel = channelDatabaseService.getChannelByClientGroupId(channelInfo
                    .getClientGroupId());
            if (channel != null) {
                alResponse.setStatus(AlResponse.SUCCESS);
                alResponse.setResponse(channel);
            } else {
                ChannelFeedApiResponse channelFeedApiResponse = channelClientService
                        .createChannelWithResponse(channelInfo);
                if (channelFeedApiResponse == null) {
                    alResponse.setStatus(AlResponse.ERROR);
                } else {
                    if (channelFeedApiResponse.isSuccess()) {
                        ChannelFeed channelFeed = channelFeedApiResponse.getResponse();
                        if (channelFeed != null) {
                            ChannelFeed[] channelFeeds = new ChannelFeed[1];
                            channelFeeds[0] = channelFeed;
                            processChannelFeedList(channelFeeds, true);
                            alResponse.setStatus(AlResponse.SUCCESS);
                            alResponse.setResponse(getChannel(channelFeed));
                        }
                    } else {
                        ChannelFeed channelFeed = channelClientService.getChannelInfo(channelInfo
                                .getClientGroupId());
                        if (channelFeed != null) {
                            ChannelFeed[] channelFeeds = new ChannelFeed[1];
                            channelFeeds[0] = channelFeed;
                            processChannelFeedList(channelFeeds, false);
                            alResponse.setStatus(AlResponse.SUCCESS);
                            alResponse.setResponse(getChannel(channelFeed));
                        }
                    }
                }
            }
        }
        return alResponse;
    }

    @ApplozicInternal //Cleanup: remove
    public List<ChannelFeed> getGroupInfoFromGroupIds(List<String> groupIds) {
        return getGroupInfoFromGroupIds(groupIds, null);
    }

    @ApplozicInternal //Cleanup: remove
    public List<ChannelFeed> getGroupInfoFromClientGroupIds(List<String> clientGroupIds) {
        return getGroupInfoFromGroupIds(null, clientGroupIds);
    }

    @ApplozicInternal
    public List<String> getChildGroupKeys(Integer parentGroupKey) {
        return channelDatabaseService.getChildGroupIds(parentGroupKey);
    }

    //ApplozicInternal: private
    public List<ChannelFeed> getGroupInfoFromGroupIds(List<String> groupIds, List<String>
            clientGroupIds) {

        ChannelFeedListResponse channelFeedList = channelClientService.getGroupInfoFromGroupIds
                (groupIds, clientGroupIds);

        if (channelFeedList == null) {
            return null;
        }

        if (channelFeedList != null && ChannelFeedListResponse.SUCCESS.equals(channelFeedList
                .getStatus())) {
            processChannelFeedList(channelFeedList.getResponse().toArray(new
                    ChannelFeed[channelFeedList.getResponse().size()]), false);
        }

        return channelFeedList.getResponse();
    }

    /**
     * Adds the list of users to a contact group with given id.
     *
     * <p>If group is not present, it will be created.</p>
     *
     * @param contactGroupId the id of the contact
     * @param groupType the group type
     * @param contactGroupMemberList list of userIds of contacts to add in group
     * @return true if successfully added
     */
    public boolean addMemberToContactGroup(String contactGroupId, String groupType, List<String>
            contactGroupMemberList) {

        ApiResponse apiResponse = null;
        if (!TextUtils.isEmpty(contactGroupId) && contactGroupMemberList != null) {
            if (!TextUtils.isEmpty(groupType)) {
                apiResponse = channelClientService.addMemberToContactGroupOfType(contactGroupId,
                        groupType, contactGroupMemberList);

            } else {
                apiResponse = channelClientService.addMemberToContactGroup(contactGroupId,
                        contactGroupMemberList);
            }
        }

        if (apiResponse == null) {
            return false;
        }
        return apiResponse.isSuccess();
    }

    /**
     * Gets the users/contacts in the contact group for the passed id and group type.
     *
     * <p>Sends a request to get the contacts for a group, identified by it's contactGroupId and groupType.
     * The new contacts retrieved are also added/updated in the local database.</p>
     *
     * @param contactGroupId the contact group id
     * @param groupType the group type
     * @return {@link ChannelFeed}. User {@link ChannelFeed#getGroupUsers()} to get the contacts.
     */
    public ChannelFeed getMembersFromContactGroup(String contactGroupId, String groupType) {
        ChannelFeed channelFeed = null;
        if (!TextUtils.isEmpty(contactGroupId)) {
            if (!TextUtils.isEmpty(groupType)) {
                channelFeed = channelClientService.getMembersFromContactGroupOfType
                        (contactGroupId, groupType);
            } else {
                channelFeed = channelClientService.getMembersFromContactGroup(contactGroupId);
            }
        }
        if (channelFeed != null) {
            ChannelFeed[] channelFeeds = new ChannelFeed[1];
            channelFeeds[0] = channelFeed;
            processChannelFeedList(channelFeeds, false);
            UserService.getInstance(context).processUserDetails(channelFeed
                    .getContactGroupMembersId());
            return channelFeed;
        }
        return null;
    }

    //ApplozicInternal: private
    //Cleanup: remove
    public ChannelFeed[] getMembersFromContactGroupList(List<String> groupIdList, List<String>
            groupNames, String groupType) {
        List<ChannelFeed> channelFeedList;

        ChannelFeedListResponse channelFeedListResponse = channelClientService
                .getMemebersFromContactGroupIds(groupIdList, groupNames, groupType);
        if (channelFeedListResponse != null && channelFeedListResponse.getStatus().equals
                (ChannelFeedListResponse.SUCCESS)) {
            channelFeedList = channelFeedListResponse.getResponse();
            processChannelFeedList(channelFeedList.toArray(new ChannelFeed[channelFeedList.size()
                    ]), false);
            return channelFeedList.toArray(new ChannelFeed[channelFeedList.size()]);
        }

        return null;
    }

    /**
     * Sends a request to remove a user from a contact group with the given group type and group id.
     *
     * <p>Contacts for a user can be grouped together. Such a group is called a contact group.
     * Groups can have group types.</p>
     *
     * @param contactGroupId the id of the contact group
     * @param groupType the group type
     * @param userId id of contact to remove
     * @return the {@link ApiResponse} for the request, null in-case of exception
     */
    public ApiResponse removeMemberFromContactGroup(String contactGroupId, String groupType,
                                                    String userId) {
        ApiResponse apiResponse;
        if (!TextUtils.isEmpty(contactGroupId) && !TextUtils.isEmpty(userId)) {
            apiResponse = channelClientService.removeMemberFromContactGroupOfType(contactGroupId,
                    groupType, userId);
            return apiResponse;
        }
        return null;
    }

    @ApplozicInternal
    public void processChannelFeedForSync(ChannelFeed channelFeed) {
        if (channelFeed != null) {
            Set<String> memberUserIds = channelFeed.getMembersName();
            Set<String> userIds = new HashSet<>();
            Channel channel = getChannel(channelFeed);
            if (channelDatabaseService.isChannelPresent(channel.getKey())) {
                channelDatabaseService.updateChannel(channel);
                channelDatabaseService.deleteChannelUserMappers(channel.getKey());
            } else {
                channelDatabaseService.addChannel(channel);
            }
            if (memberUserIds != null && memberUserIds.size() > 0) {
                for (String userId : memberUserIds) {
                    ChannelUserMapper channelUserMapper = new ChannelUserMapper(channelFeed.getId
                            (), userId);
                    channelUserMapper.setParentKey(channelFeed.getParentKey());
                    channelDatabaseService.addChannelUserMapper(channelUserMapper);
                    if (!baseContactService.isContactExists(userId)) {
                        userIds.add(userId);
                    }
                }
                if (userIds != null && userIds.size() > 0) {
                    userService.processUserDetailsByUserIds(userIds);
                }
            }

            if (channelFeed.getGroupUsers() != null && channelFeed.getGroupUsers().size() > 0) {
                for (ChannelUsersFeed channelUsers : channelFeed.getGroupUsers()) {
                    if (channelUsers.getRole() != null) {
                        channelDatabaseService.updateRoleInChannelUserMapper(channelFeed.getId(),
                                channelUsers.getUserId(), channelUsers.getRole());
                    }
                }
            }

            if (channelFeed.getChildKeys() != null && channelFeed.getChildKeys().size() > 0) {
                processChildGroupKeysForChannelSync(channelFeed.getChildKeys());
            }
            if (channel.isDeleted() && ApplozicClient.getInstance(context).isSkipDeletedGroups()) {
                BroadcastService.sendConversationDeleteBroadcast(context, BroadcastService.INTENT_ACTIONS.DELETE_CONVERSATION.toString(), null, channel.getKey(), "success");
            }
        }
    }

    @ApplozicInternal
    public String deleteChannel(Integer channelKey) {
        return deleteChannel(channelKey, false, false);
    }

    /**
     * Deletes the channel with the given channel key.
     *
     * <p>The channel is first deleted from the server and then locally along with it's
     * member mappings.
     * A broadcast is also sent with intent action {@link BroadcastService.INTENT_ACTIONS#DELETE_CONVERSATION}.</p>
     *
     * @param channelKey the channel key
     * @param updateClientGroupId pass true if you want the client group id to be updated
     * @param resetCount pass true if you want to reset the unread count for the channel
     * @return success (ignore case) or failure (ignore case)
     */
    public String deleteChannel(Integer channelKey, boolean updateClientGroupId, boolean resetCount) {
        ApiResponse apiResponse = channelClientService.deleteChannel(channelKey, updateClientGroupId, resetCount);
        if (apiResponse != null && apiResponse.isSuccess()) {
            channelDatabaseService.deleteChannel(channelKey);
            channelDatabaseService.deleteChannelUserMappers(channelKey);
            BroadcastService.sendConversationDeleteBroadcast(context, BroadcastService.INTENT_ACTIONS.DELETE_CONVERSATION.toString(), null, channelKey, apiResponse.getStatus());
            return apiResponse.getStatus();
        } else {
            return null;
        }
    }

    private void processChildGroupKeys(Set<Integer> childGroupKeys) {
        for (Integer channelKey : childGroupKeys) {
            Channel channel = channelDatabaseService.getChannelByChannelKey(channelKey);
            if (channel == null) {
                ChannelFeed channelFeed = channelClientService.getChannelInfo(channelKey);
                if (channelFeed != null) {
                    processChannelFeed(channelFeed, false);
                }
            }
        }
    }

    private void processChildGroupKeysForChannelSync(Set<Integer> childGroupKeys) {
        for (Integer channelKey : childGroupKeys) {
            Channel channel = channelDatabaseService.getChannelByChannelKey(channelKey);
            if (channel == null) {
                ChannelFeed channelFeed = channelClientService.getChannelInfo(channelKey);
                if (channelFeed != null) {
                    processChannelFeedForSync(channelFeed);
                }
            }
        }
    }

    @ApplozicInternal
    public Integer getParentGroupKeyByClientGroupKey(String parentClientGroupKey) {
        return channelDatabaseService.getParentGroupKey(parentClientGroupKey);
    }

    //ApplozicInternal: private
    //Cleanup: remove
    public void getChannelByChannelKeyAsync(Integer groupId, AlChannelListener channelListener) {
        AlTask.execute(new AlGetPeopleTask(context, null, null, groupId, channelListener, null, null, this));
    }

    //ApplozicInternal: private
    //Cleanup: remove
    public void getChannelByClientKeyAsync(String clientChannelKey, AlChannelListener channelListener) {
        AlTask.execute(new AlGetPeopleTask(context, null, clientChannelKey, null, channelListener, null, null, this));
    }

    /**
     * The getAllChannelList method will return list of groups.
     *
     * @return List of Channel objects or empty list in case of error or no groups.
     */
    public List<Channel> getAllChannelList() {
        List<Channel> channelList = null;
        SyncChannelFeed syncChannelFeed = channelClientService.getChannelFeed(MobiComUserPreference.getInstance(context).getChannelListLastGeneratedAtTime());
        if (syncChannelFeed == null || !syncChannelFeed.isSuccess()) {
            return null;
        }
        List<ChannelFeed> channelFeeds = syncChannelFeed.getResponse();
        if (channelFeeds != null && !channelFeeds.isEmpty()) {
            processChannelFeedList(channelFeeds.toArray(new ChannelFeed[channelFeeds.size()
                    ]), false);
        }
        MobiComUserPreference.getInstance(context).setChannelListLastGeneratedAtTime(syncChannelFeed.getGeneratedAt());

        channelList = ChannelDatabaseService.getInstance(context).getAllChannels();
        if (channelList == null) {
            return new ArrayList<>();
        }
        return channelList;
    }

    /**
     * Checks if the user with the given userId has the role of admin in the given channel.
     *
     * @param userId the userId of the user
     * @param channel the channel object to check in
     * @return true if admin/false otherwise
     */
    public boolean isUserAdminInChannel(String userId, Channel channel) {
        if(channel == null) {
            return false;
        }

        List<ChannelUserMapper> updatedChannelUserMapperList = ChannelService.getInstance(context).getListOfUsersFromChannelUserMapper(channel.getKey());

        if(TextUtils.isEmpty(userId)) {
            return false;
        }

        for (ChannelUserMapper channelUserMapper : updatedChannelUserMapperList) {
            if(userId.equals(channelUserMapper.getUserKey())) {
                return ChannelUserMapper.UserRole.ADMIN.getValue() == channelUserMapper.getRole().intValue();
            }
        }

        return false;
    }

    /**
     * Gets the logged-in userId from {@link MobiComUserPreference#getUserId()}
     * and checks if that user has role admin in channel.
     *
     * @param channel the channel to check in
     * @return true if admin/false otherwise
     */
    public boolean isLoggedInUserAdminInChannel(Channel channel) {
        return isUserAdminInChannel(loggedInUserId, channel);
    }
}