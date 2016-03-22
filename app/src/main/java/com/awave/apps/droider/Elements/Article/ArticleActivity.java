package com.awave.apps.droider.Elements.Article;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.support.v8.renderscript.RSIllegalArgumentException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.awave.apps.droider.Main.AdapterMain;
import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Helper;
import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


public class ArticleActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private static final String TAG = "ArticleActivity";
    public static RelativeLayout headerImage;
    private AppBarLayout appBarLayout;
    protected static TextView sArticleHeader;
    protected static WebView sArticle;
    protected static TextView sArticleShortDescription;
    protected static ImageView sArticleImg;
    protected static ProgressBar sProgressBar;
    private Window mWindow;
    private static Toolbar toolbar;
    private static String title;
    private static String shortDescr;
    private static String webViewTextColor;
    private CollapsingToolbarLayout collapsingToolbar;
    private LinearLayout articleRelLayout;
    private static YouTubePlayerSupportFragment youtubeFragment;
    private NestedScrollView articleBackground;
    private static FrameLayout youtubeFrame;
    private Bundle extras;
    private int webViewBackgroundColor;
    private int theme;
    private String themeName;
    private static boolean isBlur;
    private static boolean isPalette;
    private int currentNightMode;

    public static final String YOUTUBE_API_KEY = "AIzaSyBl-6eQJ9SgBSznqnQV6ts_5MZ88o31sl4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /** Проверяем какая тема выбрана в настройках **/
        themeName = PreferenceManager.getDefaultSharedPreferences(this).getString("theme", "Светлая");
        isBlur = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("beta_enableBlur", false);
