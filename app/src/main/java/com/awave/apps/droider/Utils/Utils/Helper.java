package com.awave.apps.droider.Utils.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.awave.apps.droider.Elements.MainScreen.Feed;

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
    public static final String EXTRA_HEADER_IMAGE = "com.awave.apps.droider.Elements.EXTRA_HEADER_IMAGE";
    public static final String EXTRA_SHORT_DESCRIPTION = "com.awave.droider.Elements.EXTRA_SHORT_DESCRIPTION";

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

    public static void checkInternetConnection(final Context context)
    {
        new AlertDialog.Builder(context).setTitle("Соединение прервано").setMessage("Проверьте своё соединение с интернетом")
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).create().show();
    }


}
