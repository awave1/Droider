package com.awave.apps.droider.Utils.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Blur {
    private static final String TAG = "Blur";
    private static final float BITMAP_SCALE = 0.4f;
    private static float BLUR_RADIUS = 8.f; // 8.5f

    public static float getBlurRadius() {
        return BLUR_RADIUS;
    }

    public static void setBlurRadius(float blurRadius) {
        BLUR_RADIUS = blurRadius;
    }

    public static Bitmap blur (Context context, Bitmap original){
        int w = Math.round(original.getWidth() * BITMAP_SCALE);
        int h = Math.round(original.getHeight() * BITMAP_SCALE);

        RenderScript renderScript = RenderScript.create(context);
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));

        Bitmap input = Bitmap.createScaledBitmap(original, w, h, false);
        Bitmap output = Bitmap.createBitmap(input);

        Allocation tempIn = Allocation.createFromBitmap(renderScript, input);
        Allocation tempOut = Allocation.createFromBitmap(renderScript, output);

        intrinsicBlur.setRadius(BLUR_RADIUS);
        intrinsicBlur.setInput(tempIn);
        intrinsicBlur.forEach(tempOut);

        tempOut.copyTo(output);
//        Log.d(TAG, "blur: output image size = " + output.getAllocationByteCount());
        original.recycle();
        renderScript.destroy();
        return output;
    }

    private static Bitmap compressBitmap(Bitmap b){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 80, out);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
    }

//    public static Bitmap onScrollBlur(Bitmap original, float blurRadius, Context context){
//        int w = Math.round(original.getWidth() * BITMAP_SCALE);
//        int h = Math.round(original.getHeight() * BITMAP_SCALE);
//
//        Bitmap input = Bitmap.createScaledBitmap(original, w, h, false);
//        Bitmap output = Bitmap.createBitmap(input);
//
//        RenderScript renderScript = RenderScript.create(context);
//        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
//
//        Allocation tempIn = Allocation.createFromBitmap(renderScript, input);
//        Allocation tempOut = Allocation.createFromBitmap(renderScript, output);
//
//        intrinsicBlur.setRadius(blurRadius);
//        intrinsicBlur.setInput(tempIn);
//        intrinsicBlur.forEach(tempOut);
//        tempOut.copyTo(output);
//        renderScript.destroy();
//
//        return output;
//    }

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
            Bitmap original;
            try {
                // Todo change to Glide
                original = Picasso.with(mContext).load(src[0]).get();
                blurred = Blur.blur(mContext, original);
            }
            catch (IOException e) {
                Log.e(TAG, "doInBackground: Failed to load image!", e.getCause());
            }

            return blurred;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (frameLayout != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                    frameLayout.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), result));
                }
                else {
                    frameLayout.setBackground(new BitmapDrawable(mContext.getResources(), result));
                }
            }
            else if (image != null) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN){
                    image.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), result));
                }
                else {
                    image.setBackground(new BitmapDrawable(mContext.getResources(), result));
                }
            }
            else if (drawable != null) {
                drawable = new BitmapDrawable(mContext.getResources(), result);
            }
        }
    }
}
