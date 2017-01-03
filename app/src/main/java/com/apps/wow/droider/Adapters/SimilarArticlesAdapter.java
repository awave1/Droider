package com.apps.wow.droider.Adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.wow.droider.Model.Post;
import com.apps.wow.droider.R;
import com.apps.wow.droider.databinding.CardPopularBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by awave on 2017-01-02.
 */

public class SimilarArticlesAdapter extends RecyclerView.Adapter<SimilarArticlesAdapter.ViewHolder> {

    private ArrayList<Post> mData;

    public SimilarArticlesAdapter(ArrayList<Post> data) {
        mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardPopularBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                                        R.layout.card_popular, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post p = mData.get(position);
        holder.binding.popularTitle.setText(p.getTitle());
        holder.binding.count.setText(holder.binding.getRoot().getResources().getString(
                R.string.popular_count,
                String.valueOf(position + 1),
                String.valueOf(getItemCount())
        ));

        Picasso.with(holder.binding.getRoot().getContext())
                .load(p.getPictureWide())
                .into(holder.binding.popularCardImage);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardPopularBinding binding;
        public ViewHolder(CardPopularBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
