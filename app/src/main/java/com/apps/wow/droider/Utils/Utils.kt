package com.apps.wow.droider.Utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.util.Log
import android.util.TypedValue
import android.view.Window
import android.view.WindowManager
import com.apps.wow.droider.Article.ArticlePresenter.Companion.TAG
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.common.Priority
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Utils {

    val HOME_URL = "http://droider.ru"

    val SLUG_MAIN = "main"
    val SLUG_ANDROID = "android"
    val SLUG_APPLE = "apple"
    val SLUG_GAGETS = "gadgets"
    val SLUG_NEW_GAMES = "new_games"
    val SLUG_FROM_INTERNET = "from_internet"
    val SLUG_VIDEO = "video"
    val SLUG_PODCAST = "podcast"

    val CATEGORY_MAIN = "0"
    val CATEGORY_ANDROID = "17459"
    val CATEGORY_APPLE = "17460"
    val CATEGORY_GAGETS = "17461"
    val CATEGORY_NEW_GAMES = "17932"
    val CATEGORY_FROM_INTERNET = "17463"
    val CATEGORY_VIDEO = "260"
    val CATEGORY_PODCAST = "17931"

    val DEFAULT_COUNT = 20

    val EXTRA_ARTICLE_TITLE = "com.apps.wow.droider.Elements.EXTRA_ARTICLE_TITLE"
    val EXTRA_ARTICLE_URL = "com.apps.wow.droider.Elements.EXTRA_ARTICLE_URL"
    val EXTRA_CATEGORY = "com.apps.wow.droider.CATEGORY"
    val EXTRA_SLUG = "com.apps.wow.droider.EXTRA_SLUG"
    val EXTRA_SHORT_DESCRIPTION = "com.awave.droider.Elements.EXTRA_SHORT_DESCRIPTION"
    val EXTRA_ARTICLE_X_TOUCH_COORDINATE = "EXTRA_ARTICLE_X_TOUCH_COORDINATE"
    val EXTRA_ARTICLE_Y_TOUCH_COORDINATE = "EXTRA_ARTICLE_Y_TOUCH_COORDINATE"
    val EXTRA_ARTICLE_IMG_URL = "EXTRA_ARTICLE_IMG_URL"
    var CIRCULAR_REVIVAL_ANIMATION_RADIUS = 100

    fun getYoutubeImg(src: String): String {
        var img = ""
        if (src != "") {
            img = "http://img.youtube.com/vi/" + src.substring(src.indexOf("embed/") + 6) + "/0.jpg"
            return img
        }
        return img
    }

    fun trimYoutubeId(src: String): String {
        return src.substring(30)
    }

    fun isOnline(context: Context?): Boolean {
        if (context != null) {
            val connectivity = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivity.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }
        return false
    }

    fun enableWiFi(c: Context, wifi: Boolean) {
        val wifiConfiguration = c.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiConfiguration.isWifiEnabled = wifi
    }

    fun getRobotoFont(type: String, isItalic: Boolean, a: Activity): Typeface {
        if (!isItalic) {
            when (type) {
                "Thin" -> return Typeface.createFromAsset(a.assets, "fonts/Roboto-Thin.ttf")
                "Light" -> return Typeface.createFromAsset(a.assets, "fonts/Roboto-Light.ttf")
                "Medium" -> return Typeface.createFromAsset(a.assets, "fonts/Roboto-Medium.ttf")
                "Regular" -> return Typeface.createFromAsset(a.assets, "fonts/Roboto-Regular.ttf")
                "Bold" -> return Typeface.createFromAsset(a.assets, "fonts/Roboto-Bold.ttf")
                else -> return Typeface.createFromAsset(a.assets, "fonts/Roboto-Regular")
            }
        } else {
            when (type) {
                "Thin" -> return Typeface.createFromAsset(a.assets, "fonts/Roboto-ThinItalic.ttf")
                "Light" -> return Typeface.createFromAsset(a.assets, "fonts/Roboto-LightItalic.ttf")
                "Medium" -> return Typeface.createFromAsset(a.assets, "fonts/Roboto-MediumItalic.ttf")
                "Bold" -> return Typeface.createFromAsset(a.assets, "fonts/Roboto-BoldItalic.ttf")
                else -> return Typeface.createFromAsset(a.assets, "fonts/Roboto-MediumItalic.ttf")
            }
        }
    }

    fun drawableToBitmap(d: Drawable): Bitmap {
        val b: Bitmap
        if (d.mutate() is BitmapDrawable) {
            val bitmapDrawable = d
            if ((bitmapDrawable as BitmapDrawable).bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }

        if (d.intrinsicWidth <= 0 || d.intrinsicHeight <= 0) {
            b = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        } else {
            b = Bitmap.createBitmap(d.intrinsicWidth, d.intrinsicHeight,
                    Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(b)
        d.setBounds(0, 0, canvas.width, canvas.height)
        d.draw(canvas)
        return b
    }

    fun convertImageUrlToBitmap(imageUri: String, mContext: Context, callback : BitmapLoaded) {
        val imagePipeline = Fresco.getImagePipeline()

        val imageRequest = ImageRequestBuilder
                .newBuilderWithSource(Uri.parse(imageUri))
                .setRequestPriority(Priority.HIGH)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .build()


        val dataSource =
                imagePipeline.fetchDecodedImage(imageRequest, mContext)

        try {
            dataSource.subscribe(object : BaseBitmapDataSubscriber() {

                public override fun onNewResultImpl(bitmap: Bitmap?) {
                    if (bitmap == null) {
                        Log.d(TAG, "Bitmap data source returned success, but bitmap null.")
                        return
                    }

                    callback.readyToUse(bitmap)
                    // The bitmap provided to this method is only guaranteed to be around
                    // for the lifespan of this method. The image pipeline frees the
                    // bitmap's memory after this method has completed.
                    //
                    // This is fine when passing the bitmap to a system process as
                    // Android automatically creates a copy.
                    //
                    // If you need to keep the bitmap around, look into using a
                    // BaseDataSubscriber instead of a BaseBitmapDataSubscriber.
                }

                override fun onFailureImpl(p0: DataSource<CloseableReference<CloseableImage>>?) {
                    // stub
                }
            }, CallerThreadExecutor.getInstance())
        } finally {
            dataSource?.close()
        }
    }

    fun <T> createRxService(rxService: Class<T>, baseUrl: String, withLog: Boolean): T {
        if (withLog) {
            val rxRetrofit = Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .client(HttpClientWithLog())
                    .build()
            return rxRetrofit.create(rxService)
        } else {
            return Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .client(HttpClientWithoutLog())
                    .build()
                    .create(rxService)
        }
    }

    private fun HttpClientWithoutLog(): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.connectTimeout(15, TimeUnit.SECONDS)
        client.readTimeout(30, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)
        return client.build()
    }

    private fun HttpClientWithLog(): OkHttpClient {
        val client = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        client.addInterceptor(logging)
        client.connectTimeout(15, TimeUnit.SECONDS)
        client.readTimeout(30, TimeUnit.SECONDS)
        client.retryOnConnectionFailure(true)
        return client.build()
    }

    fun setStatusBarColor(color: String, window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor(color)
        }
    }

    fun setStatusBarColor(color: Int, window: Window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }
    }

    @ColorInt
    fun getThemeColor(
            context: Context,
            @AttrRes attributeColor: Int): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(attributeColor, value, true)
        return value.data
    }

}