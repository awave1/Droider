
package com.apps.wow.droider.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringEscapeUtils;

import android.os.Parcel;
import android.os.Parcelable;

public class Post implements Parcelable {

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

    protected Post(Parcel in) {
        pictureBasic = in.readString();
        pictureWide = in.readString();
        title = in.readString();
        description = in.readString();
        url = in.readString();
        date = in.readString();
        comments = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pictureBasic);
        dest.writeString(pictureWide);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeString(date);
        dest.writeString(comments);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getPictureBasic() {
        return pictureBasic;
    }

    public String getPictureWide() {
        return pictureWide;
    }

    public String getTitle() {
        return StringEscapeUtils.unescapeHtml4(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return StringEscapeUtils.unescapeHtml4(description);
    }

    public void setText(String text) {
        this.description = text;
    }

    public String getUrl() {
        return url;
    }

    public String getDate() {
        return date;
    }

    public Boolean getIsImportant() {
        return isImportant;
    }

    public Boolean getIsHot() {
        return isHot;
    }

    public String getComments() {
        return comments;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

}
