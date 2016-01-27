package com.awave.apps.droider.Utils.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

/**
 * Created by awave on 2016-01-23.
 */
public class Helper {
    public static final String HOME_URL = "http://droider.ru/page/";
    public static final String NEWS_URL = "http://droider.ru/category/news/page/";
    public static final String APPS_URL = "http://droider.ru/category/apps_and_games/programs/page/";
    public static final String GAMES_URL = "http://droider.ru/category/apps_and_games/games/page/";
    public static final String VIDEOS_URL = "http://droider.ru/category/video/page/";

    public static final String EXTRA_ARTICLE_TITLE = "com.awave.apps.droider.Elements.EXTRA_ARTICLE_TITLE";
    public static final String EXTRA_FEED_URL = "com.awave.apps.droider.Elements.EXTRA_FEED_URL";


    public static String youtubeVideo;
    public static CharSequence trimWhiteSpace(CharSequence src){
        if (src == null){
            return "";
        }

        int i = src.length();
        while (i-- >= 0 && Character.isWhitespace(src.charAt(i))){

        }
        return src.subSequence(0, i+1);
    }

    public static String getYoutubeImg(String src){
        String img = "";
        if(!src.equals("")) {
            img = "http://img.youtube.com/vi/" + src.substring(src.indexOf("embed/") + 6) + "/0.jpg";
            return img;
        }
        return img;
    }

    public static String trimYoutubeId(String src){
        return src.substring(src.indexOf("embed/") + 6);
    }

    public static void setYoutubeVideo(String src){
        youtubeVideo = src;
    }

    public static String getYoutubeVideo(){
        return youtubeVideo;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivity.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static AlertDialog createDialog(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        builder.setTitle(title);
        builder.setMessage(message);

        return builder.create();
    }
}
