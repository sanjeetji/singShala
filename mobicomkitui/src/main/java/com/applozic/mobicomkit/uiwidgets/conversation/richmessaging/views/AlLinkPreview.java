package com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.views;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.listners.AlCallback;
import com.applozic.mobicomkit.uiwidgets.AlCustomizationSettings;
import com.applozic.mobicomkit.uiwidgets.R;
import com.applozic.mobicomkit.uiwidgets.async.AlMessageMetadataUpdateTask;
import com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.models.AlLinkPreviewModel;
import com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.utils.AlRegexHelper;
import com.applozic.mobicommons.ApplozicService;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.task.AlAsyncTask;
import com.applozic.mobicommons.task.AlTask;
import com.bumptech.glide.Glide;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AlLinkPreview {
    private static final String TAG = "AlLinkPreview";

    public static final String LINK_PREVIEW_META_KEY = "KM_LINK_PREVIEW_META_KEY";
    private Context context;
    private Message message;
    private RelativeLayout urlLoadLayout;
    private AlCustomizationSettings alCustomizationSettings;
    private ImageView imageView;
    private TextView titleText;
    private TextView descriptionText;
    private ImageView imageOnlyView;

    public AlLinkPreview(Context context, Message message, RelativeLayout urlLoadLayout, AlCustomizationSettings alCustomizationSettings) {
        this.context = context;
        this.message = message;
        this.urlLoadLayout = urlLoadLayout;
        this.alCustomizationSettings = alCustomizationSettings;
        this.imageView = urlLoadLayout.findViewById(R.id.url_image);
        this.titleText = urlLoadLayout.findViewById(R.id.url_header);
        this.descriptionText = urlLoadLayout.findViewById(R.id.url_body);
        this.imageOnlyView = urlLoadLayout.findViewById(R.id.image_only_view);
    }

    public void createView() {
        AlLinkPreviewModel existingLinkModel = getUrlMetaModel();
        if (existingLinkModel != null) {
            updateViews(existingLinkModel);
        } else {
            urlLoadLayout.setVisibility(View.GONE);
            AlTask.execute(new UrlLoader(context, message, new AlCallback() {
                @Override
                public void onSuccess(Object response) {
                    updateViews((AlLinkPreviewModel) response);
                }

                @Override
                public void onError(Object error) {

                }
            }));
        }
    }

    public void updateViews(AlLinkPreviewModel linkPreviewModel) {
        if (linkPreviewModel != null && linkPreviewModel.hasLinkData()) {
            urlLoadLayout.setVisibility(View.VISIBLE);

            if (linkPreviewModel.hasImageOnly()) {
                toggleImageOnlyVisibility(true);
                Glide.with(context).load(linkPreviewModel.getImageLink()).into(imageOnlyView);
            } else {
                toggleImageOnlyVisibility(false);
                titleText.setText(linkPreviewModel.getTitle());
                descriptionText.setText(linkPreviewModel.getDescription());
                if (!TextUtils.isEmpty(linkPreviewModel.getImageLink())) {
                    imageView.setVisibility(View.VISIBLE);
                    Glide.with(context).load(linkPreviewModel.getImageLink()).into(imageView);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
        } else {
            urlLoadLayout.setVisibility(View.GONE);
        }

        urlLoadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, context.getString(R.string.opening_link), Toast.LENGTH_SHORT).show();
                AlTask.execute(new OpenLinkTask(context, message));
            }
        });
    }

    private void toggleImageOnlyVisibility(boolean showImageOnly) {
        imageOnlyView.setVisibility(showImageOnly ? View.VISIBLE : View.GONE);
        imageView.setVisibility(showImageOnly ? View.GONE : View.VISIBLE);
        titleText.setVisibility(showImageOnly ? View.GONE : View.VISIBLE);
        descriptionText.setVisibility(showImageOnly ? View.GONE : View.VISIBLE);
    }

    public AlLinkPreviewModel getUrlMetaModel() {
        try {
            if (message.getMetadata() != null && message.getMetadata().containsKey(LINK_PREVIEW_META_KEY)) {
                return (AlLinkPreviewModel) GsonUtils.getObjectFromJson(message.getMetadata().get(LINK_PREVIEW_META_KEY), AlLinkPreviewModel.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class UrlLoader extends AlAsyncTask<Void, AlLinkPreviewModel> {

        private WeakReference<Context> context;
        private Message message;
        private AlCallback callback;

        public UrlLoader(Context context, Message message, AlCallback callback) {
            this.context = new WeakReference<>(context);
            this.message = message;
            this.callback = callback;
        }

        @Override
        protected AlLinkPreviewModel doInBackground() {
            String validUrl = getValidUrl(message);
            AlLinkPreviewModel linkPreviewModel = null;
            try {
                if (!TextUtils.isEmpty(validUrl) && Pattern.compile(AlRegexHelper.IMAGE_PATTERN).matcher(validUrl).matches()) {
                    linkPreviewModel = new AlLinkPreviewModel();
                    linkPreviewModel.setImageLink(validUrl);
                } else {
                    Document document = Jsoup.connect(validUrl).get();
                    linkPreviewModel = getMetaTags(document, message);
                    if (TextUtils.isEmpty(linkPreviewModel.getTitle())) {
                        linkPreviewModel.setTitle(document.title());
                    }
                    if (!TextUtils.isEmpty(linkPreviewModel.getImageLink()) &&
                            !(linkPreviewModel.getImageLink().startsWith(AlRegexHelper.HTTP_PROTOCOL) || linkPreviewModel.getImageLink().startsWith(AlRegexHelper.HTTPS_PROTOCOL))) {
                        linkPreviewModel.setImageLink(getValidUrl(message) + linkPreviewModel.getImageLink());
                    }
                }
                return linkPreviewModel;
            } catch (HttpStatusException e) {
                if (linkPreviewModel == null) {
                    linkPreviewModel = new AlLinkPreviewModel();
                }
                linkPreviewModel.setInvalidUrl(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return linkPreviewModel;
        }

        @Override
        protected void onPostExecute(final AlLinkPreviewModel urlMetaModel) {
            if (callback != null) {
                if (urlMetaModel != null) {
                    if (urlMetaModel.hasLinkData()) {
                        Map<String, String> metadata = message.getMetadata();
                        if (metadata == null) {
                            metadata = new HashMap<>();
                        }
                        metadata.put(LINK_PREVIEW_META_KEY, GsonUtils.getJsonFromObject(urlMetaModel, AlLinkPreviewModel.class));
                        AlTask.execute(new AlMessageMetadataUpdateTask(context.get(), message.getKeyString(), metadata, new AlMessageMetadataUpdateTask.MessageMetadataListener() {
                            @Override
                            public void onSuccess(Context context, String message) {
                                callback.onSuccess(urlMetaModel);
                            }

                            @Override
                            public void onFailure(Context context, String error) {
                                callback.onError(error);
                            }
                        }));
                    }
                    callback.onSuccess(urlMetaModel);
                } else {
                    callback.onError(null);
                }
            }
            super.onPostExecute(urlMetaModel);
        }
    }

    public static class OpenLinkTask extends AlAsyncTask<Void, String> {
        private final Message message;
        private final WeakReference<Context> contextWeakReference;

        OpenLinkTask(Context context, Message message) {
            this.message = message;
            contextWeakReference = new WeakReference<>(context);
        }

        public void openUrl(@Nullable String url) {
            if (TextUtils.isEmpty(url) || contextWeakReference.get() == null) {
                return;
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            contextWeakReference.get().startActivity(browserIntent);
        }

        @Override
        protected String doInBackground() throws Exception {
            if (message == null) {
                return null;
            }

            return AlLinkPreview.getValidUrl(message);
        }

        @Override
        protected void onPostExecute(String validUrl) {
            openUrl(validUrl);
        }
    }

    private static AlLinkPreviewModel getMetaTags(Document doc, Message message) {
        AlLinkPreviewModel linkPreviewModel = new AlLinkPreviewModel();
        String url = getValidUrl(message);
        try {
            Elements elements = doc.getElementsByTag("meta");

            String title = doc.select("meta[property=og:title]").attr("content");

            Utils.printLog(ApplozicService.getAppContext(), "LinkTest", "Title : " + title);
            if (!TextUtils.isEmpty(title)) {
                linkPreviewModel.setTitle(title);
            } else {
                linkPreviewModel.setTitle(doc.title());
            }

            //getDescription
            String description = doc.select("meta[name=description]").attr("content");
            if (description.isEmpty() || description == null) {
                description = doc.select("meta[name=Description]").attr("content");
            }
            if (description.isEmpty() || description == null) {
                description = doc.select("meta[property=og:description]").attr("content");
            }
            if (description.isEmpty() || description == null) {
                description = "";
            }
            linkPreviewModel.setDescription(description);

            //getImages
            Elements imageElements = doc.select("meta[property=og:image]");
            if (imageElements.size() > 0) {
                String image = imageElements.attr("content");
                if (!TextUtils.isEmpty(image)) {
                    linkPreviewModel.setImageLink(resolveURL(url, image));
                }
            }
            if (TextUtils.isEmpty(linkPreviewModel.getImageLink())) {
                String src = doc.select("link[rel=image_src]").attr("href");
                if (!TextUtils.isEmpty(src)) {
                    linkPreviewModel.setImageLink(resolveURL(url, src));
                } else {
                    src = doc.select("link[rel=apple-touch-icon]").attr("href");
                    if (!TextUtils.isEmpty(src)) {
                        linkPreviewModel.setImageLink(resolveURL(url, src));
                    } else {
                        src = doc.select("link[rel=icon]").attr("href");
                        if (!TextUtils.isEmpty(src)) {
                            linkPreviewModel.setImageLink(resolveURL(url, src));
                        }
                    }
                }
            }

            //Favicon
            String src = doc.select("link[rel=apple-touch-icon]").attr("href");
            if (!TextUtils.isEmpty(src) && TextUtils.isEmpty(linkPreviewModel.getImageLink())) {
                linkPreviewModel.setImageLink(resolveURL(url, src));
            } else {
                src = doc.select("link[rel=icon]").attr("href");
                if (!TextUtils.isEmpty(src) && TextUtils.isEmpty(linkPreviewModel.getImageLink())) {
                    linkPreviewModel.setImageLink(resolveURL(url, src));
                }
            }

            for (Element element : elements) {
                if (element.hasAttr("property")) {
                    String strProperty = element.attr("property").toString().trim();
                    if (strProperty.equals("og:url")) {
                        linkPreviewModel.setUrl(element.attr("content").toString());
                    }
                    if (strProperty.equals("og:site_name") && TextUtils.isEmpty(linkPreviewModel.getTitle())) {
                        linkPreviewModel.setTitle(element.attr("content").toString());
                    }
                }
            }

            if (TextUtils.isEmpty(linkPreviewModel.getUrl())) {
                URI uri = null;
                try {
                    uri = new URI(url);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                linkPreviewModel.setUrl(uri == null ? url : uri.getHost());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return linkPreviewModel;
    }

    public static String getExpandedURLIfShortened(String url) {
        final String LOCATION_HEADER_KEY = "Location";
        URLConnection urlConn = connectURL(url);
        if (urlConn != null) {
            urlConn.getHeaderFields();
            String locationHeaderURL = urlConn.getHeaderField(LOCATION_HEADER_KEY);
            if (!TextUtils.isEmpty(locationHeaderURL)) {
                return locationHeaderURL;
            }
            String connectionURL = urlConn.getURL().toString();
            if (!TextUtils.isEmpty(connectionURL)) {
                return connectionURL;
            }
        }
        return url;
    }

    public static URLConnection connectURL(String strURL) {
        URLConnection conn = null;
        try {
            URL inputURL = new URL(strURL);
            conn = inputURL.openConnection(Proxy.NO_PROXY);
        } catch (MalformedURLException e) {
            Log.d(TAG, "URL not valid for showing link preview.");
        } catch (IOException ioe) {
            Log.d(TAG, "Can not connect to the URL for showing link preview.");
        }
        return conn;
    }

    //run this in background thread
    private static String getValidUrl(Message message) {
        String url = message.getFirstUrl();
        if (!TextUtils.isEmpty(url) && !(url.regionMatches(true, 0, AlRegexHelper.HTTP_PROTOCOL, 0, AlRegexHelper.HTTP_PROTOCOL.length()) || url.regionMatches(true, 0, AlRegexHelper.HTTPS_PROTOCOL, 0, AlRegexHelper.HTTPS_PROTOCOL.length()))) {
            url = AlRegexHelper.HTTP_PROTOCOL + url;
        }
        url = getExpandedURLIfShortened(url);
        return url;
    }

    private static String resolveURL(String url, String part) {
        if (URLUtil.isValidUrl(part)) {
            return part;
        } else {
            URI baseUri = null;
            try {
                baseUri = new URI(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if (baseUri != null) {
                baseUri = baseUri.resolve(part);
                return baseUri.toString();
            }
            return null;
        }
    }
}
