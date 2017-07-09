package com.apps.wow.droider.Model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringEscapeUtils

data class Post(@SerializedName("picture_basic") @Expose var pictureBasic: String? = null,
                @SerializedName("picture_wide") @Expose var pictureWide: String? = null,
                @SerializedName("title") @Expose private var title: String? = null,
                @SerializedName("text") @Expose private var description: String? = null,
                @SerializedName("url") @Expose var url: String? = null,
                @SerializedName("date") @Expose var date: String? = null,
                @SerializedName("is_important") @Expose val isImportant: Boolean? = null,
                @SerializedName("is_hot") @Expose val isHot: Boolean? = null,
                @SerializedName("comments") @Expose var comments: String? = null,
                @SerializedName("views") @Expose var views: Int? = null) : Parcelable {

    var titleValue = title
        get(): String? {
            return StringEscapeUtils.unescapeHtml4(title)
        }

    var descriptionValue = title
        get(): String? {
            return StringEscapeUtils.unescapeHtml4(description)
        }

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

    constructor(`in`: Parcel) : this() {
        pictureBasic = `in`.readString()
        pictureWide = `in`.readString()
        title = `in`.readString()
        description = `in`.readString()
        url = `in`.readString()
        date = `in`.readString()
        comments = `in`.readString()
    }

    val CREATOR: Parcelable.Creator<Post> = object : Parcelable.Creator<Post> {
        override fun createFromParcel(`in`: Parcel): Post {
            return Post(`in`)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}
