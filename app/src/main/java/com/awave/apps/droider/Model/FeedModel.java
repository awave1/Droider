package com.awave.apps.droider.Model;

public class FeedModel {
    private static final String TAG = "FeedItem";

    private String mTitle;
    private String mDescription;
    private String mUrl;
    private String mImgUrl;
    private int drCastImg;

    public void setDrCastImg(int drCastImg) {
        this.drCastImg = drCastImg;
    }

    public int getDrCastImg() {

        return drCastImg;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getImgUrl() {
        return mImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        mImgUrl = imgUrl;
    }
}
