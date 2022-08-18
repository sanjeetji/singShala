package com.applozic.mobicomkit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.account.register.RegistrationResponse;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.PushNotificationTask;
import com.applozic.mobicomkit.api.account.user.User;
import com.applozic.mobicomkit.api.account.user.UserClientService;
import com.applozic.mobicomkit.api.account.user.UserLoginTask;
import com.applozic.mobicomkit.api.account.user.UserLogoutTask;
import com.applozic.mobicomkit.api.authentication.AlAuthService;
import com.applozic.mobicomkit.api.conversation.ApplozicMqttWorker;
import com.applozic.mobicomkit.api.notification.MobiComPushReceiver;
import com.applozic.mobicomkit.api.notification.NotificationChannels;
import com.applozic.mobicomkit.broadcast.ApplozicBroadcastReceiver;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.contact.database.ContactDatabase;
import com.applozic.mobicomkit.listners.AlCallback;
import com.applozic.mobicomkit.listners.AlLoginHandler;
import com.applozic.mobicomkit.listners.AlLogoutHandler;
import com.applozic.mobicomkit.listners.AlPushNotificationHandler;
import com.applozic.mobicomkit.listners.ApplozicUIListener;
import com.applozic.mobicommons.AlLog;
import com.applozic.mobicommons.ApplozicService;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.data.AlPrefSettings;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;
import com.applozic.mobicommons.task.AlTask;

import java.util.Map;

/**
 * Class for all the major public methods for using the Applozic Chat SDK.
 */
public class Applozic {

    private static final String APPLICATION_KEY = "APPLICATION_KEY";
    private static final String DEVICE_REGISTRATION_ID = "DEVICE_REGISTRATION_ID";
    private static final String MY_PREFERENCE = "applozic_preference_key";
    private static final String NOTIFICATION_CHANNEL_VERSION_STATE = "NOTIFICATION_CHANNEL_VERSION_STATE";
    private static final String CUSTOM_NOTIFICATION_SOUND = "CUSTOM_NOTIFICATION_SOUND";
    public static Applozic applozic;
    private SharedPreferences sharedPreferences;
    private Context context;
    private ApplozicBroadcastReceiver applozicBroadcastReceiver;
    private AlLog.AlLoggerListener alLoggerListener;

    private Applozic(Context context) {
        this.context = ApplozicService.getContext(context);
        this.sharedPreferences = this.context.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
    }

    /**
     * Initializes the client SDK.
     *
     * <p>This method basically stores the application key and application context for further use.</p>
     *
     * @param context the context
     * @param applicationKey the application id/application key
     *
     * @return the {@link Applozic} object
     */
    public static Applozic init(Context context, String applicationKey) {
        applozic = getInstance(context);
        AlPrefSettings.getInstance(context).setApplicationKey(applicationKey);
        return applozic;
    }

    /**
     * Returns the {@link Applozic} object. Singleton.
     *
     * @param context the context
     * @return the object
     */
    public static Applozic getInstance(Context context) {
        if (applozic == null) {
            applozic = new Applozic(ApplozicService.getContext(context));
        }
        return applozic;
    }

    /**
     * Will set a log callback, that will be able to "capture" Applozic logs.
     *
     * <p>Logs logged using {@link AlLog} will be sent in the callback.</p>
     *
     * @param alLoggerListener the listener
     */
    public void setAlLoggerListener(AlLog.AlLoggerListener alLoggerListener) {
        this.alLoggerListener = alLoggerListener;
    }

    /**
     * Will set the API key from Google's Geolocation API
     *
     * <p>This will be used for the Applozic SDK's location services like sharing location etc.</p>
     *
     * @param geoApiKey the geolocation API key
     */
    public void setGeoApiKey(String geoApiKey) {
        AlPrefSettings.getInstance(context).setGeoApiKey(geoApiKey);
    }

    @ApplozicInternal
    public String getGeoApiKey() {
        String geoApiKey = AlPrefSettings.getInstance(context).getGeoApiKey();
        if (!TextUtils.isEmpty(geoApiKey)) {
            return geoApiKey;
        }
        return Utils.getMetaDataValue(context, AlPrefSettings.GOOGLE_API_KEY_META_DATA);
    }

