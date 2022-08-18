package com.applozic.mobicomkit.listners;

import android.content.Context;

import com.applozic.mobicomkit.api.account.register.RegistrationResponse;

/**
 * Callbacks for Applozic login/registration.
 */
public interface AlLoginHandler {
    void onSuccess(RegistrationResponse registrationResponse, Context context);

    void onFailure(RegistrationResponse registrationResponse, Exception exception);
}
