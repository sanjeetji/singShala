package com.applozic.mobicomkit.channel.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.database.MobiComDatabaseHelper;
import com.applozic.mobicomkit.feed.GroupInfoUpdate;
import com.applozic.mobicommons.ApplozicService;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelUserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages database operations related to {@link Channel} and {@link ChannelUserMapper}.
 *
 * <p>This class is responsible for handling database queries for operations
 * related to "groups".
 * Care must be taken when working with all database service classes.
 * Modifying the database might lead to unexpected changes in the working of the
 * SDK.</p>
 */
public class ChannelDatabaseService {

    private static final String TAG = "ChannelDatabaseService";
    private static final String CHANNEL = "channel";
    private static final String CHANNEL_USER_X = "channel_User_X";
    private static ChannelDatabaseService channelDatabaseService;
    private Context context;
    private MobiComUserPreference mobiComUserPreference;
    private MobiComDatabaseHelper dbHelper;

    private ChannelDatabaseService(Context context) {
        this.context = ApplozicService.getContext(context);
        this.mobiComUserPreference = MobiComUserPreference.getInstance(ApplozicService.getContext(context));
        this.dbHelper = MobiComDatabaseHelper.getInstance(ApplozicService.getContext(context));
    }

    @VisibleForTesting
    private ChannelDatabaseService(Context context, MobiComDatabaseHelper dbHelper) {
        this.context = ApplozicService.getContext(context);
        this.mobiComUserPreference = MobiComUserPreference.getInstance(ApplozicService.getContext(context));
        this.dbHelper = dbHelper;
    }

    public synchronized static ChannelDatabaseService getInstance(Context context) {
        if (channelDatabaseService == null) {
            channelDatabaseService = new ChannelDatabaseService(ApplozicService.getContext(context));
        }
        return channelDatabaseService;
    }

    @VisibleForTesting
    public synchronized static ChannelDatabaseService getTestInstance(Context context, MobiComDatabaseHelper dbHelper) {
        if (channelDatabaseService == null) {
            channelDatabaseService = new ChannelDatabaseService(ApplozicService.getContext(context), dbHelper);
        }
        return channelDatabaseService;
    }

