package com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.models;

import android.text.TextUtils;

import com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.utils.AlRegexHelper;
import com.applozic.mobicommons.json.JsonMarker;

import java.util.regex.Pattern;

public class AlLinkPreviewModel extends JsonMarker {

    private String url;
    private String imageLink;
    private String title;
    private String description;
    private boolean invalidUrl = false;

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isInvalidUrl() {
        return invalidUrl;
    }

    public void setInvalidUrl(boolean invalidUrl) {
        this.invalidUrl = invalidUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean hasLinkData() {
        return !TextUtils.isEmpty(imageLink) || !TextUtils.isEmpty(title) || !TextUtils.isEmpty(description) || isInvalidUrl();
    }

    public boolean hasImageOnly() {
        return !TextUtils.isEmpty(imageLink)
                && Pattern.compile(AlRegexHelper.IMAGE_PATTERN).matcher(imageLink).matches()
                && TextUtils.isEmpty(title)
                && TextUtils.isEmpty(description);
    }
}
