package com.awave.apps.droider.Utils.Utils.Article;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.awave.apps.droider.Main.AdapterMain;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;

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

    /**
     * Тут все ясно. Передается url и через Picasso скачиватеся
     * @param source - url to download a Bitmap
     * @return new Bitmap downloaded from source
     */
    public Bitmap fetchBitmap(String source){
        try {
            return Picasso.with(mContext).load(source).resize(mMetrics.widthPixels, 0).get();
        }

        catch (IOException e){
            Log.e(TAG, "fetchBitmap: Error fetching images!", e.getCause());
            return null;
        }
   }

    /**
     * This method is called when the HTML parser encounters an
     * img tag.  The <code>src</code> argument is the
     * string from the "src" attribute; the return value should be
     * a Drawable representation of the image or <code>null</code>
     * for a generic replacement image.  Make sure you call
     * setBounds() on your Drawable if it doesn't already have
     * its bounds set.
     *
     * @param src
     */
    @Override
    public Drawable getDrawable(String src) {
        URLDrawable urlDraw = new URLDrawable();
        ImageGetterAsyncTask imageGetterAsyncTask = new ImageGetterAsyncTask(urlDraw);
        imageGetterAsyncTask.execute(src);
        return urlDraw;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Bitmap>{
        protected URLDrawable urlDrawable;

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

            if (isQrCodeOrHead(src)){
                return fetchBitmap(src);
            }
            else {
                Log.d(TAG, "Flag is false; Image and qr skipped");
            }

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

            ImageParser.this.mTextView.setText(ImageParser.this.mTextView.getText());  // Без этой строчки пикчи
                                                                                      // не появляются. Магия
        }

        private boolean isQrCodeOrHead(String img){
            boolean flag = true;
            String qr = "http://chart.apis.google.com/chart?cht=qr&chs=150x150&chl=https://play.google.com/store/apps/details?";

            if (img.contains(qr) || AdapterMain.getHeadImage().equals(img)){
                flag = false;
            }
            return flag;
        }
    }

    // Этот класс можно сказать служит как хэлпер.
    // метод draw отвечает за рисование пикчи в тексте
    //
    // Из доков:
    /**
     *  Draw in its bounds (set via setBounds)
     *  respecting optional effects such as alpha (set via setAlpha)
     *  and color filter (set via setColorFilter).
    */
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
}
