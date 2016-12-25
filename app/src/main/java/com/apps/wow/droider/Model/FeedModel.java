
package com.apps.wow.droider.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class FeedModel {

    @SerializedName("has_more")
    @Expose
    private Boolean hasMore;
    @SerializedName("posts")
    @Expose
    private ArrayList<Post> posts = null;

    //TODO заюзать
    public Boolean getHasMore() {
        return hasMore;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }
}
