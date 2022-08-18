package com.applozic.mobicomkit.api.conversation;

import android.content.Context;

import com.applozic.mobicommons.task.AlAsyncTask;

/**
 * This task will sync messages from the server to the local database.
 *
 * <p>It will also update the sync time and user (Contact) details.
 * See {@link SyncCallService#syncMessages(String)} when the key is passed null.</p>
 */
public class MessageSyncTask extends AlAsyncTask<Void, Void> {
    private final SyncCallService syncCallService;
    private final MessageSyncListener messageSyncListener;

    public MessageSyncTask(Context context, MessageSyncListener messageSyncListener) {
        syncCallService = SyncCallService.getInstance(context);
        this.messageSyncListener = messageSyncListener;
    }

    @Override
    protected Void doInBackground() throws Exception {
        syncCallService.syncMessages(null);
        return super.doInBackground();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        messageSyncListener.onSync();
    }

    public interface MessageSyncListener {
        void onSync();
    }
}
