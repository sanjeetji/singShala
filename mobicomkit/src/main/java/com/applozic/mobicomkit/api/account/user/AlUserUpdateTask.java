package com.applozic.mobicomkit.api.account.user;

import android.content.Context;

import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicomkit.listners.AlCallback;
import com.applozic.mobicommons.task.AlAsyncTask;

import java.lang.ref.WeakReference;

/**
 * Task that wraps around {@link UserService#updateUserWithResponse(User)}.
 *
 * <p>Update user data/details. Remotely as well as locally.
 * Created for async execution of the above mentioned method.
 * Use {@link AlCallback} to get the results.</p>
 */
public class AlUserUpdateTask extends AlAsyncTask<Void, ApiResponse> {
    private WeakReference<Context> context;
    private User user;
    private AlCallback callback;

    public AlUserUpdateTask(Context context, User user, AlCallback callback) {
        this.context = new WeakReference<>(context);
        this.user = user;
        this.callback = callback;
    }

    @Override
    protected ApiResponse doInBackground() {
        return UserService.getInstance(context.get()).updateUserWithResponse(user);
    }

    @Override
    protected void onPostExecute(ApiResponse apiResponse) {
        super.onPostExecute(apiResponse);
        if (callback != null) {
            if (apiResponse != null) {
                if (apiResponse.isSuccess()) {
                    callback.onSuccess(apiResponse.getResponse());
                } else {
                    callback.onError(apiResponse.getErrorResponse());
                }
            } else {
                callback.onError("error");
            }
        }
    }
}
