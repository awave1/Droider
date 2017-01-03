
package com.apps.wow.droider.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {

    // For similar articles
    public Post(String title, String pictureWide, String url) {
        this.title = title;
        this.pictureWide = pictureWide;
        this.url = url;
    }

    @SerializedName("picture_basic")
    @Expose
    private String pictureBasic;
    @SerializedName("picture_wide")
    @Expose
    private String pictureWide;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("text")
    @Expose
    private String description;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("is_important")
    @Expose
    private Boolean isImportant;
    @SerializedName("is_hot")
    @Expose
    private Boolean isHot;
    @SerializedName("comments")
    @Expose
    private String comments;
    @SerializedName("views")
    @Expose
    private Integer views;

    public String getPictureBasic() {
        return pictureBasic;
    }

    public void setPictureBasic(String pictureBasic) {
        this.pictureBasic = pictureBasic;
    }

    public String getPictureWide() {
        return pictureWide;
    }

    public void setPictureWide(String pictureWide) {
        this.pictureWide = pictureWide;
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

    public void setText(String text) {
        this.description = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getIsImportant() {
        return isImportant;
    }

    public void setIsImportant(Boolean isImportant) {
        this.isImportant = isImportant;
    }

    public Boolean getIsHot() {
        return isHot;
    }

    public void setIsHot(Boolean isHot) {
        this.isHot = isHot;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

}
