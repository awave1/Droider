package com.awave.apps.droider.Elements.Article;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.awave.apps.droider.Main.AdapterMain;
import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Utils.Article.ImageParser;
import com.awave.apps.droider.Utils.Utils.DeveloperKey;
import com.awave.apps.droider.Utils.Utils.Helper;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


public class ArticleActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private static final String TAG = "ArticleActivity";

    private static Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbar;
    private ShareActionProvider mShareActionProvider;
    private Intent share = getIntent();
    private RelativeLayout headerImage;
    private LinearLayout articleRelLayout;
    private static ArrayList<String> youTubeLink = new ArrayList<>();
    private TextView articleHeader;
    private static WebView article;
    private TextView articleShortDescription;
    private static YouTubePlayerSupportFragment youtubeFragment;
    private static DisplayMetrics metrics;
    private static Display display;
    private static ImageParser imageParser;
    private static FrameLayout  youtubeFrame;
    private String title;
    private String shortDescr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article);

        AppBarLayout appBarLayout = (AppBarLayout)findViewById(R.id.appbar_article);
        appBarLayout.addOnOffsetChangedListener(this);
        youtubeFrame = (FrameLayout)findViewById(R.id.YouTubeFrame);
        articleRelLayout = (LinearLayout) findViewById(R.id.articleRelLayout);
        Bundle extras = getIntent().getExtras();
        title = extras.getString(Helper.EXTRA_ARTICLE_TITLE);
        shortDescr = extras.getString(Helper.EXTRA_SHORT_DESCRIPTION);

        NestedScrollView nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        articleRelLayout = (LinearLayout) findViewById(R.id.articleRelLayout);
        collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar_article);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("");

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(getResources().getColor(R.color.colorControlNormal_light), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        articleHeader = (TextView) findViewById(R.id.article_header);
        articleHeader.setText(title);
        articleHeader.setTypeface(Helper.getRobotoFont("Light", false, this));

        articleShortDescription = (TextView) findViewById(R.id.article_shortDescription);
        articleShortDescription.setText(shortDescr);
        articleShortDescription.setTypeface(Helper.getRobotoFont("Light", false, this));

        display = getWindowManager().getDefaultDisplay();
        metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;

        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        articleRelLayout.setMinimumHeight(screenHeight - actionBarHeight);

        article = (WebView) findViewById(R.id.article);
        this.setupArticleWebView(article);

        headerImage = (RelativeLayout) findViewById(R.id.article_header_content);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            headerImage.setBackgroundDrawable(AdapterMain.getHeaderImage());
        else
            headerImage.setBackground(AdapterMain.getHeaderImage());

//        this.setupYoutube();

//        if (!AdapterMain.getHeadImage().contains("youtube")){
//            new Blur.AsyncBlurImage(headerImage, this).execute(AdapterMain.getHeadImage());
//        }


        if(AdapterMain.getHeadImage().contains("youtube")) {
            youtubeFrame.setVisibility(View.VISIBLE);
            youtubeFragment = YouTubePlayerSupportFragment.newInstance();
            youtubeFragment.initialize(DeveloperKey.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasResumed) {
                   synchronized (youtubeFragment)
                   {
                       try {
                           youtubeFragment.wait(500);
                       } catch (InterruptedException e) {
                           e.printStackTrace();
                       }
                   }
                    youTubePlayer.cueVideo(Parser.YouTubeVideoURL);
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    Log.d(TAG, "onInitializationFailure: ");

                }
            });
            getSupportFragmentManager().beginTransaction().replace(R.id.YouTubeFrame, youtubeFragment).commit();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (Math.abs(verticalOffset) >= appBarLayout.getBottom()) {
            collapsingToolbar.setTitle(title);
        }
        else {
            assert getSupportActionBar() != null;
            collapsingToolbar.setTitle("");
            assert getActionBar() != null;
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStop() {
        youTubeLink.clear();
        super.onStop();
    }

    public static class Parser extends AsyncTask<String, String, String>{
        private String html = "";
        private static String YouTubeVideoURL;

        @Override
        protected void onPreExecute() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                 // ждём
                }
            },500);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document document = Jsoup.connect(strings[0]).get();
                Elements elements = document.select("div.entry p");
                Elements imgs = document.select("div.entry img");
                Elements iframe = document.select("div.entry iframe");

                iframe.wrap("<div class=\"iframe_container\"></div>");
                imgs.wrap("<div class=\"article_image\"></div>");

                for (Element e: imgs) {
                    if (e.attr("src").equals(AdapterMain.getHeadImage())){
                        e.remove();
                    }
                }

                Elements entry = document.select("div.entry");

                for (Element youtube : entry) {
                    youTubeLink.add(youtube.getElementsByTag("iframe").attr("src"));
                        if (youTubeLink.get(youtube.getAllElements().indexOf(youtube)).contains("youtube")) {
                            Log.d(TAG, "doInBackground: was here");
                            YouTubeVideoURL = youTubeLink.get(0);
                            YouTubeVideoURL = Helper.trimYoutubeId(YouTubeVideoURL);
                        }
                }

                Log.d(TAG, "Youtube Video: " + youTubeLink.get(0));
                Log.d(TAG, "Youtube Video ID: " +YouTubeVideoURL);

                elements.remove(0);
                elements.remove(1);

                html = setupHtml(elements.toString());

                Log.d(TAG, "doInBackground: html content = " + html);
            }
            catch (IOException e){
                Log.d(TAG, "Failed");
            }
            return "";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            try {
                article.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", "");

            } catch (StringIndexOutOfBoundsException e)
            {
                e.printStackTrace();
            }
        }

        private String setupHtml(String html){
            String head = "<head>" +
                    "<link href='https://fonts.googleapis.com/css?family=Roboto:300,700italic,300italic' rel='stylesheet' type='text/css'>"+
                    "<style>" +
                    "body{margin:0;padding:0;font-family:\"Roboto\", sans-serif;}"+
                    ".container{padding-left:16px;padding-right:16px;}" +
                    ".article_image{margin-left:-16px;margin-right:-16px;}" +
                    ".iframe_container{margin-left:-16px;margin-right:-16px;position:relative;overflow:hidden;}"+
                    "iframe{max-width: 100%; width: 100%; height: 260px;}"+
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
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(AdapterMain.getShareUrl())));
                break;
        }
        return true;
    }

    private Intent shareIntent() {
        share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, AdapterMain.getShareTitle() + ":  " + AdapterMain.getShareUrl());
        share.setType("text/plain");
        return share;
    }

    private void setupArticleWebView(WebView w){
        w.setBackgroundColor(getResources().getColor(R.color.cardBackgroundColor_light));
        WebChromeClient client = new WebChromeClient();
        WebSettings settings = w.getSettings();
        w.setWebChromeClient(client);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
    }

//    private void setupYoutube(){
//        if(AdapterMain.getHeadImage().contains("youtube")) {
//            FrameLayout youtubeFrame = (FrameLayout)findViewById(R.id.YouTubeFrame);
//            youtubeFrame.setVisibility(View.VISIBLE);
//            YouTubePlayerSupportFragment youtubeFragment = YouTubePlayerSupportFragment.newInstance();
//            youtubeFragment.initialize(DeveloperKey.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
//                @Override
//                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasResumed) {
//                    youTubePlayer.cueVideo(Helper.trimYoutubeId(Helper.getYoutubeVideo()));
//                }
//
//                @Override
//                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//                    Log.d(TAG, "onInitializationFailure: YoutubePlayer failed to initialize!");
//
//                }
//            });
//            this.getSupportFragmentManager().beginTransaction().replace(R.id.YouTubeFrame, youtubeFragment).commit();
//            Log.d(TAG, "Youtube Video: " + Helper.getYoutubeVideo());
//            Log.d(TAG, "Youtube Video ID: " + Helper.trimYoutubeId(Helper.getYoutubeVideo()));
//        }
//    }
}