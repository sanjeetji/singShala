package com.applozic.mobicomkit.listners;

import android.app.Activity;
import android.net.Uri;

import com.applozic.mobicomkit.annotations.ApplozicInternal;

@ApplozicInternal
public interface AttachmentFilteringListener {
    void onAttachmentSelected(Activity activity, Uri selectedFileUri, AlCallback callback);
}
