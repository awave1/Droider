package com.awave.apps.droider.Main;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.awave.apps.droider.Elements.Article.ArticleActivity;
import com.awave.apps.droider.R;
import com.awave.apps.droider.Utils.Feed.FeedItem;
import com.awave.apps.droider.Utils.Helper;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class FeedRecyclerViewAdapter extends RecyclerView.Adapter<FeedViewHolder> {

    private static ArrayList<FeedItem> feedItemArrayList;
    private static Activity activity;
    private static Drawable headerImageDrawable;
    private String TAG = FeedRecyclerViewAdapter.class.getSimpleName();
    private boolean isPodcast = false;

    public FeedRecyclerViewAdapter(Activity activity, ArrayList<FeedItem> feedItemArrayList,
                                   boolean isPodcast) {
        FeedRecyclerViewAdapter.activity = activity;
        FeedRecyclerViewAdapter.feedItemArrayList = feedItemArrayList;
        this.isPodcast = isPodcast;
    }

    public static Drawable getHeaderImageDrawable() {
        return headerImageDrawable;
    }

    public static void setHeaderImageDrawable(Drawable headerImageDrawable) {
        FeedRecyclerViewAdapter.headerImageDrawable = headerImageDrawable;
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        return new FeedViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder feedViewHolder, final int i) {

        final FeedItem feedItem = feedItemArrayList.get(i);
        feedViewHolder.getCardTitleTextView().setText(feedItem.getTitle());
        feedViewHolder.getCardDescriptionTextView().setText(feedItem.getDescription());
        feedViewHolder.getSiteUrlTextView().setText(feedItem.getUrl());

        if (isPodcast) {
            assert feedViewHolder.getCardImageView() != null;
            Glide.with(activity).load(R.drawable.dr_cast).into(feedViewHolder.getCardImageView());
        } else {
            Glide.with(activity).load(feedItem.getImgUrl()).into(feedViewHolder.getCardImageView());
        }

        final String url = feedItem.getUrl();

        feedViewHolder.getCardView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new ArticleActivity.Parser(activity).execute(url);
                    Intent article = new Intent(activity, ArticleActivity.class);
                    article.putExtra(Helper.EXTRA_ARTICLE_TITLE, feedViewHolder.getCardTitleTextView().getText().toString());
                    article.putExtra(Helper.EXTRA_SHORT_DESCRIPTION, feedViewHolder.getCardDescriptionTextView().getText().toString());
                    article.putExtra(Helper.EXTRA_ARTICLE_URL, url);
                    activity.startActivity(article);
                    activity.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out);
                    FeedRecyclerViewAdapter.setHeaderImageDrawable(feedViewHolder.getCardImageView().getDrawable());
                } catch (Exception e) {
                    // Ошибка происходит если пытаться отправить пикчу
                    // в статью. Сначала он выкидывал NullPointerException
                    // на article в ArticleActivity. Я закомментил
                    // после этого ничего не открывалось
                    Toast.makeText(activity, "Произошла ошибка при открытии статьи!", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onClick: Failed to open ArticleActivity!", e.getCause());
                }
            }
        });
        feedViewHolder.getCardView().setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                Log.d(TAG, "onLingClick cardview");
                ClipboardManager clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData copyLink = ClipData.newPlainText("", feedItem.getUrl());
                clipboardManager.setPrimaryClip(copyLink);
                Toast.makeText(view.getContext(), R.string.main, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return feedItemArrayList.size();
    }

}