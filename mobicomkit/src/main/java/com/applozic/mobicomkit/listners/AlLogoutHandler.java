package com.applozic.mobicomkit.listners;

import android.content.Context;

/**
 * Callbacks for an Applozic logout.
 */
public interface AlLogoutHandler {
    void onSuccess(Context context);

    void onFailure(Exception exception);
}
