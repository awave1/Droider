package com.awave.apps.droider.Utils.Utils.Article;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.awave.apps.droider.Main.AdapterMain;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageParser implements Html.ImageGetter {
    private final String TAG = ImageParser.class.getSimpleName();

    public Context mContext;
    public TextView mTextView;
    public Resources mRes;
    public DisplayMetrics mMetrics;

    public ImageParser(TextView textView, Resources resources, Context context, DisplayMetrics metrics){
        this.mTextView = textView;
        this.mRes = resources;
        this.mContext = context;
        this.mMetrics = metrics;
    }


    public Bitmap fetchDrawable(String source){
        try {
            Log.d(TAG+".fetchDrawable", source);
            return Picasso.with(mContext).load(source)
                    .resize(mMetrics.widthPixels, 0).get();
        }
        catch (IOException e){
            Log.e(TAG, "fetchDrawable: Error fetching images!", e.getCause());
            return null;
        }
    }

    @Override
    public Drawable getDrawable(String src) {
        URLDrawable urlDraw = new URLDrawable();
        ImageGetterAsyncTask imageGetterAsyncTask = new ImageGetterAsyncTask(urlDraw);
        imageGetterAsyncTask.execute(src);
        return urlDraw;
    }

    public class URLDrawable extends BitmapDrawable {
        protected Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            if (drawable != null){
                drawable.draw(canvas);
            }
        }

        public void setDrawable(Drawable drawable) {
            this.drawable = drawable;
        }
    }



    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Bitmap>{
        protected URLDrawable urlDrawable;

        private boolean flag = true;

        public ImageGetterAsyncTask (URLDrawable u){
            this.urlDrawable = u;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String src = strings[0];

            if (isQrCode(src)){
                return fetchDrawable(src);
            }
            else {
                Log.d(TAG, "Flag is false; Image and qr skipped");
            }

            Log.d(TAG, "isQrCode: " +  isQrCode(src));
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmapResult) {
            final Drawable drawableResult = new BitmapDrawable(mRes, bitmapResult);
            float multiplier = mMetrics.widthPixels / (float) drawableResult.getIntrinsicWidth();
            int w = (int) multiplier * drawableResult.getIntrinsicWidth();
            int h = (int) multiplier * drawableResult.getIntrinsicHeight();

            drawableResult.setBounds(0, 0, w, h);
            urlDrawable.setDrawable(drawableResult);
            urlDrawable.setBounds(0, 0, drawableResult.getIntrinsicWidth(), drawableResult.getIntrinsicHeight());
            ImageParser.this.mTextView.setText(ImageParser.this.mTextView.getText());
        }

        private boolean isHeadImage(String img) {
            boolean flag = true;
            if (AdapterMain.getHeadImage().equals(img)) {
                flag = false;
            }
            return flag;
        }

        private boolean isQrCode(String img){
            boolean flag = true;
            String qr = "http://chart.apis.google.com/chart?cht=qr&chs=150x150&chl=https://play.google.com/store/apps/details?";

            if (img.contains(qr) || AdapterMain.getHeadImage().equals(img)){
                flag = false;
            }
            return flag;
        }
    }


}
