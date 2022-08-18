package com.applozic.mobicomkit.api.attachment;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicommons.task.AlAsyncTask;

import java.lang.ref.WeakReference;

@ApplozicInternal(appliesTo = ApplozicInternal.AppliesTo.ALL_MEMBERS)
public class GifDownloadAsyncTask extends AlAsyncTask<Void, String> {
    private final WeakReference<Context> contextWeakReference;
    private final String url;
    private final GifDownloadCallback gifDownloadCallback;

    public GifDownloadAsyncTask(Context context, String url, GifDownloadCallback gifDownloadCallback) {
        contextWeakReference = new WeakReference<>(context);
        this.url = url;
        this.gifDownloadCallback = gifDownloadCallback;
    }

    @Override
    protected String doInBackground() {
        return new FileClientService(contextWeakReference.get()).downloadGif(url);
    }

    @Override
    protected void onPostExecute(String localPath) {
        super.onPostExecute(localPath);
        if (gifDownloadCallback == null) {
            return;
        }

        if (TextUtils.isEmpty(localPath)) {
            gifDownloadCallback.onFailed();
        } else {
            gifDownloadCallback.onGifDownloaded(localPath);
        }
    }

    public interface GifDownloadCallback {
        void onGifDownloaded(String localPath);
        void onFailed();
    }
}
