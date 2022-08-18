package com.applozic.mobicomkit.listners;

import com.applozic.mobicomkit.api.account.register.RegistrationResponse;

/**
 * Callback for the response for registering device id for push notifications.
 */
public interface AlPushNotificationHandler {
    void onSuccess(RegistrationResponse registrationResponse);

    void onFailure(RegistrationResponse registrationResponse, Exception exception);
}
