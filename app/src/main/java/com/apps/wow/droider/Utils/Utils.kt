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
import android.net.wifi.WifiManager
import android.os.Build
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.v8.renderscript.Allocation
import android.support.v8.renderscript.Element
import android.support.v8.renderscript.RenderScript
import android.support.v8.renderscript.ScriptIntrinsicBlur
import android.util.TypedValue
import android.view.Window
import android.view.WindowManager
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
        if (d is BitmapDrawable) {
            val bitmapDrawable = d
            if (bitmapDrawable.bitmap != null) {
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

    fun applyBlur(drawable: Drawable?, context: Context): Drawable {
        val fromDrawable = drawableToBitmap(drawable!!)
        val width = Math.round(fromDrawable.width * 0.8f)
        val height = Math.round(fromDrawable.height * 0.8f)

        val inBitmap = Bitmap.createScaledBitmap(fromDrawable, width, height, false)
        val outBitmap = Bitmap.createBitmap(inBitmap)

        val renderScript = RenderScript.create(context)
        val blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))

        val `in` = Allocation.createFromBitmap(renderScript, inBitmap)
        val out = Allocation.createFromBitmap(renderScript, outBitmap)

        blur.setRadius(11.5f)
        blur.setInput(`in`)
        blur.forEach(out)

        out.copyTo(outBitmap)
        renderScript.destroy()

        return BitmapDrawable(context.resources, outBitmap)
    }

    fun applyBlur(bitmap: Bitmap, context: Context): Bitmap {
        val rs = RenderScript.create(context)
        val bitmapCopy: Bitmap
        val width = Math.round(bitmap.width * 0.8f)
        val height = Math.round(bitmap.height * 0.8f)

        if (bitmap.config == Bitmap.Config.ARGB_8888) {
            bitmapCopy = bitmap
        } else {
            bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        }

        val outBitmap = Bitmap.createBitmap(width, height, bitmapCopy.config)

        val `in` = Allocation.createFromBitmap(rs, bitmapCopy, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT)
        val out = Allocation.createTyped(rs, `in`.type)

        val blur = ScriptIntrinsicBlur.create(rs, out.element)
        blur.setRadius(11.5f)
        blur.setInput(`in`)
        blur.forEach(out)

        out.copyTo(bitmap)
        rs.destroy()

        return outBitmap
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
