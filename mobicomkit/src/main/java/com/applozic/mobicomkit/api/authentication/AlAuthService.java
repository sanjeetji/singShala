package com.applozic.mobicomkit.api.authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.TextUtils;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.listners.AlCallback;
import com.applozic.mobicommons.task.AlTask;

@ApplozicInternal
public class AlAuthService {
    //ApplozicInternal: private
    public static boolean isTokenValid(long createdAtTime, int validUptoMins) {
        return (System.currentTimeMillis() - createdAtTime) / 60000 < validUptoMins;
    }

    //ApplozicInternal: private
    public static void refreshToken(Context context, AlCallback callback) {
        AlTask.execute(new RefreshAuthTokenTask(context, callback));
    }

    //ApplozicInternal: default
    public static boolean isTokenValid(Context context) {
        if (context == null) {
            return true;
        }

        MobiComUserPreference userPreference = MobiComUserPreference.getInstance(context);
        if (userPreference == null) {
            return true;
        }
        String token = userPreference.getUserAuthToken();
        long createdAtTime = userPreference.getTokenCreatedAtTime();
        int validUptoMins = userPreference.getTokenValidUptoMins();

        if ((validUptoMins > 0 && !isTokenValid(createdAtTime, validUptoMins)) || TextUtils.isEmpty(token)) {
            return false;
        } else if (!TextUtils.isEmpty(token)) {
            if ((createdAtTime == 0 || validUptoMins == 0)) {
                JWT.parseToken(context, token);
                isTokenValid(context);
            }
        }
        return true;
    }

    //ApplozicInternal: default
    public static void verifyToken(Context context, String loadingMessage, AlCallback callback) {
        if (context == null) {
            return;
        }

        MobiComUserPreference userPreference = MobiComUserPreference.getInstance(context);
        if (userPreference == null) {
            return;
        }
        String token = userPreference.getUserAuthToken();
        long createdAtTime = userPreference.getTokenCreatedAtTime();
        int validUptoMins = userPreference.getTokenValidUptoMins();

        if ((validUptoMins > 0 && !isTokenValid(createdAtTime, validUptoMins)) || TextUtils.isEmpty(token)) {
            refreshToken(context, loadingMessage, callback);
        } else if (!TextUtils.isEmpty(token)) {
            if ((createdAtTime == 0 || validUptoMins == 0)) {
                JWT.parseToken(context, token);
                verifyToken(context, loadingMessage, callback);
            }
            if (callback != null) {
                callback.onSuccess(true);
            }
        }
    }

    //ApplozicInternal: private
    public static void refreshToken(Context context, String loadingMessage, final AlCallback callback) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(context));
        progressDialog.setMessage(loadingMessage);
        progressDialog.setCancelable(false);
        progressDialog.show();

        refreshToken(context, new AlCallback() {
            @Override
            public void onSuccess(Object response) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (callback != null) {
                    callback.onSuccess(response);
                }
            }

            @Override
            public void onError(Object error) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (callback != null) {
                    callback.onSuccess(error);
                }
            }
        });
    }

    //ApplozicInternal: private
    public static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