    /**
     * This method will give you the application key/id with which the given SDK has been initialized.
     *
     * <p>This application key/id is retrieved from one of three places:
     * - From the server call (usually the registration server call)
     * - From the AndroidManifest.xml metadata value
     * - From the shared preferences (key is written here during SDK initialization)</p>
     *
     * @return the application key
     */
    public String getApplicationKey() {
        String decryptedApplicationKey = AlPrefSettings.getInstance(context).getApplicationKey();
        if (!TextUtils.isEmpty(decryptedApplicationKey)) {
            return decryptedApplicationKey;
        }
        String existingAppKey = sharedPreferences.getString(APPLICATION_KEY, null);
        if (!TextUtils.isEmpty(existingAppKey)) {
            AlPrefSettings.getInstance(context).setApplicationKey(existingAppKey);
            sharedPreferences.edit().remove(APPLICATION_KEY).commit();
        }
        return existingAppKey;
    }

    /**
     * Gets the device registration id, to be used for push notifications.
     *
     * <p>This device registration id is retrieved from shared preferences.</p>
     *
     * @return the device registration id
     */
    public String getDeviceRegistrationId() {
        return sharedPreferences.getString(DEVICE_REGISTRATION_ID, null);
    }

    @SuppressLint("NewApi")
    @ApplozicInternal
    public int getNotificationChannelVersion() {
        return sharedPreferences.getInt(NOTIFICATION_CHANNEL_VERSION_STATE, NotificationChannels.NOTIFICATION_CHANNEL_VERSION - 1);
    }

    @ApplozicInternal
    public void setNotificationChannelVersion(int version) {
        sharedPreferences.edit().putInt(NOTIFICATION_CHANNEL_VERSION_STATE, version).commit();
    }

    /**
     * Store the device registration id in shared preferences.
     *
     * @param registrationId the device registration id from FCM
     * @return the enclosing class object
     */
    public Applozic setDeviceRegistrationId(String registrationId) {
        sharedPreferences.edit().putString(DEVICE_REGISTRATION_ID, registrationId).commit();
        return this;
    }

    /**
     * Sets the filepath for a custom Android notification sound.
     *
     * <p>This will be referred to by the SDK when required.</p>
     *
     * @param filePath the filepath to the notification sound
     * @return the enclosing class object
     */
    public Applozic setCustomNotificationSound(String filePath) {
        sharedPreferences.edit().putString(CUSTOM_NOTIFICATION_SOUND, filePath).commit();
        return this;
    }

    @ApplozicInternal
    public String getCustomNotificationSound() {
        return sharedPreferences.getString(CUSTOM_NOTIFICATION_SOUND, null);
    }

    //ApplozicInternal: private
    /**
     * Publish offline status, unsubscribe from conversation topic (for real time updates) and disconnect MQTT.
     *
     * @param context the context
     * @param deviceKeyString the device key string (id)
     * @param userKeyString the user key string (id)
     * @param useEncrypted true will send a encrypted topic filter along with a non-encrypted while un-subscribing, false will send only non-encrypted
     */
    public static void disconnectPublish(Context context, String deviceKeyString, String userKeyString, boolean useEncrypted) {
        if (!TextUtils.isEmpty(userKeyString) && !TextUtils.isEmpty(deviceKeyString)) {
            ApplozicMqttWorker.enqueueWorkDisconnectPublish(context, deviceKeyString, userKeyString, useEncrypted);
        }
    }

    /**
     * @deprecated User {@link Applozic#isConnected(Context)}.
     */
    @Deprecated
    public static boolean isLoggedIn(Context context) {
        return MobiComUserPreference.getInstance(context).isLoggedIn();
    }

    /**
     * Disconnect from MQTT for publishing and receiving events.
     *
     * @param context the context
     */
    public static void disconnectPublish(Context context) {
        disconnectPublish(context, true);
    }

    /**
     * Connect to MQTT for publishing and receiving events.
     *
     * @param context the context
     */
    //Cleanup: can be removed, not used in SDK
    public static void connectPublish(Context context) {
        ApplozicMqttWorker.enqueueWorkSubscribeAndConnectPublishAfter(context, true, 0);
    }

    /**
     * Connect to MQTT after for publishing and receiving events after verifying and refreshing the JWT auth token.
     *
     * @param context the context
     * @param loadingMessage the message to display in the progress dialog while loading
     */
    public static void connectPublishWithVerifyToken(final Context context, String loadingMessage) {
        connectPublishWithVerifyTokenAfter(context, loadingMessage, 0);
    }

    /**
     * Connect to MQTT after the given interval in minutes for publishing and receiving events
     * after verifying and refreshing the JWT auth token.
     *
     * @param context the context
     * @param loadingMessage the message to display in the progress dialog while loading
     * @param minutes the minutes after which to schedule the MQTT connection request, pass 0 for immediate
     */
    public static void connectPublishWithVerifyTokenAfter(final Context context, String loadingMessage, int minutes) {
        if (context == null) {
            return;
        }
        AlLog.d("Applozic", "MQTTRetry", "Refreshing JWT Token if required...");
        AlAuthService.verifyToken(context, loadingMessage, new AlCallback() {
            @Override
            public void onSuccess(Object response) {
                ApplozicMqttWorker.enqueueWorkSubscribeAndConnectPublishAfter(context, true, minutes);
            }

            @Override
            public void onError(Object error) { }
        });
    }

