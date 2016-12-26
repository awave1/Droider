package com.apps.wow.droider.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Utils {

  public static final String HOME_URL = "http://droider.ru";

  public static final String SLUG_MAIN= "main";
  public static final String SLUG_ANDROID = "android";
  public static final String SLUG_APPLE = "apple";
  public static final String SLUG_GAGETS = "gadgets";
  public static final String SLUG_NEW_GAMES = "new_games";
  public static final String SLUG_FROM_INTERNET = "from_internet";
  public static final String SLUG_VIDEO = "video";
  public static final String SLUG_PODCAST = "podcast";

  public static final String CATEGORY_MAIN= "0";
  public static final String CATEGORY_ANDROID = "17459";
  public static final String CATEGORY_APPLE = "17460";
  public static final String CATEGORY_GAGETS = "17461";
  public static final String CATEGORY_NEW_GAMES = "17932";
  public static final String CATEGORY_FROM_INTERNET = "17463";
  public static final String CATEGORY_VIDEO = "260";
  public static final String CATEGORY_PODCAST = "17931";


  public static final int DEFAULT_COUNT = 20;

  public static final String EXTRA_ARTICLE_TITLE =
      "com.apps.wow.droider.Elements.EXTRA_ARTICLE_TITLE";
  public static final String EXTRA_ARTICLE_URL =
      "com.apps.wow.droider.Elements.EXTRA_ARTICLE_URL";
  public static final String EXTRA_CATEGORY = "com.apps.wow.droider.CATEGORY";
  public static final String EXTRA_SLUG = "com.apps.wow.droider.EXTRA_SLUG";
  public static final String EXTRA_SHORT_DESCRIPTION =
      "com.awave.droider.Elements.EXTRA_SHORT_DESCRIPTION";
  public static final String EXTRA_ARTICLE_X_TOUCH_COORDINATE = "EXTRA_ARTICLE_X_TOUCH_COORDINATE";
  public static final String EXTRA_ARTICLE_Y_TOUCH_COORDINATE = "EXTRA_ARTICLE_Y_TOUCH_COORDINATE";
  private static final String TAG = "Helper";
  public static final String EXTRA_ARTICLE_IMG_URL = "EXTRA_ARTICLE_IMG_URL";
  public static int CIRCULAR_REVIVAL_ANIMATION_RADIUS = 100;

  public static String getYoutubeImg(String src) {
    String img = "";
    if (!src.equals("")) {
      img = "http://img.youtube.com/vi/" + src.substring(src.indexOf("embed/") + 6) + "/0.jpg";
      return img;
    }
    return img;
  }

  public static String trimYoutubeId(String src) {
    return src.substring(30);
  }

  public static boolean isOnline(Context context) {
    if (context != null) {
      ConnectivityManager connectivity =
              (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo netInfo = connectivity.getActiveNetworkInfo();
      return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    return false;
  }

  public static void enableWiFi(Context c, boolean wifi) {
    WifiManager wifiConfiguration = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
    wifiConfiguration.setWifiEnabled(wifi);
  }

  public static Typeface getRobotoFont(String type, boolean isItalic, Activity a) {
    if (!isItalic) {
      switch (type) {
        case "Thin":
          return Typeface.createFromAsset(a.getAssets(), "fonts/Roboto-Thin.ttf");
        case "Light":
          return Typeface.createFromAsset(a.getAssets(), "fonts/Roboto-Light.ttf");
        case "Medium":
          return Typeface.createFromAsset(a.getAssets(), "fonts/Roboto-Medium.ttf");
        case "Regular":
          return Typeface.createFromAsset(a.getAssets(), "fonts/Roboto-Regular.ttf");
        case "Bold":
          return Typeface.createFromAsset(a.getAssets(), "fonts/Roboto-Bold.ttf");
        default:
          return Typeface.createFromAsset(a.getAssets(), "fonts/Roboto-Regular");
      }
    } else {
      switch (type) {
        case "Thin":
          return Typeface.createFromAsset(a.getAssets(), "fonts/Roboto-ThinItalic.ttf");
        case "Light":
          return Typeface.createFromAsset(a.getAssets(), "fonts/Roboto-LightItalic.ttf");
        case "Medium":
          return Typeface.createFromAsset(a.getAssets(), "fonts/Roboto-MediumItalic.ttf");
        case "Bold":
          return Typeface.createFromAsset(a.getAssets(), "fonts/Roboto-BoldItalic.ttf");
        default:
          return Typeface.createFromAsset(a.getAssets(), "fonts/Roboto-MediumItalic.ttf");
      }
    }
  }

  public static Bitmap drawableToBitmap(Drawable d) {
    Bitmap b;
    if (d instanceof BitmapDrawable) {
      BitmapDrawable bitmapDrawable = (BitmapDrawable) d;
      if (bitmapDrawable.getBitmap() != null) {
        return bitmapDrawable.getBitmap();
      }
    }

    if (d.getIntrinsicWidth() <= 0 || d.getIntrinsicHeight() <= 0) {
      b = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    } else {
      b = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(),
          Bitmap.Config.ARGB_8888);
    }

    Canvas canvas = new Canvas(b);
    d.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    d.draw(canvas);
    return b;
  }

  public static Drawable applyBlur(Drawable drawable, Context context) {
    Bitmap fromDrawable = drawableToBitmap(drawable);
    int width = Math.round(fromDrawable.getWidth() * 0.8f);
    int height = Math.round(fromDrawable.getHeight() * 0.8f);

    Bitmap inBitmap = Bitmap.createScaledBitmap(fromDrawable, width, height, false);
    Bitmap outBitmap = Bitmap.createBitmap(inBitmap);

    RenderScript renderScript = RenderScript.create(context);
    ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

    Allocation in = Allocation.createFromBitmap(renderScript, inBitmap);
    Allocation out = Allocation.createFromBitmap(renderScript, outBitmap);

    blur.setRadius(11.5f);
    blur.setInput(in);
    blur.forEach(out);

    out.copyTo(outBitmap);
    renderScript.destroy();

    return new BitmapDrawable(context.getResources(), outBitmap);
  }

  public static Bitmap applyBlur(Bitmap bitmap, Context context) {
    RenderScript rs = RenderScript.create(context);
    Bitmap bitmapCopy;
    int width = Math.round(bitmap.getWidth() * 0.8f);
    int height = Math.round(bitmap.getHeight() * 0.8f);

    if (bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
      bitmapCopy = bitmap;
    } else {
      bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    Bitmap outBitmap = Bitmap.createBitmap(width, height, bitmapCopy.getConfig());

    Allocation in =
        Allocation.createFromBitmap(rs, bitmapCopy, Allocation.MipmapControl.MIPMAP_NONE,
            Allocation.USAGE_SCRIPT);
    Allocation out = Allocation.createTyped(rs, in.getType());

    ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, out.getElement());
    blur.setRadius(11.5f);
    blur.setInput(in);
    blur.forEach(out);

    out.copyTo(bitmap);
    rs.destroy();

    return outBitmap;
  }

  public static <T> T createRxService(final Class<T> rxService, String baseUrl, boolean withLog) {
    if (withLog) {
      Retrofit rxRetrofit =
          new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
              .addConverterFactory(GsonConverterFactory.create())
              .baseUrl(baseUrl)
              .client(HttpClientWithLog())
              .build();
      return rxRetrofit.create(rxService);
    } else {
      return new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .baseUrl(baseUrl)
          .client(HttpClientWithoutLog())
          .build()
          .create(rxService);
    }
  }

  private static OkHttpClient HttpClientWithoutLog() {
    OkHttpClient.Builder client = new OkHttpClient.Builder();
    client.connectTimeout(15, TimeUnit.SECONDS);
    client.readTimeout(30, TimeUnit.SECONDS);
    client.retryOnConnectionFailure(true);
    return client.build();
  }

  private static OkHttpClient HttpClientWithLog() {
    OkHttpClient.Builder client = new OkHttpClient.Builder();
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
    client.addInterceptor(logging);
    client.connectTimeout(15, TimeUnit.SECONDS);
    client.readTimeout(30, TimeUnit.SECONDS);
    client.retryOnConnectionFailure(true);
    return client.build();
  }
}