    //ApplozicInternal: private
    public static ChannelUserMapper getChannelUser(Cursor cursor) {
        ChannelUserMapper channelUserMapper = new ChannelUserMapper();
        channelUserMapper.setUserKey(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.USERID)));
        channelUserMapper.setKey(cursor.getInt(cursor.getColumnIndex(MobiComDatabaseHelper.CHANNEL_KEY)));
        channelUserMapper.setUnreadCount(cursor.getShort(cursor.getColumnIndex(MobiComDatabaseHelper.UNREAD_COUNT)));
        channelUserMapper.setRole(cursor.getInt(cursor.getColumnIndex(MobiComDatabaseHelper.ROLE)));
        channelUserMapper.setStatus(cursor.getShort(cursor.getColumnIndex(MobiComDatabaseHelper.STATUS)));
        channelUserMapper.setParentKey(cursor.getInt(cursor.getColumnIndex(MobiComDatabaseHelper.PARENT_GROUP_KEY)));
        return channelUserMapper;
    }

    //ApplozicInternal: private
    public static List<ChannelUserMapper> getListOfUsers(Cursor cursor) {
        List<ChannelUserMapper> channelUserMapper = new ArrayList<ChannelUserMapper>();
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    channelUserMapper.add(getChannelUser(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return channelUserMapper;
    }

    //ApplozicInternal: default
    public void addChannel(Channel channel) {
        try {
            ContentValues contentValues = prepareChannelValues(channel);
            dbHelper.getWritableDatabase().insertWithOnConflict(CHANNEL, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    //ApplozicInternal: private
    public ContentValues prepareChannelValues(Channel channel) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MobiComDatabaseHelper.CHANNEL_DISPLAY_NAME, channel.getName());
        contentValues.put(MobiComDatabaseHelper.CHANNEL_KEY, channel.getKey());
        contentValues.put(MobiComDatabaseHelper.CLIENT_GROUP_ID, channel.getClientGroupId());
        contentValues.put(MobiComDatabaseHelper.TYPE, channel.getType());
        contentValues.put(MobiComDatabaseHelper.NOTIFICATION_AFTER_TIME, channel.getNotificationAfterTime());
        contentValues.put(MobiComDatabaseHelper.DELETED_AT, channel.getDeletedAtTime());
        contentValues.put(MobiComDatabaseHelper.ADMIN_ID, channel.getAdminKey());
        Channel oldChannel = null;
        contentValues.put(MobiComDatabaseHelper.CHANNEL_IMAGE_URL, channel.getImageUrl());
        oldChannel = ChannelDatabaseService.getInstance(context).getChannelByChannelKey(channel.getKey());

        if (channel.getKmStatus() != 0) {
            contentValues.put(MobiComDatabaseHelper.CONVERSATION_STATUS, channel.getKmStatus());
        }
        if (oldChannel != null && !TextUtils.isEmpty(oldChannel.getImageUrl()) && !channel.getImageUrl().equals(oldChannel.getImageUrl())) {
            updateChannelLocalImageURI(channel.getKey(), null);
        }
        if (!TextUtils.isEmpty(channel.getLocalImageUri())) {
            contentValues.put(MobiComDatabaseHelper.CHANNEL_IMAGE_LOCAL_URI, channel.getLocalImageUri());
        }
        if (channel.getUserCount() != 0) {
            contentValues.put(MobiComDatabaseHelper.USER_COUNT, channel.getUserCount());
        }
        if (channel.getUnreadCount() != 0) {
            contentValues.put(MobiComDatabaseHelper.UNREAD_COUNT, channel.getUnreadCount());
        }
        if (channel.getMetadata() != null) {
            contentValues.put(MobiComDatabaseHelper.CHANNEL_META_DATA, GsonUtils.getJsonFromObject(channel.getMetadata(), Map.class));
            if (channel.getMetadata().containsKey(Channel.AL_CATEGORY)) {
                contentValues.put(MobiComDatabaseHelper.AL_CATEGORY, channel.getMetadata().get(Channel.AL_CATEGORY));
            }
        }
        contentValues.put(MobiComDatabaseHelper.PARENT_GROUP_KEY, channel.getParentKey());
        contentValues.put(MobiComDatabaseHelper.PARENT_CLIENT_GROUP_ID, channel.getParentClientGroupId());
        return contentValues;
    }

    //ApplozicInternal: default
    public void addChannelUserMapper(ChannelUserMapper channelUserMapper) {
        try {
            ContentValues contentValues = prepareChannelUserMapperValues(channelUserMapper);
            dbHelper.getWritableDatabase().insertWithOnConflict(CHANNEL_USER_X, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbHelper.close();
        }
    }

    //ApplozicInternal: private
    public ContentValues prepareChannelUserMapperValues(ChannelUserMapper channelUserMapper) {
        ContentValues contentValues = new ContentValues();
        if (channelUserMapper != null) {
            if (channelUserMapper.getKey() != null) {
                contentValues.put(MobiComDatabaseHelper.CHANNEL_KEY, channelUserMapper.getKey());
            }
            if (channelUserMapper.getUserKey() != null) {
                contentValues.put(MobiComDatabaseHelper.USERID, channelUserMapper.getUserKey());
            }
            if (channelUserMapper.getUserKey() != null) {
                contentValues.put(MobiComDatabaseHelper.UNREAD_COUNT, channelUserMapper.getUnreadCount());
            }
            if (channelUserMapper.getStatus() != 0) {
                contentValues.put(MobiComDatabaseHelper.STATUS, channelUserMapper.getStatus());
            }
            contentValues.put(MobiComDatabaseHelper.ROLE, channelUserMapper.getRole());

            if (channelUserMapper.getParentKey() != null) {
                contentValues.put(MobiComDatabaseHelper.PARENT_GROUP_KEY, channelUserMapper.getParentKey());
            }
        }
        return contentValues;
    }

    /**
     * Will get a channel object from the database, corresponding to the passed client group id.
     *
     * @param clientGroupId the client group id. used to identify a channel (similar to with group id/channel key)
     * @return the channel object, null if no such channel exists in database
     */
    public Channel getChannelByClientGroupId(String clientGroupId) {
        Channel channel = null;
        try {
            String structuredNameWhere = MobiComDatabaseHelper.CLIENT_GROUP_ID + " =?";
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(CHANNEL, null, structuredNameWhere, new String[]{String.valueOf(clientGroupId)}, null, null, null);
            try {
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        channel = getChannel(cursor);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                dbHelper.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    /**
     * Will get a channel object from the database, corresponding to the passed channel key.
     *
     * @param channelKey the channel key/group id. used to identify a channel
     * @return the channel object, null if no such channel exists in database
     */
    public Channel getChannelByChannelKey(final Integer channelKey) {
        Channel channel = null;
        try {
            String structuredNameWhere = MobiComDatabaseHelper.CHANNEL_KEY + " =?";
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(CHANNEL, null, structuredNameWhere, new String[]{String.valueOf(channelKey)}, null, null, null);
            try {
                if (cursor != null) {
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        channel = getChannel(cursor);
                    }
                }

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                dbHelper.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    /**
     * Will return a list of users/contacts that are part of the channel with the given channel key.
     *
     * <p>The users will be returned in the form of User-To-Channel Mappings ({@link ChannelUserMapper)}.
     * Internally, this mapping for ever channel-user is stored in a separate table
     * with the name "channel_User_X".</p>
     *
     * @param channelKey {@link Channel#getKey()}
     * @return a list of {@link ChannelUserMapper} objects, or a empty list, or null in case of any exception
     */
    public List<ChannelUserMapper> getChannelUserList(Integer channelKey) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String structuredNameWhere = "";

            structuredNameWhere += "channelKey = ?";
            cursor = db.query(CHANNEL_USER_X, null, structuredNameWhere, new String[]{String.valueOf(channelKey)}, null, null, null);
            return getListOfUsers(cursor);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dbHelper.close();
        }
        return null;
    }

    @ApplozicInternal
    public Channel getChannel(Cursor cursor) {
        Channel channel = new Channel();
        channel.setKey(cursor.getInt(cursor.getColumnIndex(MobiComDatabaseHelper.CHANNEL_KEY)));
        channel.setParentClientGroupId(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.PARENT_CLIENT_GROUP_ID)));
        channel.setClientGroupId(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.CLIENT_GROUP_ID)));
        channel.setName(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.CHANNEL_DISPLAY_NAME)));
        channel.setAdminKey(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.ADMIN_ID)));
        channel.setType(cursor.getShort(cursor.getColumnIndex(MobiComDatabaseHelper.TYPE)));
        channel.setImageUrl(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.CHANNEL_IMAGE_URL)));
        channel.setLocalImageUri(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.CHANNEL_IMAGE_LOCAL_URI)));
        int count = cursor.getInt(cursor.getColumnIndex(MobiComDatabaseHelper.UNREAD_COUNT));
        channel.setNotificationAfterTime(cursor.getLong(cursor.getColumnIndex(MobiComDatabaseHelper.NOTIFICATION_AFTER_TIME)));
        channel.setDeletedAtTime(cursor.getLong(cursor.getColumnIndex(MobiComDatabaseHelper.DELETED_AT)));
        channel.setParentKey(cursor.getInt(cursor.getColumnIndex(MobiComDatabaseHelper.PARENT_GROUP_KEY)));
        channel.setKmStatus(cursor.getInt(cursor.getColumnIndex(MobiComDatabaseHelper.CONVERSATION_STATUS)));
        String metadata = cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.CHANNEL_META_DATA));
        channel.setMetadata(((Map<String, String>) GsonUtils.getObjectFromJson(metadata, Map.class)));
        if (count > 0) {
            channel.setUnreadCount(count);
        }
        return channel;
    }

    /**
     * Get a list of all the channels in the local database.
     *
     * <p>This will include channels the current logged in user is part of
     * or was a part of.
     * Note: This will not return all the channels, but simply the ones synced locally.</p>
     *
     * @return a list of channels, empty if none present, null in case of an exception
     */
    public List<Channel> getAllChannels() {
        List<Channel> contactList = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            cursor = db.query(CHANNEL, null, null, null, null, null, MobiComDatabaseHelper.CHANNEL_DISPLAY_NAME + " asc");
            contactList = getChannelList(cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dbHelper.close();
        }
        return contactList;
    }

    //ApplozicInternal: private
    public List<Channel> getChannelList(Cursor cursor) {
        try {
            List<Channel> channelList = new ArrayList<Channel>();
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    channelList.add(getChannel(cursor));
                } while (cursor.moveToNext());
            }
            return channelList;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    //ApplozicInternal: default
    public void updateChannel(Channel channel) {
        ContentValues contentValues = prepareChannelValues(channel);
        dbHelper.getWritableDatabase().update(CHANNEL, contentValues, MobiComDatabaseHelper.CHANNEL_KEY + "=?", new String[]{String.valueOf(channel.getKey())});
        dbHelper.close();
    }

    //ApplozicInternal: default
    /**
     * Updates the time after which notifications should be received for a muted channel in the local database.
     *
     * @param id the group id
     * @param notificationAfterTime the time in milliseconds
     */
    public void updateNotificationAfterTime(Integer id, Long notificationAfterTime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MobiComDatabaseHelper.NOTIFICATION_AFTER_TIME, notificationAfterTime);
        dbHelper.getWritableDatabase().update(CHANNEL, contentValues, MobiComDatabaseHelper.CHANNEL_KEY + "=?", new String[]{String.valueOf(id)});
    }

    //ApplozicInternal: default
    public void updateChannelUserMapper(ChannelUserMapper channelUserMapper) {
        ContentValues contentValues = prepareChannelUserMapperValues(channelUserMapper);
        dbHelper.getWritableDatabase().update(CHANNEL_USER_X, contentValues, MobiComDatabaseHelper.CHANNEL_KEY + "=?  and " + MobiComDatabaseHelper.USERID + "=?", new String[]{String.valueOf(channelUserMapper.getKey()), String.valueOf(channelUserMapper.getUserKey())});
        dbHelper.close();
    }

    /**
     * Checks if the given channel is present in the local database.
     *
     * @param channelKey the channel key
     * @return true/false
     */
    public boolean isChannelPresent(Integer channelKey) {
        Cursor cursor = null;
        try {
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            cursor = database.rawQuery(
                    "SELECT COUNT(*) FROM channel WHERE channelKey=?", new String[]{String.valueOf(channelKey)});
            cursor.moveToFirst();
            return cursor.getInt(0) > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dbHelper.close();
        }
    }

    //ApplozicInternal: default
    public void updateChannelLocalImageURI(Integer channelKey, String channelLocalURI) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MobiComDatabaseHelper.CHANNEL_IMAGE_LOCAL_URI, channelLocalURI);
        dbHelper.getWritableDatabase().update(CHANNEL, contentValues, MobiComDatabaseHelper.CHANNEL_KEY + "=?", new String[]{String.valueOf(channelKey)});
    }

    /**
     * Checks if there is a {@link ChannelUserMapper} entry in the local database for the
     * given channelKey and userId.
     *
     * @param channelKey the channel key for which we want to check
     * @param userId the user we wish to check
     * @return true if user is present in channel, false otherwise
     */
    public boolean isChannelUserPresent(Integer channelKey, String userId) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        boolean present = false;
        try {
            cursor = database.rawQuery(
                    "SELECT COUNT(*) FROM channel_User_X WHERE " + MobiComDatabaseHelper.CHANNEL_KEY + "=? and " + MobiComDatabaseHelper.USERID + "=?",
                    new String[]{String.valueOf(channelKey), String.valueOf(userId)});
            cursor.moveToFirst();
            present = cursor.getInt(0) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dbHelper.close();
        }
        return present;
    }

    //ApplozicInternal: default
    public int removeMemberFromChannel(String clientGroupId, String userId) {
        Channel channel = getChannelByClientGroupId(clientGroupId);
        return removeMemberFromChannel(channel.getKey(), userId);
    }

    //ApplozicInternal: default
    public int removeMemberFromChannel(Integer channelKey, String userId) {
        int deleteUser = 0;
        try {
            deleteUser = dbHelper.getWritableDatabase().delete(MobiComDatabaseHelper.CHANNEL_USER_X, "channelKey=? AND userId= ?", new String[]{String.valueOf(channelKey), userId});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteUser;
    }

    //ApplozicInternal: default
    public int leaveMemberFromChannel(String clientGroupId, String userId) {
        Channel channel = getChannelByClientGroupId(clientGroupId);
        return leaveMemberFromChannel(channel.getKey(), userId);
    }

    //ApplozicInternal: default
    public int leaveMemberFromChannel(Integer channelKey, String userId) {
        int deletedRows = 0;
        try {
            deletedRows = dbHelper.getWritableDatabase().delete(MobiComDatabaseHelper.CHANNEL_USER_X, "channelKey=? AND userId= ?", new String[]{String.valueOf(channelKey), userId});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deletedRows;
    }

    //ApplozicInternal: private
    public @Nullable Map<String, String> getMetadataToUpdateToDatabaseFromGroupInfoUpdate(GroupInfoUpdate groupInfoUpdate) {
        Map<String, String> updateMetadata = groupInfoUpdate.getMetadata();
        if(updateMetadata == null) {
            return null; //since no updating required
        }

        Channel channel = null;
        if (groupInfoUpdate.getGroupId() != null && groupInfoUpdate.getGroupId() != 0) {
            channel = getChannelByChannelKey(groupInfoUpdate.getGroupId());
        } else if (!TextUtils.isEmpty(groupInfoUpdate.getClientGroupId())) {
            channel = getChannelByClientGroupId(groupInfoUpdate.getClientGroupId());
        }

        if (channel != null) {
            Map<String, String> existingChannelMetadata = channel.getMetadata();
            if (existingChannelMetadata == null) {
                existingChannelMetadata = new HashMap<>();
            }
            for (String key : updateMetadata.keySet()) {
                String value = updateMetadata.get(key);
                if (value != null) {
                    existingChannelMetadata.put(key, value);
                }
            }
            return existingChannelMetadata; //new and updated now
        } else {
            Log.d(TAG, "GroupInfoUpdate object doesn't have channelKey or clientGroupId.");
            return null;
        }
    }

    //ApplozicInternal: default
    public int updateChannel(GroupInfoUpdate groupInfoUpdate) {
        if (groupInfoUpdate.getImageUrl() == null && groupInfoUpdate.getNewName() == null) {
            return 0;
        }

        int rowUpdated = 0;
        try {
            ContentValues values = new ContentValues();
            if (groupInfoUpdate != null) {
                if (!TextUtils.isEmpty(groupInfoUpdate.getClientGroupId())) {
                    Channel channel = getChannelByClientGroupId(groupInfoUpdate.getClientGroupId());
                    groupInfoUpdate.setGroupId(channel.getKey());
                }
                if (groupInfoUpdate.getNewName() != null) {
                    values.put("channelName", groupInfoUpdate.getNewName());
                }
                if (groupInfoUpdate.getImageUrl() != null) {
                    values.put("channelImageURL", groupInfoUpdate.getImageUrl());
                    values.putNull("channelImageLocalURI");
                }
                if (groupInfoUpdate.getMetadata() != null) {
                    Map<String, String> metadataToUpdate = getMetadataToUpdateToDatabaseFromGroupInfoUpdate(groupInfoUpdate);
                    if(metadataToUpdate != null) {
                        values.put(MobiComDatabaseHelper.CHANNEL_META_DATA, GsonUtils.getJsonFromObject(metadataToUpdate, Map.class));
                        if (metadataToUpdate.containsKey(Channel.AL_CATEGORY)) {
                            values.put(MobiComDatabaseHelper.AL_CATEGORY, metadataToUpdate.get(Channel.AL_CATEGORY));
                        }
                    }
                }
            }
            rowUpdated = dbHelper.getWritableDatabase().update("channel", values, "channelKey=" + groupInfoUpdate.getGroupId(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }

    //ApplozicInternal: default
    public int deleteChannel(Integer channelKey) {
        int deletedRows = 0;
        try {
            deletedRows = dbHelper.getWritableDatabase().delete(MobiComDatabaseHelper.CHANNEL, "channelKey=?", new String[]{String.valueOf(channelKey)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deletedRows;
    }

    //ApplozicInternal: default
    public int deleteChannelUserMappers(Integer channelKey) {
        int deletedRows = 0;
        try {
            deletedRows = dbHelper.getWritableDatabase().delete(MobiComDatabaseHelper.CHANNEL_USER_X, "channelKey=?", new String[]{String.valueOf(channelKey)});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deletedRows;
    }

    @ApplozicInternal
    public Loader<Cursor> getSearchCursorForGroupsLoader(final String searchString) {

        return new CursorLoader(context, null, null, null, null, MobiComDatabaseHelper.CHANNEL_DISPLAY_NAME + " asc") {
            @Override
            public Cursor loadInBackground() {

                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor;

                StringBuffer stringBuffer = new StringBuffer();

                stringBuffer.append("SELECT ").append(" * ").
                        append(" FROM ").append(MobiComDatabaseHelper.CHANNEL).append(" where ").append(MobiComDatabaseHelper.TYPE).append(" NOT IN ('").append(Channel.GroupType.CONTACT_GROUP.getValue()).append("')");

                if (!TextUtils.isEmpty(searchString)) {
                    stringBuffer.append(" AND " + MobiComDatabaseHelper.CHANNEL_DISPLAY_NAME + " like '%" + searchString.replaceAll("'", "''") + "%'");
                }
                stringBuffer.append(" order by " + MobiComDatabaseHelper.CHANNEL_DISPLAY_NAME + " COLLATE NOCASE asc ");
                cursor = db.rawQuery(stringBuffer.toString(), null);

                return cursor;

            }
        };
    }

    @ApplozicInternal
    public String getGroupOfTwoReceiverId(Integer channelKey) {
        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String structuredNameWhere = "";

            structuredNameWhere += "channelKey = ? AND userId NOT IN ('" + MobiComUserPreference.getInstance(context).getUserId().replaceAll("'", "''") + "')";
            cursor = db.query(CHANNEL_USER_X, null, structuredNameWhere, new String[]{String.valueOf(channelKey)}, null, null, null);

            List<ChannelUserMapper> channelUserMappers = getListOfUsers(cursor);
            if (channelUserMappers != null && channelUserMappers.size() > 0) {
                ChannelUserMapper channelUserMapper = channelUserMappers.get(0);
                if (channelUserMapper != null) {
                    return channelUserMapper.getUserKey();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dbHelper.close();
        }
        return null;
    }

    @ApplozicInternal
    public String[] getChannelMemberByName(String name, String type) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        List<String> userIds = new ArrayList<String>();
        Cursor cursor = database.rawQuery("Select cu.userId from channel c JOIN channel_User_X cu on c.channelKey = cu.channelKey where c.channelName ='" + name + "' AND c.type ='" + type + "'", null);
        try {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    userIds.add(cursor.getString(cursor.getColumnIndex(MobiComDatabaseHelper.USERID)));

                } while (cursor.moveToNext());
            }
            if (userIds.contains(MobiComUserPreference.getInstance(context).getUserId())) {
                userIds.remove(MobiComUserPreference.getInstance(context).getUserId());
            }
            if (userIds != null && userIds.size() > 0) {
                return userIds.toArray(new String[userIds.size()]);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dbHelper.close();
        }
    }

    //ApplozicInternal: default
    public void updateRoleInChannelUserMapper(Integer channelKey, String userId, Integer role) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MobiComDatabaseHelper.ROLE, role);
        dbHelper.getWritableDatabase().update(CHANNEL_USER_X, contentValues, MobiComDatabaseHelper.CHANNEL_KEY + "=? AND " + MobiComDatabaseHelper.USERID + "=?", new String[]{String.valueOf(channelKey), userId});
    }

    //ApplozicInternal: default
    public ChannelUserMapper getChannelUserByChannelKey(final Integer channelKey) {
        ChannelUserMapper channelUserMapper = null;
        Cursor cursor = null;
        try {
            String structuredNameWhere = MobiComDatabaseHelper.CHANNEL_KEY + " =? AND " + MobiComDatabaseHelper.USERID + "=" + MobiComUserPreference.getInstance(context).getUserId();

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.query(CHANNEL_USER_X, null, structuredNameWhere, new String[]{String.valueOf(channelKey)}, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    channelUserMapper = getChannelUser(cursor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dbHelper.close();
        }
        return channelUserMapper;
    }

    /**
     * Get the User-To-Channel mapping object ({@link ChannelUserMapper} for the given channel
     * and user.
     *
     * @param channelKey the channel key
     * @param userId the user id
     * @return the ChannelUserMapper object, null if not found
     */
    public ChannelUserMapper getChannelUserByChannelKeyAndUserId(final Integer channelKey, final String userId) {
        ChannelUserMapper channelUserMapper = null;
        Cursor cursor = null;
        try {
            String structuredNameWhere = MobiComDatabaseHelper.CHANNEL_KEY + " =? AND " + MobiComDatabaseHelper.USERID + " =?";

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.query(CHANNEL_USER_X, null, structuredNameWhere, new String[]{String.valueOf(channelKey), userId}, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    channelUserMapper = getChannelUser(cursor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dbHelper.close();
        }
        return channelUserMapper;
    }

    //ApplozicInternal: default
    public List<String> getChildGroupIds(Integer parentGroupKey) {
        if (parentGroupKey == null || parentGroupKey == 0) {
            return new ArrayList<>();
        }
        Cursor cursor = null;
        try {
            List<String> childGroupIds = new ArrayList<>();

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String structuredNameWhere = "";
            structuredNameWhere += "parentGroupKey = ?";
            cursor = db.query(CHANNEL, null, structuredNameWhere, new String[]{String.valueOf(parentGroupKey)}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    childGroupIds.add(String.valueOf(cursor.getInt(cursor.getColumnIndex(MobiComDatabaseHelper.CHANNEL_KEY))));

                } while (cursor.moveToNext());
            }
            return childGroupIds;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dbHelper.close();
        }
        return null;
    }

    //ApplozicInternal: private
    public void updateParentGroupKeyInUserMapper(Integer channelKey, Integer parentGroupKey) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MobiComDatabaseHelper.PARENT_GROUP_KEY, parentGroupKey);
        dbHelper.getWritableDatabase().update(CHANNEL_USER_X, contentValues, MobiComDatabaseHelper.CHANNEL_KEY + "=?", new String[]{String.valueOf(channelKey)});
    }

    @ApplozicInternal
    public Integer getParentGroupKey(String parentClientGroupId) {
        if (TextUtils.isEmpty(parentClientGroupId)) {
            return null;
        }
        Cursor cursor = null;
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String structuredNameWhere = "";
            structuredNameWhere += "parentClientGroupId = ?";
            cursor = db.query(CHANNEL, null, structuredNameWhere, new String[]{String.valueOf(parentClientGroupId)}, null, null, null);
            if (cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex("parentGroupKey"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            dbHelper.close();
        }
        return null;
    }


}
