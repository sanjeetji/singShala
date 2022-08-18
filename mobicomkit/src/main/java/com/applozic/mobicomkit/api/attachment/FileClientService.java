package com.applozic.mobicomkit.api.attachment;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.annotations.ApplozicInternal;
import com.applozic.mobicomkit.api.HttpRequestUtils;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.attachment.urlservice.URLServiceProvider;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.api.conversation.service.ConversationService;
import com.applozic.mobicomkit.feed.TopicDetail;
import com.applozic.mobicommons.commons.core.utils.DateUtils;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.commons.image.ImageUtils;
import com.applozic.mobicommons.file.FileUtils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.Conversation;
import com.applozic.mobicommons.people.contact.Contact;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by devashish on 26/12/14.
 */
public class FileClientService extends MobiComKitClientService {

    //Todo: Make the base folder configurable using either strings.xml or properties file
    //ApplozicInternal: default, make all private unless specified
    public static final String MOBI_COM_IMAGES_FOLDER = "/image";
    public static final String MOBI_COM_VIDEOS_FOLDER = "/video";
    public static final String MOBI_COM_CONTACT_FOLDER = "/contact";
    public static final String MOBI_COM_OTHER_FILES_FOLDER = "/other";
    public static final String MOBI_COM_THUMBNAIL_SUFIX = "/.Thumbnail";
    @ApplozicInternal public static final String FILE_UPLOAD_URL = "/rest/ws/aws/file/url";
    public static final String IMAGE_DIR = "image";
    public static final String AL_UPLOAD_FILE_URL = "/rest/ws/upload/file";
    public static final String CUSTOM_STORAGE_SERVICE_END_POINT = "/rest/ws/upload/image";
    //    public static final String S3_SIGNED_URL_END_POINT = "/rest/ws/upload/file";
    public static final String S3_SIGNED_URL_END_POINT = "/rest/ws/upload/image"; //ApplozicInternal: default
    public static final String S3_SIGNED_URL_PARAM = "aclsPrivate"; //ApplozicInternal: default
    public static final String THUMBNAIL_URL = "/files/";
    private static final int MARK = 1024;
    private static final String TAG = "FileClientService";
    private static final String MAIN_FOLDER_META_DATA = "main_folder_name";
    private HttpRequestUtils httpRequestUtils;
    private MobiComKitClientService mobiComKitClientService;

    public FileClientService(Context context) {
        super(context);
        this.httpRequestUtils = new HttpRequestUtils(context);
        this.mobiComKitClientService = new MobiComKitClientService(context);
    }

    //ApplozicInternal: rename to getApplozicInternalFilePath
    @ApplozicInternal
    public static File getFilePath(String fileName, Context context, String contentType, boolean isThumbnail) {
        File filePath;
        File dir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String folder = "/" + Utils.getMetaDataValue(context, MAIN_FOLDER_META_DATA) + MOBI_COM_OTHER_FILES_FOLDER;

            if (contentType.startsWith("image")) {
                folder = "/" + Utils.getMetaDataValue(context, MAIN_FOLDER_META_DATA) + MOBI_COM_IMAGES_FOLDER;
            } else if (contentType.startsWith("video")) {
                folder = "/" + Utils.getMetaDataValue(context, MAIN_FOLDER_META_DATA) + MOBI_COM_VIDEOS_FOLDER;
            } else if (contentType.equalsIgnoreCase("text/x-vCard")) {
                folder = "/" + Utils.getMetaDataValue(context, MAIN_FOLDER_META_DATA) + MOBI_COM_CONTACT_FOLDER;
            }
            if (isThumbnail) {
                folder = folder + MOBI_COM_THUMBNAIL_SUFIX;
            }
            File directory = context.getExternalFilesDir(null);
            if (directory != null) {
                dir = new File(directory.getAbsolutePath() + folder);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
        } else {
            dir = new File(new ContextWrapper(context).getFilesDir().getAbsolutePath() + MOBI_COM_IMAGES_FOLDER);
        }
        // Create image name
        //String extention = "." + contentType.substring(contentType.indexOf("/") + 1);
        filePath = new File(dir, fileName);
        return filePath;
    }

