package com.applozic.mobicomkit.listners;

/**
 * An interface for a simple success-failure callback.
 *
 * <p>Use as per need.</p>
 */
public interface AlCallback {
    void onSuccess(Object response);

    void onError(Object error);
}
