package com.applozic.mobicomkit.api.account.user;

import android.content.Context;

import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicommons.task.AlAsyncTask;

/**
 * Task that wraps around {@link UserService#processUserBlock(String, boolean)}.
 *
 * <p>Blocks the contact/user with the given userId. Remotely as well as locally.
 *
 * Created for async execution of the above mentioned method.
 * Use {@link TaskListener} to get the results.</p>
 */
public class UserBlockTask extends AlAsyncTask<Void, Boolean> {

    private final TaskListener taskListener;
    private final Context context;
    private ApiResponse apiResponse;
    private String userId;
    private boolean block;
    private Exception mException;
    private Integer groupId;

    public UserBlockTask(Context context, TaskListener listener, String userId, boolean block) {
        this.context = context;
        this.taskListener = listener;
        this.userId = userId;
        this.block = block;
    }
    @Override
    protected Boolean doInBackground() {
        try {
            apiResponse = UserService.getInstance(context).processUserBlock(userId, block);
            return apiResponse != null && apiResponse.isSuccess();
        } catch (Exception e) {
            e.printStackTrace();
            mException = e;
            return false;
        }
    }

    @Override
    protected void onPostExecute(final Boolean result) {
        if (result && this.taskListener != null) {
            this.taskListener.onSuccess(apiResponse);
        } else if (mException != null && this.taskListener != null) {
            this.taskListener.onFailure(apiResponse, mException);
        }
        this.taskListener.onCompletion();
    }

    public interface TaskListener {

        void onSuccess(ApiResponse apiResponse);

        void onFailure(ApiResponse apiResponse, Exception exception);

        void onCompletion();
    }


}
