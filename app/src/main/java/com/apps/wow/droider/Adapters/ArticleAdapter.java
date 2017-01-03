package com.apps.wow.droider.Adapters;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.apps.wow.droider.Article.ArticleActivity;
import com.apps.wow.droider.Model.Post;
import com.apps.wow.droider.R;
import com.apps.wow.droider.Utils.Utils;
import com.apps.wow.droider.databinding.CardPopularBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by awave on 2016-12-25.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.PopularViewHolder> {

    private static final String TAG = "ArticleAdapter";
    private ArrayList<Post> mData;

    private float touchYCoordinate;
    private float touchXCoordinate;

    public ArticleAdapter(ArrayList<Post> data) {
        this.mData = data;
    }

    @Override
    public PopularViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardPopularBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.card_popular, parent, false);
        return new PopularViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    
    @Override
    public void onBindViewHolder(final PopularViewHolder holder, int position) {
        Post post = mData.get(position);

        Picasso.with(holder.binding.getRoot().getContext())
                .load(post.getPictureWide())
                .into(holder.binding.popularCardImage);
        holder.binding.popularTitle.setText(post.getTitle());
        holder.binding.count.setText(holder.binding.getRoot().getResources().getString(
                R.string.popular_count,
                String.valueOf(position + 1),
                String.valueOf(getItemCount())
        ));

        holder.binding.popularCard.setOnTouchListener((v, event) -> {
            touchXCoordinate = event.getRawX();
            touchYCoordinate = event.getRawY();
            return false;
        });

        holder.binding.popularCard.setOnClickListener(view -> {
            try {
                Intent articleIntent =
                        new Intent(holder.binding.popularCard.getContext(), ArticleActivity.class);
                articleIntent.putExtra(Utils.EXTRA_ARTICLE_TITLE,
                        holder.binding.popularTitle.getText().toString());
                articleIntent.putExtra(Utils.EXTRA_ARTICLE_URL, post.getUrl());
                articleIntent.putExtra(Utils.EXTRA_ARTICLE_X_TOUCH_COORDINATE, touchXCoordinate);
                articleIntent.putExtra(Utils.EXTRA_ARTICLE_Y_TOUCH_COORDINATE, touchYCoordinate);
                articleIntent.putExtra(Utils.EXTRA_ARTICLE_IMG_URL, post.getPictureWide());
                holder.binding.getRoot().getContext().startActivity(articleIntent);

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
