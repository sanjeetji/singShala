package com.applozic.mobicomkit.api.attachment.urlservice;

import android.content.Context;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicommons.ApplozicService;

import java.io.IOException;
import java.net.HttpURLConnection;

@ApplozicInternal
public class URLServiceProvider {

    private Context context;
    private URLService urlService;
    private MobiComKitClientService mobiComKitClientService;

    //ApplozicInternal: default
    public URLServiceProvider(Context context) {
        this.context = ApplozicService.getContext(context);
        mobiComKitClientService = new MobiComKitClientService(context);
    }

    private URLService getUrlService(Context context) {

        if (urlService != null) {
            return urlService;
        }

        ApplozicClient appClient = ApplozicClient.getInstance(context);

        if (appClient.isS3StorageServiceEnabled()) {
            urlService = new S3URLService(context);
        } else if (appClient.isGoogleCloudServiceEnabled()) {
            urlService = new GoogleCloudURLService(context);
        } else if (appClient.isStorageServiceEnabled()) {
            urlService = new ApplozicMongoStorageService(context);
        } else {
            urlService = new DefaultURLService(context);
        }

        return urlService;
    }

    @ApplozicInternal
    public HttpURLConnection getDownloadConnection(Message message) throws IOException {
        HttpURLConnection connection;

        try {
            connection = getUrlService(context).getAttachmentConnection(message);
        } catch (Exception e) {
            throw new IOException("Error connecting");
        }
        return connection;
    }

    @ApplozicInternal
    public String getThumbnailURL(Message message) throws IOException {
        try {
            return getUrlService(context).getThumbnailURL(message);
        } catch (Exception e) {
            throw new IOException("Error connecting");
        }
    }

    @ApplozicInternal
    public String getFileUploadUrl() {
        return getUrlService(context).getFileUploadUrl();
    }

    @ApplozicInternal
    public String getImageURL(Message message) {
        return getUrlService(context).getImageUrl(message);
    }
}
