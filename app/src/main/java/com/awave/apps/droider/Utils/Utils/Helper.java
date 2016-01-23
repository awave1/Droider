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

    public static class Blur {
        private static final float BITMAP_SCALE = 0.4f;
        private static float BLUR_RADIUS = 10.5f; // 8.5f

        public static float getBlurRadius() {
            return BLUR_RADIUS;
        }

        public static void setBlurRadius(float blurRadius) {
            BLUR_RADIUS = blurRadius;
        }

        public static Bitmap blur (Context context, Bitmap original){
            int w = Math.round(original.getWidth() * BITMAP_SCALE);
            int h = Math.round(original.getHeight() * BITMAP_SCALE);

            Bitmap input = Bitmap.createScaledBitmap(original, w, h, false);
            Bitmap output = Bitmap.createBitmap(input);

            RenderScript renderScript = RenderScript.create(context);
            ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

            Allocation tempIn = Allocation.createFromBitmap(renderScript, input);
            Allocation tempOut = Allocation.createFromBitmap(renderScript, output);

            intrinsicBlur.setRadius(BLUR_RADIUS);
            intrinsicBlur.setInput(tempIn);
            intrinsicBlur.forEach(tempOut);
            tempOut.copyTo(output);

            return output;
        }

        public static Bitmap onScrollBlur(Bitmap original, float blurRadius, Context context){
            int w = Math.round(original.getWidth() * BITMAP_SCALE);
            int h = Math.round(original.getHeight() * BITMAP_SCALE);

            Bitmap input = Bitmap.createScaledBitmap(original, w, h, false);
            Bitmap output = Bitmap.createBitmap(input);

            RenderScript renderScript = RenderScript.create(context);
            ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

            Allocation tempIn = Allocation.createFromBitmap(renderScript, input);
            Allocation tempOut = Allocation.createFromBitmap(renderScript, output);

            intrinsicBlur.setRadius(blurRadius);
            intrinsicBlur.setInput(tempIn);
            intrinsicBlur.forEach(tempOut);
            tempOut.copyTo(output);
            renderScript.destroy();

            return output;
        }

        public static class AsyncBlurImage extends AsyncTask<String, Void, Bitmap> {
            private ImageView image;
            private Drawable drawable;
            private FrameLayout frameLayout;

            private Context mContext;

            public AsyncBlurImage(ImageView imageView, Context context) {
                this.image = imageView;
                this.mContext = context;
            }

            public AsyncBlurImage(Drawable drawable, Context context){
                this.drawable = drawable;
                this.mContext = context;
            }

            public AsyncBlurImage (FrameLayout frameLayout, Context context){
                this.frameLayout = frameLayout;
                this.mContext = context;
            }

            @Override
            protected Bitmap doInBackground(String... src) {
                Bitmap blurred = null;
                Bitmap original = null;
                try {
                    original = Picasso.with(mContext).load(src[0]).get();
                    blurred = Helper.Blur.blur(mContext, original);
                }
                catch (IOException e){
                    Log.d(AsyncBlurImage.class.getSimpleName(), "Failed to load image");
                }
                return blurred;
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                int sdkVer = Build.VERSION.SDK_INT;
                if (sdkVer < Build.VERSION_CODES.JELLY_BEAN){
                    frameLayout.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), result));
                }
                else {
                    frameLayout.setBackground(new BitmapDrawable(mContext.getResources(), result));
                }
            }
        }
    }
}