    @ApplozicInternal
    public static File getFilePath(String fileName, Context context, String contentType) {
        return getFilePath(fileName, context, contentType, false);
    }

    //ApplozicInternal: private
    public String profileImageUploadURL() {
        return getBaseUrl() + AL_UPLOAD_FILE_URL;
    }

    private String[] getParts(String filePath) {
        return filePath.split("/");
    }

    private String getVideoThumbnailFileNameForLocalGeneration(String filePath) {
        String[] parts = getParts(filePath);
        String videoFileName = parts[parts.length - 1].split("[.]")[0];
        return videoFileName + ".jpeg";
    }

    private String getThumbnailFileNameForServerDownload(Message message) {
        String contentType = message.getFileMetas().getContentType();
        String messageFileFormat = FileUtils.getFileFormat(message.getFileMetas().getName());
        boolean isThumbnailForVideo = !TextUtils.isEmpty(contentType) && contentType.contains("video");
        final String DEFAULT_EXTENSION_FOR_VIDEO_THUMBNAILS = "jpeg";
        String thumbnailExtension = isThumbnailForVideo ? DEFAULT_EXTENSION_FOR_VIDEO_THUMBNAILS : messageFileFormat;
        return FileUtils.getName(message.getFileMetas().getName()) + message.getCreatedAtTime() + "." + thumbnailExtension;
    }

