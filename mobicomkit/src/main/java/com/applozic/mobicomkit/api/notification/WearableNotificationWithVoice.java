package com.applozic.mobicomkit.api.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.app.NotificationCompat.WearableExtender;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.annotations.ApplozicInternal;

@ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS)
public class WearableNotificationWithVoice {

    public @ApplozicInternal static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
    Class<?> notificationHandler;
    Context mContext;
    int actionIconResId;
    Builder notificationBuilder;
    int replyLabelResourceId;
    int actionTitleId;
    int notificationId;
    PendingIntent pendingIntent;

    /**
     * @param notificationBuilder
     * @param actionTitleId
     * @param replyLabelResourceId
     * @param actionIcon
     * @param notificationId
     */
    public WearableNotificationWithVoice(Builder notificationBuilder,
                                         int actionTitleId, int replyLabelResourceId, int actionIcon, int notificationId) {
        this.notificationBuilder = notificationBuilder;
        this.replyLabelResourceId = replyLabelResourceId;
        this.actionIconResId = actionIcon;
        this.actionTitleId = actionTitleId;
        this.notificationId = notificationId;
    }

    //ApplozicInternal: private
    public void setNotificationHandler(Class<?> replyActivityClass) {
        this.notificationHandler = replyActivityClass;
    }

    //ApplozicInternal: default
    public void setCurrentContext(Context currentContext) {
        this.mContext = currentContext;
    }

    //ApplozicInternal: default
    public void setPendingIntent(PendingIntent pendingIntent) {
        this.pendingIntent = pendingIntent;
    }

    /**
     * This method is just like a wrapper class method for usual notification class which add voice actions
     * for wearable devices
     *
     * @throws RuntimeException
     */
    //ApplozicInternal: defalut
    public void sendNotification() throws Exception {
        if (pendingIntent == null && notificationHandler == null) {
            throw new RuntimeException("Either pendingIntent or handler class requires.");
        }
        //Action action = buildWearableAction(); removed remote input action for now
        Notification notification = notificationBuilder.extend(new WearableExtender()).build();

        if (ApplozicClient.getInstance(mContext).isNotificationSmallIconHidden() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            int smallIconViewId = mContext.getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());
            if (smallIconViewId != 0) {

                if (notification.contentIntent != null  && notification.contentView != null) {
                    notification.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (notification.headsUpContentView != null) {
                        notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
                    }
                    if (notification.bigContentView != null) {
                        notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
                    }
                }

            }
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
        notificationManager.notify(notificationId, notification);
    }

    private Action buildWearableAction() {
        String replyLabel = mContext.getString(replyLabelResourceId);
        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY).setLabel(replyLabel).build();
        // Create an intent for the reply action
        if (pendingIntent == null) {
            Intent replyIntent = new Intent(mContext, notificationHandler);
            pendingIntent = PendingIntent.getActivity(mContext, (int) (System.currentTimeMillis() & 0xfffffff), replyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
        // Create the reply action and add the remote input
        NotificationCompat.Action action = new NotificationCompat.Action.Builder(actionIconResId,
                mContext.getString(actionTitleId), pendingIntent).addRemoteInput(remoteInput).build();
        return action;
    }

}