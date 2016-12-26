package com.apps.wow.droider.Adapters;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.StringDef;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apps.wow.droider.Article.ArticleActivity;
import com.apps.wow.droider.Model.FeedModel;
import com.apps.wow.droider.Model.Post;
import com.apps.wow.droider.R;
import com.apps.wow.droider.Utils.Utils;
import com.apps.wow.droider.databinding.CardPopularBinding;
import com.squareup.picasso.Picasso;

/**
 * Created by awave on 2016-12-25.
 */

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.PopularViewHolder> {

    private static final String TAG = "PopularAdapter";
    private FeedModel model;

    private float touchYCoordinate;
    private float touchXCoordinate;

    public PopularAdapter(FeedModel model) {
        this.model = model;
    }

    @Override
    public PopularViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardPopularBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.card_popular, parent, false);
        return new PopularViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return model.getPosts().size();
    }
    
    @Override
    public void onBindViewHolder(final PopularViewHolder holder, int position) {
        final Post post = model.getPosts().get(position);
        Picasso.with(holder.binding.getRoot().getContext())
                .load(post.getPictureWide())
                .into(holder.binding.popularCardImage);
        holder.binding.popularTitle.setText(post.getTitle());
        holder.binding.count.setText(holder.binding.getRoot().getResources().getString(
                R.string.popular_count,
                String.valueOf(position + 1),
                String.valueOf(getItemCount())
        ));

        holder.binding.popularCard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchXCoordinate = event.getRawX();
                touchYCoordinate = event.getRawY();
                return false;
            }
        });

        holder.binding.popularCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent articleIntent =
                            new Intent(holder.binding.popularCard.getContext(), ArticleActivity.class);
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_TITLE,
                            holder.binding.popularTitle.getText().toString());
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_URL, post.getUrl());
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_X_TOUCH_COORDINATE, touchXCoordinate);
                    articleIntent.putExtra(Utils.EXTRA_ARTICLE_Y_TOUCH_COORDINATE, touchYCoordinate);
                    holder.binding.getRoot().getContext().startActivity(articleIntent);

                    FeedRecyclerViewAdapter.setHeaderImageDrawable(
                            holder.binding.popularCardImage.getDrawable());
                } catch (NullPointerException npe) {
                    // Ошибка происходит если пытаться отправить пикчу
                    // в статью. Сначала он выкидывал NullPointerException
                    // на article в ArticleActivity. Я закомментил
                    // после этого ничего не открывалось
                    Toast.makeText(holder.binding.getRoot().getContext(),
                            "Произошла ошибка при открытии статьи!", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "onClick: Failed to open ArticleActivity!", npe.getCause());
                    npe.printStackTrace();
                }
            }
        });
    }

    class PopularViewHolder extends RecyclerView.ViewHolder {
        CardPopularBinding binding;
        PopularViewHolder(CardPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


    }
}
