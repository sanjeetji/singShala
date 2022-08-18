package com.applozic.mobicomkit.api.attachment.urlservice;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.HttpRequestUtils;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.attachment.FileClientService;
import com.applozic.mobicomkit.api.conversation.Message;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;

//ApplozicInternal: default
public class DefaultURLService implements URLService {

    private MobiComKitClientService mobiComKitClientService;
    private HttpRequestUtils httpRequestUtils;

    //ApplozicInternal: default
    public DefaultURLService(Context context) {
        this.httpRequestUtils = new HttpRequestUtils(context);
        mobiComKitClientService = new MobiComKitClientService(context);
    }

    //ApplozicInternal: default
    @Override
    public HttpURLConnection getAttachmentConnection(Message message) throws IOException {
        return !TextUtils.isEmpty(message.getFileMetas().getUrl()) ? mobiComKitClientService.openHttpConnection(message.getFileMetas().getUrl()) : mobiComKitClientService.openHttpConnection(mobiComKitClientService.getFileUrl() + message.getFileMetas().getBlobKeyString());
    }

    //ApplozicInternal: default
    @Override
    public String getThumbnailURL(Message message) throws IOException {
        return message.getFileMetas().getThumbnailUrl();
    }

    //ApplozicInternal: default
    @Override
    public String getFileUploadUrl() {
        return httpRequestUtils.getResponse(mobiComKitClientService.getFileBaseUrl() + FileClientService.FILE_UPLOAD_URL
                + "?data=" + new Date().getTime(), "text/plain", "text/plain", true);
    }

    //ApplozicInternal: default
    @Override
    public String getImageUrl(Message message) {
        return mobiComKitClientService.getFileUrl() + message.getFileMetas().getBlobKeyString();
    }

}