    @ApplozicInternal
    public Bitmap downloadAndSaveThumbnailImage(Context context, Message message, int reqWidth, int reqHeight) {
        HttpURLConnection connection = null;
        try {
            Bitmap attachedImage = null;

            String thumbnailUrl = new URLServiceProvider(context).getThumbnailURL(message);

            if (TextUtils.isEmpty(thumbnailUrl)) {
                return null;
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            String contentType = message.getFileMetas().getContentType();
            String imageName = getThumbnailFileNameForServerDownload(message);
            String imageLocalPath = getFilePath(imageName, context, contentType, true).getAbsolutePath();

            if (imageLocalPath != null) {
                try {
                    attachedImage = BitmapFactory.decodeFile(imageLocalPath);
                } catch (Exception ex) {
                    Utils.printLog(context, TAG, "File not found on local storage: " + ex.getMessage());
                }
            }

            if (attachedImage == null) {
                connection = openHttpConnection(thumbnailUrl);
                if (connection.getResponseCode() == 200) {
                    // attachedImage = BitmapFactory.decodeStream(connection.getInputStream(),null,options);
                    attachedImage = BitmapFactory.decodeStream(connection.getInputStream());
                    File file = FileClientService.getFilePath(imageName, context, contentType, true);
                    imageLocalPath = ImageUtils.saveImageToInternalStorage(file, attachedImage);

                } else {
                    Utils.printLog(context, TAG, "Download is failed response code is ...." + connection.getResponseCode());
                    return null;
                }
            }
            // Calculate inSampleSize
            options.inSampleSize = ImageUtils.calculateInSampleSize(options, 200, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            attachedImage = BitmapFactory.decodeFile(imageLocalPath, options);
            return attachedImage;
        } catch (FileNotFoundException ex) {
            Utils.printLog(context, TAG, "File not found on server: " + ex.getMessage());
        } catch (Exception ex) {
            Utils.printLog(context, TAG, "Exception fetching file from server: " + ex.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    /**
     * @param message
     */
    @ApplozicInternal
    public void loadContactsvCard(Message message) {
        File file = null;
        HttpURLConnection connection = null;
        try {
            InputStream inputStream = null;
            FileMeta fileMeta = message.getFileMetas();
            String contentType = fileMeta.getContentType();
            String fileName = fileMeta.getName();
            file = FileClientService.getFilePath(fileName, context.getApplicationContext(), contentType);
            if (!file.exists()) {

                connection = new URLServiceProvider(context).getDownloadConnection(message);
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                } else {
                    //TODO: Error Handling...
                    Utils.printLog(context, TAG, "Got Error response while uploading file : " + connection.getResponseCode());
                    return;
                }

                OutputStream output = new FileOutputStream(file);
                byte data[] = new byte[1024];
                int count = 0;
                while ((count = inputStream.read(data)) != -1) {
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                inputStream.close();
            }
            //Todo: Fix this, so that attach package can be moved to mobicom mobicom.
            new MessageDatabaseService(context).updateInternalFilePath(message.getKeyString(), file.getAbsolutePath());

            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(file.getAbsolutePath());
            message.setFilePaths(arrayList);

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            Utils.printLog(context, TAG, "File not found on server");
        } catch (Exception ex) {
            //If partial file got created delete it, we try to download it again
            if (file != null && file.exists()) {
                Utils.printLog(context, TAG, " Exception occured while downloading :" + file.getAbsolutePath());
                file.delete();
            }
            ex.printStackTrace();
            Utils.printLog(context, TAG, "Exception fetching file from server");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @ApplozicInternal
    public String downloadGif(String url) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL urlConnection = new URL(url);
            connection = (HttpURLConnection) urlConnection.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "Gif Download: Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                return null;
            }

            input = connection.getInputStream();

            String gifName = "GIF_" + System.currentTimeMillis() + ".gif";
            String gifLocalPath = getFilePath(gifName, context, "image", false).getAbsolutePath();
            File downloadedFile = new File(gifLocalPath);

            output = new FileOutputStream(downloadedFile);

            byte[] data = new byte[4096];
            int count;
            while ((count = input.read(data)) != -1) {
                output.write(data, 0, count);
            }

            return downloadedFile.getAbsolutePath();
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (IOException ignored) { }

            if (connection != null)
                connection.disconnect();
        }
    }

    @ApplozicInternal
    public Bitmap loadMessageImage(Context context, String url) {
        try {
            Bitmap attachedImage = null;

            if (attachedImage == null) {
                InputStream in = new java.net.URL(url).openStream();
                if (in != null) {
                    attachedImage = BitmapFactory.decodeStream(in);
                }
            }
            return attachedImage;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            Utils.printLog(context, TAG, "File not found on server: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Utils.printLog(context, TAG, "Exception fetching file from server: " + ex.getMessage());
        }

        return null;
    }

    @ApplozicInternal
    public String uploadBlobImage(String path, Handler handler, String oldMessageKey) throws
            UnsupportedEncodingException {
        try {

            ApplozicMultipartUtility multipart = new ApplozicMultipartUtility(getUploadURL(), "UTF-8", context);
            if (ApplozicClient.getInstance(context).isS3StorageServiceEnabled()) {
                multipart.addFilePart("file", new File(path), handler, oldMessageKey);
            } else {
                multipart.addFilePart("files[]", new File(path), handler, oldMessageKey);
            }
            return multipart.getResponse();
//            return new URLServiceProvider(context).getMultipartFile(path, handler).getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //ApplozicInternal: private
    public String getUploadURL() {
        String fileUrl = new URLServiceProvider(context).getFileUploadUrl();
        return fileUrl;
    }

    //ApplozicInternal: default
    public Bitmap downloadBitmap(Contact contact, Channel channel) {
        HttpURLConnection connection = null;
        MarkStream inputStream = null;
        try {
            if (contact != null) {
                connection = openHttpConnection(contact.getImageURL());
            } else {
                connection = openHttpConnection(channel.getImageUrl());
            }
            if (connection != null) {
                if (connection.getResponseCode() == 200) {
                    inputStream = new MarkStream(connection.getInputStream());
                    BitmapFactory.Options optionsBitmap = new BitmapFactory.Options();
                    optionsBitmap.inJustDecodeBounds = true;
                    inputStream.allowMarksToExpire(false);
                    long mark = inputStream.setPos(MARK);
                    BitmapFactory.decodeStream(inputStream, null, optionsBitmap);
                    inputStream.resetPos(mark);
                    optionsBitmap.inJustDecodeBounds = false;
                    optionsBitmap.inSampleSize = ImageUtils.calculateInSampleSize(optionsBitmap, 100, 50);
                    Bitmap attachedImage = BitmapFactory.decodeStream(inputStream, null, optionsBitmap);
                    inputStream.allowMarksToExpire(true);
                    return attachedImage;
                } else {
                    Utils.printLog(context, TAG, "Download is failed response code is ...." + connection.getResponseCode());
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            Utils.printLog(context, TAG, "Image not found on server: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            Utils.printLog(context, TAG, "Exception fetching file from server: " + ex.getMessage());
        } catch (Throwable t) {

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    //ApplozicInternal: private
    public String getThumbnailParentDir(String filePath) {
        String[] parts = getParts(filePath);
        String thumbnailDir = "";

        for (int i = 0; i < parts.length - 1; i++) {
            thumbnailDir += (parts[i] + "/");
        }
        thumbnailDir = thumbnailDir + ".Thumbnail/";
        return thumbnailDir;
    }

    /**
     * This path is supposed to be the same as the path where video thumbnails are downloaded.
     * see {@link FileClientService#downloadAndSaveThumbnailImage(Context, Message, int, int)}
     */
    public String getThumbnailPath(String filePath) { //ApplozicInternal: default
        String thumbnailParentDir = getThumbnailParentDir(filePath);
        File dir = new File(thumbnailParentDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return thumbnailParentDir + getVideoThumbnailFileNameForLocalGeneration(filePath);
    }

    //ApplozicInternal: default
    public Bitmap createThumbnailFileInLocalStorageAndReturnBitmap(String filePath) {
        Bitmap videoThumbnail;
        OutputStream fOut;
        File file = new File(getThumbnailParentDir(filePath), getVideoThumbnailFileNameForLocalGeneration(filePath));
        try {
            file.createNewFile();
            fOut = new FileOutputStream(file);
            videoThumbnail = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            videoThumbnail.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return videoThumbnail;
    }

    /**
     * This methods aims to save the video thumbnail in the same location where it will save downloaded thumbnails from the server.
     * see {@link FileClientService#downloadAndSaveThumbnailImage(Context, Message, int, int)}
     */
    @ApplozicInternal
    public Bitmap getOrCreateVideoThumbnail(String filePath) {
        String videoThumbnailPath = getThumbnailPath(filePath);

        Bitmap videoThumbnail;
        if (new File(videoThumbnailPath).exists()) {
            videoThumbnail = BitmapFactory.decodeFile(videoThumbnailPath);
        } else {
            videoThumbnail = createThumbnailFileInLocalStorageAndReturnBitmap(filePath);
        }

        return videoThumbnail;
    }

    @ApplozicInternal
    public String uploadProfileImage(String path) throws UnsupportedEncodingException {
        try {
            ApplozicMultipartUtility multipart = new ApplozicMultipartUtility(profileImageUploadURL(), "UTF-8", context);
            multipart.addFilePart("file", new File(path), null, null);
            return multipart.getResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @ApplozicInternal
    public Bitmap loadMessageImage(Context context, Conversation conversation) {
        try {
            if (conversation == null) {
                return null;
            }
            Bitmap attachedImage = ImageUtils.getBitMapFromLocalPath(conversation.getTopicLocalImageUri());
            if (attachedImage != null) {
                return attachedImage;
            }
            Bitmap bitmap = downloadProductImage(conversation);
            if (bitmap != null) {
                File file = FileClientService.getFilePath("topic_" + conversation.getId(), context.getApplicationContext(), "image", true);
                String imageLocalPath = ImageUtils.saveImageToInternalStorage(file, bitmap);
                conversation.setTopicLocalImageUri(imageLocalPath);
            }
            if (!TextUtils.isEmpty(conversation.getTopicLocalImageUri())) {
                ConversationService.getInstance(context).updateTopicLocalImageUri(conversation.getTopicLocalImageUri(), conversation.getId());
            }
            return bitmap;

        } catch (Exception e) {

        }
        return null;
    }

    //ApplozicInternal: private
    public Bitmap downloadProductImage(Conversation conversation) {
        TopicDetail topicDetail = (TopicDetail) GsonUtils.getObjectFromJson(conversation.getTopicDetail(), TopicDetail.class);
        if (TextUtils.isEmpty(topicDetail.getLink())) {
            return null;
        }
        HttpURLConnection connection = null;
        MarkStream inputStream = null;
        try {
            if (conversation != null) {
                connection = openHttpConnection(topicDetail.getLink());
            }
            if (connection != null) {
                if (connection.getResponseCode() == 200) {
                    inputStream = new MarkStream(connection.getInputStream());
                    BitmapFactory.Options optionsBitmap = new BitmapFactory.Options();
                    optionsBitmap.inJustDecodeBounds = true;
                    inputStream.allowMarksToExpire(false);
                    long mark = inputStream.setPos(MARK);
                    BitmapFactory.decodeStream(inputStream, null, optionsBitmap);
                    inputStream.resetPos(mark);
                    optionsBitmap.inJustDecodeBounds = false;
                    optionsBitmap.inSampleSize = ImageUtils.calculateInSampleSize(optionsBitmap, 100, 50);
                    Bitmap attachedImage = BitmapFactory.decodeStream(inputStream, null, optionsBitmap);
                    inputStream.allowMarksToExpire(true);
                    return attachedImage;
                } else {
                    return null;
                }
            }
        } catch (FileNotFoundException ex) {
            Utils.printLog(context, TAG, "Image not found on server: " + ex.getMessage());
        } catch (Exception ex) {
            Utils.printLog(context, TAG, "Exception fetching file from server: " + ex.getMessage());
        } catch (Throwable t) {

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @ApplozicInternal
    public void writeFile(Uri uri, File file) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = context.getContentResolver().openInputStream(uri);
            byte[] buffer = new byte[1024];
            int bytesRead = -1;
            out = new FileOutputStream(file);
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (in != null && out != null) {
                try {
                    out.flush();
                    out.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * Infers the file format from the mime type, if possible.
     *
     * @param mimeType the mime type
     * @return the file extension
     */
    public @Nullable String getFileFormatFromMimeType(@NonNull String mimeType) {
        String[] array = mimeType.split("/");
        String fileFormat = null;

        if (array.length > 1) {
            fileFormat = array[1];
        }

        if (TextUtils.isEmpty(fileFormat)) {
            return null;
        }

        return fileFormat;
    }

    /**
     * Saves the file to the Applozic apps media/file folder.
     *
     * <p>Name of the file will be set using {@link DateUtils#getDateStringForLocalFileName()}.
     * and the path from {@link FileClientService#getFilePath(String, Context, String)}.<p/>
     *
     * @param fromUri the uri of the file to save
     * @param mimeType mime type of the file to save
     * @return the saved file object
     */
    public @Nullable File saveFileToApplozicLocalStorage(@NonNull Uri fromUri, @Nullable String mimeType) {
        if (context != null && !TextUtils.isEmpty(mimeType)) {
            String fileFormat = getFileFormatFromMimeType(mimeType);

            if (TextUtils.isEmpty(fileFormat)) {
                return null;
            }

            String fileNameToWrite = DateUtils.getDateStringForLocalFileName() + "." + fileFormat;

            File mediaFile = FileClientService.getFilePath(fileNameToWrite, context, mimeType);

            writeFile(fromUri, mediaFile);

            return mediaFile;
        }
        return null;
    }
}
