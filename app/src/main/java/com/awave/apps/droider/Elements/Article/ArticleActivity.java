package com.awave.apps.droider.Elements.Article;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
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
import org.jsoup.select.Elements;

import java.io.IOException;


/**
 * Created by awave on 16/05/2015
 */
public class ArticleActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ShareActionProvider mShareActionProvider;
    private Intent share = getIntent();
    private FrameLayout header;
    private RelativeLayout articleRelLayout;
    private static NestedScrollView nestedScrollView;
    private static TextView articleHeader;
    private static TextView article;
    private static DisplayMetrics metrics;
    private static ImageParser imageParser;
    private static String title = "";

    private static final String TAG = ArticleActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article);

        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);
        articleRelLayout = (RelativeLayout)findViewById(R.id.articleRelLayout);

        toolbar = (Toolbar) findViewById(R.id.toolbar_article);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //странно, но эта хрень когда так установлено - не двигается в тулбар, а когда просто текст, но двигатеся #бред
        getSupportActionBar().setTitle(title);


        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;

        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        articleRelLayout.setMinimumHeight(screenHeight - actionBarHeight);

        articleHeader = (TextView) findViewById(R.id.article_header);
        article = (TextView) findViewById(R.id.article);

        metrics = new DisplayMetrics(); // for ImageParser
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        imageParser = new ImageParser(article, getResources(), this, metrics);

        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = nestedScrollView.getScrollY();
                float blurRadius = (float) scrollY / 100;
                Helper.Blur.setBlurRadius(blurRadius);
            }
        });

        header = (FrameLayout) findViewById(R.id.article_header_content);
        if (!AdapterMain.getHeadImage().contains("youtube")){
            new Helper.Blur.AsyncBlurImage(header, this).execute(AdapterMain.getHeadImage());
        }
        else {
            YouTubePlayerSupportFragment youtubeFragment = YouTubePlayerSupportFragment.newInstance();
            youtubeFragment.initialize(DeveloperKey.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasResumed) {
                    youTubePlayer.cueVideo(Helper.trimYoutubeId(Helper.getYoutubeVideo()));
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                }
            });
            getSupportFragmentManager().beginTransaction().replace(R.id.article_header_content, youtubeFragment).commit();
            Log.d(TAG, "Youtube Video: " + Helper.getYoutubeVideo());
            Log.d(TAG, "Youtube Video ID: " + Helper.trimYoutubeId(Helper.getYoutubeVideo()));
        }
    }

    public static class Parser extends AsyncTask<String, String, String>{
        private String html = "";

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document document = Jsoup.connect(strings[0]).get();
                Elements elements = document.select("div.entry p");
                Elements titleDiv = document.select("div.title a");
                String qr = "http://chart.apis.google.com/chart?cht=qr&chs=150x150&chl=https://play.google.com/store/apps/details?id=";
                String video = "https://www.youtube.com/embed/";

                elements.attr("style", "padding-left:10dp;padding-right:10dp");

                title = titleDiv.attr("title") + "\n";
                html =  elements.toString();
            }
            catch (IOException e){
                Log.d(TAG, "Failed");
            }
            return "";
        }

        @Override
        protected void onPostExecute(String aVoid) {
            articleHeader.setText(title);
            Spanned htmlSpan = Html.fromHtml(html.trim(), imageParser, null);
            article.setText(Helper.trimWhiteSpace(htmlSpan));
            article.setMovementMethod(LinkMovementMethod.getInstance()); // Handles hyperlink clicks
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

        // Set up ShareActionProvider's default share intent
        MenuItem shareItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        mShareActionProvider.setShareIntent(getDefaultIntent());

        return super.onCreateOptionsMenu(menu);
    }

    private Intent getDefaultIntent() {
        share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, AdapterMain.getShareTitle() + ":  " + AdapterMain.getShareUrl());
        share.setType("text/plain");
        return share;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