    //ApplozicInternal: private
    public static void disconnectPublish(Context context, boolean useEncrypted) {
        final String deviceKeyString = MobiComUserPreference.getInstance(context).getDeviceKeyString();
        final String userKeyString = MobiComUserPreference.getInstance(context).getSuUserKeyString();
        disconnectPublish(context, deviceKeyString, userKeyString, useEncrypted);
    }

    //ApplozicInternal: private
    public static void subscribeToSupportGroup(Context context, boolean useEncrypted) {
        ApplozicMqttWorker.enqueueWorkSubscribeToSupportGroup(context, useEncrypted);
    }

    //ApplozicInternal: private
    public static void unSubscribeToSupportGroup(Context context, boolean useEncrypted) {
        ApplozicMqttWorker.enqueueWorkUnSubscribeToSupportGroup(context, useEncrypted);
    }

    /**
     * Subscribe to the MQTT topic for typing for a particular conversation.
     *
     * <p>Either one of the channel or contact passed can be null.</p>
     *
     * @param context the context
     * @param channel the channel you wish to subscribe to for typing
     * @param contact the contact you wish to subscribe to for typing
     */
    public static void subscribeToTyping(Context context, Channel channel, Contact contact) {
        ApplozicMqttWorker.enqueueWorkSubscribeToTyping(context, channel, contact);
    }

    /**
     * Unsubscribe to the MQTT topic for typing for a particular conversation.
     *
     * <p>Either one of the channel or contact passed can be null.</p>
     *
     * @param context the context
     * @param channel the channel you wish to unsubscribe to for typing
     * @param contact the contact you wish to unsubscribe to for typing
     */
    public static void unSubscribeToTyping(Context context, Channel channel, Contact contact) {
        ApplozicMqttWorker.enqueueWorkUnSubscribeToTyping(context, channel, contact);
    }

    /**
     * Publish your typing status to MQTT for a particular conversation.
     *
     * <p>Either one of the channel or contact passed can be null.</p>
     *
     * @param context the context
     * @param channel the channel you wish to publish typing status to
     * @param contact the contact you wish to publish typing status to
     */
    public static void publishTypingStatus(Context context, Channel channel, Contact contact, boolean typingStarted) {
        ApplozicMqttWorker.enqueueWorkPublishTypingStatus(context, channel, contact, typingStarted);
    }

    /**
     * @deprecated User {@link Applozic#connectUser(Context, User, AlLoginHandler)}.
     */
    @Deprecated
    public static void loginUser(Context context, User user, AlLoginHandler loginHandler) {
        if (MobiComUserPreference.getInstance(context).isLoggedIn()) {
            RegistrationResponse registrationResponse = new RegistrationResponse();
            registrationResponse.setMessage("User already Logged in");
            loginHandler.onSuccess(registrationResponse, context);
        } else {
            AlTask.execute(new UserLoginTask(user, loginHandler, context));
        }
    }

    /**
     * Use this method for Applozic user login/registration. It connects and authenticates a user to the Applozic servers
     * and initializes user data for the SDK.
     *
     * <p>This method checks if a user is already logged/connected and doesn't connect if it finds one.
     * If the user with the given user id is not found in the servers, a new one will be registered.
     * See {@link com.applozic.mobicomkit.api.account.register.RegisterUserClientService#createAccount(User)}.</p>
     *
     * @param context the context
     * @param user the user object
     * @param loginHandler the success/failure callbacks
     */
    public static void connectUser(Context context, User user, AlLoginHandler loginHandler) {
        if (isConnected(context)) {
            RegistrationResponse registrationResponse = new RegistrationResponse();
            registrationResponse.setMessage("User already Logged in");
            Contact contact = new ContactDatabase(context).getContactById(MobiComUserPreference.getInstance(context).getUserId());
            if (contact != null) {
                registrationResponse.setUserId(contact.getUserId());
                registrationResponse.setContactNumber(contact.getContactNumber());
                registrationResponse.setRoleType(contact.getRoleType());
                registrationResponse.setImageLink(contact.getImageURL());
                registrationResponse.setDisplayName(contact.getDisplayName());
                registrationResponse.setStatusMessage(contact.getStatus());
            }
            loginHandler.onSuccess(registrationResponse, context);
        } else {
            AlTask.execute(new UserLoginTask(user, loginHandler, context));
        }
    }

