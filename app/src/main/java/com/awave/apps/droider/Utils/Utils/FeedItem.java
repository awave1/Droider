package com.awave.apps.droider.Utils.Utils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by awave on 2016-01-23.
 */
public class FeedItem {

    private String title;
    private String link;
    private String description;
    private Date date;
    private ArrayList<String> categories;
    private String img;
    private String tag;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
