
package com.apps.wow.droider.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class NewFeedModel {

    @SerializedName("has_more")
    @Expose
    private Boolean hasMore;
    @SerializedName("posts")
    @Expose
    private ArrayList<Post> posts = null;

    public Boolean getHasMore() {
        return hasMore;
    }

    public void setHasMore(Boolean hasMore) {
        this.hasMore = hasMore;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

}
