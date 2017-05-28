package com.apps.wow.droider.Model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FeedModel(@SerializedName("has_more") @Expose val hasMore: Boolean? = null,
                     @SerializedName("posts") @Expose val posts: ArrayList<Post>)