//        isPalette = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("palette", false);
        mWindow = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWindow.setStatusBarColor(Color.TRANSPARENT);
        }

        switch (themeName) {
            case "Светлая":

                theme = R.style.LightTheme;

                webViewBackgroundColor = R.color.cardBackgroundColor_light;
                webViewTextColor = "black";
                break;

            case "Тёмная":

                theme = R.style.DarkTheme;

                webViewBackgroundColor = R.color.cardBackgroundColor_dark;
                webViewTextColor = "white";
                break;

            case "В зависимости от времени суток":

                theme = R.style.DayNightAuto;
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);


        }

        super.onCreate(savedInstanceState);

        /** Затем "включаем" нужную тему **/
        setTheme(theme);

        setContentView(R.layout.article);

        /** Обнаружение всех View **/
        articleBackground = (NestedScrollView) findViewById(R.id.article_background_NSV);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar_article);
        articleRelLayout = (LinearLayout) findViewById(R.id.articleRelLayout);
        headerImage = (RelativeLayout) findViewById(R.id.article_header_content);
        sArticleHeader = (TextView) findViewById(R.id.article_header);
        sArticleShortDescription = (TextView) findViewById(R.id.article_shortDescription);
        articleRelLayout = (LinearLayout) findViewById(R.id.articleRelLayout);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar_article);
        sArticle = (WebView) findViewById(R.id.article);
        sArticleImg = (ImageView) findViewById(R.id.article_header_img);
        sProgressBar = (ProgressBar) findViewById(R.id.article_progressBar);


        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Parser parser = new Parser(this);

        /** Проверка как мы попали в статью **/
        extras = getIntent().getExtras();
        //Проверят можно в статье про ремикс ос 2 (через категорию видео легко найти)
        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            parser.isOutIntent(true);
            String outsideUrl = getIntent().getData().toString();
            parser.execute(outsideUrl);
        } else {
            title = extras.getString(Helper.EXTRA_ARTICLE_TITLE);
            shortDescr = extras.getString(Helper.EXTRA_SHORT_DESCRIPTION);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                if (isBlur)
                    headerImage.setBackgroundDrawable(Helper.applyBlur(AdapterMain.getHeaderImage(), this));
                else
                    headerImage.setBackgroundDrawable(AdapterMain.getHeaderImage());
            } else {
                try {
                    if(isBlur)
                        headerImage.setBackground(Helper.applyBlur(AdapterMain.getHeaderImage(), this));
                    else
                        headerImage.setBackground(AdapterMain.getHeaderImage());
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                    headerImage.setBackground(AdapterMain.getHeaderImage());
                }

            }
        }

        sArticleHeader.setText(title);
        sArticleHeader.setTypeface(Helper.getRobotoFont("Light", true, this));

        sArticleShortDescription.setText(shortDescr);
        sArticleShortDescription.setTypeface(Helper.getRobotoFont("Light", false, this));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Parser.isYoutube) {
                    /** а зачем зря тратить память как говорится, поэтому находим этот фрагмент только когда он точно нужен **/
                    youtubeFrame = (FrameLayout) findViewById(R.id.YouTubeFrame);
                    setupYoutubePlayer();
                }
            }
        }, 750);


        appBarLayout.addOnOffsetChangedListener(this);

        currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                Log.d(TAG, "onCreate: Night mode is not active, we're in day time ");
                webViewBackgroundColor = R.color.colorBackground_light;
                webViewTextColor = "black";
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                Log.d(TAG, "onCreate: Night mode is active, we're at night! ");
                webViewBackgroundColor = R.color.cardBackgroundColor_dark; // только таким диким изозрением добился нормального цвета, то бишь одного цвета с shortDescr, если сможешь - исправь на по проще. да и вообще там надо почитстить цвета, чтобы не было так много, и соответственно стайлы
                webViewTextColor = "white";
                break;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                Log.d(TAG, "onCreate: We don't know what mode we're in, assume notnight ");
                break;
        }

        this.calculateMinimumHeight();
        this.setupArticleWebView(sArticle);

    }

    private synchronized void setupYoutubePlayer() {

        youtubeFrame.setVisibility(View.VISIBLE);
       youtubeFragment = YouTubePlayerSupportFragment.newInstance();
        youtubeFragment.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasResumed) {
                youTubePlayer.cueVideo(Parser.YouTubeVideoURL);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "onInitializationFailure: ");

            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.YouTubeFrame, youtubeFragment).commit();
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) >= appBarLayout.getBottom()) {
            collapsingToolbar.setTitle(title);
//            setupPaletteBackground(false);
        } else {
            assert getSupportActionBar() != null;
            collapsingToolbar.setTitle("");
            assert getActionBar() != null;
//            setupPaletteBackground(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show items in the action bar relevant to this screen
        // if the drawer is not showing. Otherwise, let the drawer
        // decide what to show in the action bar.
        getMenuInflater().inflate(R.menu.menu_article, menu);
//
//        MenuItem shareItem = menu.findItem(R.id.action_share);
//        mShareActionProvider = (ShareActionProvider)
//                MenuItemCompat.getActionProvider(shareItem);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_open_in_browser:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(extras.getString(Helper.EXTRA_ARTICLE_URL))));
                break;
            case R.id.action_share:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT, title + ":  " + extras.getString(Helper.EXTRA_ARTICLE_URL));
                share.setType("text/plain");
                startActivity(share);
        }
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setupArticleWebView(WebView w) {
        w.setBackgroundColor(ContextCompat.getColor(Parser.activity, webViewBackgroundColor));

        WebChromeClient client = new WebChromeClient();


        WebSettings settings = w.getSettings();
        w.setWebChromeClient(client);
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setLoadWithOverviewMode(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setAppCacheEnabled(true);
        settings.setSaveFormData(true);
    }

    private void calculateMinimumHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();

        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        articleRelLayout.setMinimumHeight(screenHeight - actionBarHeight);
    }

