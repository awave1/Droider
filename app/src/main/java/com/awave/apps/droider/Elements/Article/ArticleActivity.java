package com.awave.apps.droider.Elements.Article;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.awave.apps.droider.Main.AdapterMain;
import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Helper;
import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class ArticleActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private static final String TAG = "ArticleActivity";

    private static Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ShareActionProvider mShareActionProvider;
    private Intent share = getIntent();
    public static RelativeLayout headerImage;
    private LinearLayout articleRelLayout;

    private TextView articleHeader;
    private static WebView article;
    private TextView articleShortDescription;
    private static DisplayMetrics metrics;
    private static boolean outIntent = false;
    private static String title;
    private static String shortDescr;
    private Bundle extras;
    private int theme;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /** Проверяем какая тема выбрана в настройках **/
        String themeName = PreferenceManager.getDefaultSharedPreferences(this).getString("theme", "Светлая");
        if (themeName.equals("Светлая")) {
            theme = R.style.LightTheme;
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark_light));
        } else if (themeName.equals("Тёмная")) {
            theme = R.style.DarkTheme;
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark_dark));
        }
        super.onCreate(savedInstanceState);
        /** Затем "включаем" нужную тему **/
        setTheme(theme);

        setContentView(R.layout.article);

        /** Обнаружение всех View **/
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar_article);
        articleRelLayout = (LinearLayout) findViewById(R.id.articleRelLayout);
        headerImage = (RelativeLayout) findViewById(R.id.article_header_content);
        articleHeader = (TextView) findViewById(R.id.article_header);
        articleShortDescription = (TextView) findViewById(R.id.article_shortDescription);
        articleRelLayout = (LinearLayout) findViewById(R.id.articleRelLayout);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar_article);
        article = (WebView) findViewById(R.id.article);
        ImageView articleImg = (ImageView)findViewById(R.id.article_header_img);

        /** Проверка как мы попали в статью **/

        extras = getIntent().getExtras();
        //Проверят можно в статье про ремикс ос 2 (через категорию видео легко найти)
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            outIntent = true;
            String data = getIntent().getData().toString();
            new ArticleActivity.Parser().execute(data);
            Glide.with(this).load(Parser.img).into(articleImg);
        }
        else {

            title = extras.getString(Helper.EXTRA_ARTICLE_TITLE);
            shortDescr = extras.getString(Helper.EXTRA_SHORT_DESCRIPTION);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
                headerImage.setBackgroundDrawable(AdapterMain.getHeaderImage());
            else
                headerImage.setBackground(AdapterMain.getHeaderImage());

        }

        articleHeader.setText(title);
        articleHeader.setTypeface(Helper.getRobotoFont("Regular", false, this));

        articleShortDescription.setText(shortDescr);
        articleShortDescription.setTypeface(Helper.getRobotoFont("Light", false, this));


        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /** Пробовал через темы, но чёт не ставится, хотя там есть такое "homeAsUpIndicator"
         * и ещё, не очень красиво выглядит, когда цвет индиктора белый, а тайтла чёрный
          */
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(ContextCompat.getColor(this,R.color.colorControlNormal_light), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        /** не плохо бы вынести в отдельный метод */

        metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;

        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        articleRelLayout.setMinimumHeight(screenHeight - actionBarHeight);

        appBarLayout.addOnOffsetChangedListener(this);
        this.setupArticleWebView(article);



        /** Test **/
//        Palette p = new Palette.Builder(Helper.drawableToBitmap(headerImage.getBackground())).generate();
//
//        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.articleCoordinator);
//        try {
//            mCoordinatorLayout.setBackgroundColor(p.getLightVibrantSwatch().getRgb());
//        }
//        catch (NullPointerException e){
//            mCoordinatorLayout.setBackgroundColor(getResources().getColor(R.color.colorBackground_light));
//            Log.e(TAG, "onCreate: Unable to get color from bitmap", e.getCause());
//        }


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) >= appBarLayout.getBottom()) {
            collapsingToolbar.setTitle(title);
        } else {
            assert getSupportActionBar() != null;
            collapsingToolbar.setTitle("");
            assert getActionBar() != null;
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
    }


    public static class Parser extends AsyncTask<String, String, String> {
        private String html = "";
        private static String img = "";

        @Override
        protected void onPreExecute() {
           //тут добавить анимацию загрузки статьи, на подобии конкуретнов
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document document = Jsoup.connect(strings[0]).get();
                Elements elements = document.select(".entry p");
                Elements imgs = document.select(".entry img");
                Elements iframe = document.select(".entry iframe");
                Elements titleDiv = document.select(".title a");

                iframe.wrap("<div class=\"iframe_container\"></div>");
                imgs.wrap("<div class=\"article_image\"></div>");


                if (outIntent) {
                    Log.d(TAG, "doInBackground: OUTINTENT");
                    title = titleDiv.attr("title") + "\n";
                    shortDescr = elements.get(0).text() + "\n" + elements.get(1).text() + "\n" + elements.get(2).text();
                }

                //хрен знает почему, но заходит он сюда 1 раз только, а может вообще не зайти
                for (Element e : imgs) {
                    if (e.attr("src").equals(AdapterMain.getHeadImage())) {
                        img = e.attr("src");
                        Log.d(TAG, "doInBackground: IMG" + img);
                        e.remove();
                        break;
                    }
                }


                elements.remove(0);
                elements.remove(1);

                html = setupHtml(elements.toString());

                Log.d(TAG, "doInBackground: html content = " + html);
            } catch (IOException e) {
                Log.d(TAG, "Failed");
            }
            return "";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            try {
                article.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", "");

            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        private String setupHtml(String html) {
            String head = "<head>" +
                    "<link href='https://fonts.googleapis.com/css?family=Roboto:300,700italic,300italic' rel='stylesheet' type='text/css'>" +
                    "<style>" +
                    "body{margin:0;padding:0;font-family:\"Roboto\", sans-serif;}" +
                    ".container{padding-left:16px;padding-right:16px; padding-bottom:16px}" +
                    ".article_image{margin-left:-16px;margin-right:-16px;}" +
                    ".iframe_container{margin-left:-16px;margin-right:-16px;position:relative;overflow:hidden;}" +
                    "iframe{max-width: 100%; width: 100%; height: 260px;}" +
                    "img{max-width: 100%; width: auto; height: auto;}" +
                    "</style></head>";
            return "<html>" + head + "<body><div class=\"container\">" + html + "</div></body></html>";
        }
    }

    public void restoreActionBar() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.menu_article, menu);
        restoreActionBar();
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        // Set up ShareActionProvider's default share intent

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_share:
                mShareActionProvider.setShareIntent(shareIntent());
                shareIntent();
                break;
            case R.id.action_open_in_browser:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(extras.getString(Helper.EXTRA_ARTICLE_URL))));
                break;
        }
        return true;
    }

    private Intent shareIntent() {
        share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, title + ":  " + extras.getString(Helper.EXTRA_ARTICLE_URL));
        share.setType("text/plain");
        return share;
    }

    private void setupArticleWebView(WebView w) {
        w.setBackgroundColor(getResources().getColor(R.color.cardBackgroundColor_light));
        WebChromeClient client = new WebChromeClient();
        WebSettings settings = w.getSettings();
        w.setWebChromeClient(client);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }
}