    //ApplozicInternal: private
    public static void connectUserWithoutCheck(Context context, User user, AlLoginHandler loginHandler) {
        AlTask.execute(new UserLoginTask(user, loginHandler, context));
    }

    /**
     * Checks if a user is connected and authenticated.
     *
     * <p>The SDK can be used only after a user has been logged in/connected.</p>
     *
     * @param context the context
     * @return true/false
     */
    public static boolean isConnected(Context context) {
        return MobiComUserPreference.getInstance(context).isLoggedIn();
    }

    //ApplozicInternal: private
    public static boolean isRegistered(Context context) {
        return MobiComUserPreference.getInstance(context).isRegistered();
    }

    @ApplozicInternal
    public static boolean isApplozicNotification(Context context, Map<String, String> data) {
        if (MobiComPushReceiver.isMobiComPushNotification(data)) {
            MobiComPushReceiver.processMessageAsync(context, data);
            return true;
        }
        return false;
    }

    /**
     * @deprecated Use {@link Applozic#connectUser(Context, User, AlLoginHandler)}.
     */
    @Deprecated
    public static void loginUser(Context context, User user, boolean withLoggedInCheck, AlLoginHandler loginHandler) {
        if (withLoggedInCheck && MobiComUserPreference.getInstance(context).isLoggedIn()) {
            RegistrationResponse registrationResponse = new RegistrationResponse();
            registrationResponse.setMessage("User already Logged in");
            loginHandler.onSuccess(registrationResponse, context);
        } else {
            AlTask.execute(new UserLoginTask(user, loginHandler, context));
        }
    }

    /**
     * Logout the current user in an asynchronous thread.
     *
     * <p>See {@link UserClientService#logout(boolean)} for details.</p>
     *
     * @param context the context
     * @param logoutHandler success/failure callbacks
     */
    public static void logoutUser(final Context context, AlLogoutHandler logoutHandler) {
        AlTask.execute(new UserLogoutTask(logoutHandler, context));
    }

    //ApplozicInternal: private
    public static void registerForPushNotification(Context context, String pushToken, AlPushNotificationHandler handler) {
        AlTask.execute(new PushNotificationTask(context, pushToken, handler));
    }

    //ApplozicInternal: private
    public static void registerForPushNotification(Context context, AlPushNotificationHandler handler) {
        registerForPushNotification(context, Applozic.getInstance(context).getDeviceRegistrationId(), handler);
    }

    /**
     * Logs the given info message to the console.
     *
     * <p>if a {@link com.applozic.mobicommons.AlLog.AlLoggerListener} listener has been
     * set, {@link com.applozic.mobicommons.AlLog.AlLoggerListener#onLogged(AlLog)} will be called
     * and a corresponding {@link AlLog} object will be passed to it.</p>
     *
     * @param tag The log tag.
     * @param message The log message.
     */
    public void logInfo(String tag, String message) {
        AlLog alLog = AlLog.i(tag, null, message);

        if (alLoggerListener != null) {
            alLoggerListener.onLogged(alLog);
        }
    }

    /**
     * Logs the given error message to the console.
     *
     * <p>if a {@link com.applozic.mobicommons.AlLog.AlLoggerListener} listener has been
     * set, {@link com.applozic.mobicommons.AlLog.AlLoggerListener#onLogged(AlLog)} will be called
     * and a corresponding {@link AlLog} object will be passed to it.</p>
     *
     * @param tag The log tag.
     * @param message The log message.
     */
    public void logError(String tag, String message, Throwable throwable) {
        AlLog alLog = AlLog.e(tag, null, message, throwable);

        if (alLoggerListener != null) {
            alLoggerListener.onLogged(alLog);
        }
    }

    /**
     * @deprecated Use {@link com.applozic.mobicomkit.broadcast.AlEventManager#registerUIListener(String, ApplozicUIListener)}.
     */
    @Deprecated
    public void registerUIListener(ApplozicUIListener applozicUIListener) {
        applozicBroadcastReceiver = new ApplozicBroadcastReceiver(applozicUIListener);
        LocalBroadcastManager.getInstance(context).registerReceiver(applozicBroadcastReceiver, BroadcastService.getIntentFilter());
    }

    /**
     * @deprecated Use {@link com.applozic.mobicomkit.broadcast.AlEventManager#unregisterMqttListener(String)}.
     */
    @Deprecated
    public void unregisterUIListener() {
        if (applozicBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(applozicBroadcastReceiver);
            applozicBroadcastReceiver = null;
        }
    }
}