//    private void setupPaletteBackground(boolean isTransparent) {
//        if ((isPalette && themeName.equals("Светлая")) || (isPalette && currentNightMode == Configuration.UI_MODE_NIGHT_NO) ) {
//            try {
//                Palette p = new Palette.Builder(Helper.drawableToBitmap(headerImage.getBackground())).generate();
//                if(p.getLightMutedSwatch() != null && !isTransparent) {
//                    articleBackground.setBackgroundColor(p.getLightMutedSwatch().getRgb());
////                    Log.d(TAG, "onCreate: color from bitmap: " + p.getLightVibrantSwatch().getRgb() + "");
//                }
//                else {
//                    toolbar.setBackgroundColor(Color.TRANSPARENT);
//                    articleBackground.setBackgroundColor(p.getLightMutedSwatch().getRgb());
////                    Log.d(TAG, "onCreate: else color from bitmap:TRANSPARENT " );
//                }
//            } catch (NullPointerException e) {
//                if(isTransparent)
//                    toolbar.setBackgroundColor(Color.TRANSPARENT);
//                else
//                Log.e(TAG, "onCreate: Переход по ссылке с заблюренной картинкой или Palette не может понять какой LightVibrantSwatch() ", e.getCause());
//            }
//        }
//    }


    public static class Parser extends AsyncTask<String, Integer, String> {
        private static Activity activity;

        private String html = "";
        private String img = "";
        private String title = "";
        private String descr = "";
        private Bitmap bitmap = null;
        private boolean outIntent;
        public static boolean isYoutube;
        private static String YouTubeVideoURL;
        Elements elements;


        public Parser(Activity a) {
            Parser.activity = a;
        }

        public void isOutIntent(boolean isOut) {
            this.outIntent = isOut;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document document = Jsoup.connect(strings[0]).timeout(10000).get();
                elements = document.select(".entry p, .entry ul li, .entry ol li");

                isYoutube = elements.get(1).html().contains("iframe");
                Log.d(TAG, "doInBackground: isYoutube  " + isYoutube);

                Elements imgs = document.select(".entry img");
                Elements iframe = document.select(".entry iframe, .entry p iframe ");
                Elements titleDiv = document.select(".title a");

                iframe.wrap("<div class=\"iframe_container\"></div>");
                imgs.wrap("<div class=\"article_image\"></div>");

                if (isYoutube) {
                    YouTubeVideoURL = Helper.trimYoutubeId(elements.get(1).select(".iframe_container iframe").attr("src"));
                    Log.d(TAG, "doInBackground: YouTubeVideoURL  " + YouTubeVideoURL);
                    elements.get(1).select(".iframe_container").remove();
                }
                if (outIntent) {
                    this.title = titleDiv.text();

                    if (isYoutube) {
                        img = Helper.getYoutubeImg(elements.get(1).select(".iframe_container iframe").attr("src"));

                    } else {
                        img = elements.get(1).select(".article_image img").attr("src");
                    }

                    descr = elements.get(0).text() + " " + elements.get(2).text();

                    try {
                        bitmap = Glide.with(activity).load(img).asBitmap().into(-1, -1).get(); // -1, -1 дает возможность загрузить фулл сайз.
                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(TAG, "doInBackground: Error fetching bitmap from url! (url: " + img + " )", e.getCause());
                    }
                }

//                Log.d(TAG, "doInBackground: " + elements.toString());
                elements.remove(0);
//                Log.d(TAG, "doInBackground: without element(0) " + elements.toString());
                if(!elements.isEmpty() && elements.get(1).hasText()) {
//                    Log.d(TAG, "doInBackground: HASTEXT" + elements.get(1).hasText());
                    elements.remove(1);
//                    Log.d(TAG, "doInBackground:  without element(1) " + elements.toString());
                }
                html = setupHtml(elements.toString());
            } catch (IOException e) {
                Log.e(TAG, "Failed to fetch html", e.getCause());
                html = setupHtml("Почему-то не получилось загрузить страницу. Попробуйте заново открыть статью");
            }
            return "";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            sProgressBar.setVisibility(View.GONE);
            if (outIntent) {
                try {
                    //ошибка вылетала(переполнение памяти из-за блюра) когда открываешь статью(к примеру ту же самую) через "открыть в браузере"
                    if (isBlur)
                        sArticleImg.setImageBitmap(Helper.applyBlur(bitmap, activity));
                    else
                        sArticleImg.setImageBitmap(bitmap);
                } catch (NullPointerException | RSIllegalArgumentException npe)
                {
                    npe.printStackTrace();
                    sArticleImg.setImageBitmap(bitmap);
                }
                sArticleHeader.setText(this.title);
                ArticleActivity.title = this.title;
                sArticleShortDescription.setText(this.descr);
            }

            try {
                sArticle.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", "");
                Log.d(TAG, "onPostExecute: "+ html);
            } catch (StringIndexOutOfBoundsException e) {
                Log.e(TAG, "onPostExecute: Error loading html content", e.getCause());
            }
        }

        public String setupHtml(String html) {
            String head = "<head>" +
                    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                    "<link href='https://fonts.googleapis.com/css?family=Roboto:300,700italic,300italic' rel='stylesheet' type='text/css'>" +
                    "<style>" +
                    "body{margin:0;padding:0;font-family:\"Roboto\", sans-serif;color:" + webViewTextColor + "}" +
                    ".container{padding-left:16px;padding-right:16px; padding-bottom:36px;}" +
                    ".article_image{margin-left:-16px;margin-right:-16px;}" +
                    ".iframe_container{margin-left:-16px;margin-right:-16px;position:relative;overflow:hidden;}" +
                    "iframe{max-width: 100%; width: 100%; height: 260px; allowfullscreen; }" +
                    "img{max-width: 100%; width: 100vW; height: auto;}" +
                    "</style></head>";
            return "<html>" + head + "<body><div class=\"container\">" + html + "</div></body></html>";
        }
    }
}