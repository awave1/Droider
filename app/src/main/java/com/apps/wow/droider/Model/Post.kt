package com.apps.wow.droider.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Post(@SerializedName("picture_basic") @Expose var pictureBasic: String? = null,
                @SerializedName("picture_wide") @Expose var pictureWide: String? = null,
                @SerializedName("title") @Expose var title: String? = null,
                @SerializedName("text") @Expose  var description: String? = null,
                @SerializedName("url") @Expose var url: String? = null,
                @SerializedName("date") @Expose var date: String? = null,
                @SerializedName("is_important") @Expose val isImportant: Boolean? = null,
                @SerializedName("is_hot") @Expose val isHot: Boolean? = null,
                @SerializedName("comments") @Expose var comments: String? = null,
                @SerializedName("views") @Expose var views: Int? = null) : Parcelable {

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(pictureBasic)
        dest.writeString(pictureWide)
        dest.writeString(title)
        dest.writeString(description)
        dest.writeString(url)
        dest.writeString(date)
        dest.writeString(comments)
    }

    override fun describeContents(): Int {
        return 0
    }
}
