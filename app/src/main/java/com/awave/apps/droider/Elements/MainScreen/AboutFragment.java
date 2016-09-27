package com.awave.apps.droider.Elements.MainScreen;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.awave.apps.droider.R;

import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends android.app.Fragment {

    @Nullable
    @Override
    public View onCreateView
            (LayoutInflater inflater, @Nullable ViewGroup container,
             @Nullable Bundle savedInstanceState) {

        View returnView = inflater.inflate(R.layout.about_fragment, container, false);

        authorsRecyclerViewInit(returnView);
        developersRecyclerViewInit(returnView);

        return returnView;
    }

    private void authorsRecyclerViewInit(View v) {
        List<Author> authorsList = new ArrayList<>();
        authorsList.add(
                new Author(
                        getString(R.string.about_fragment_vedenskiy_title),
                        getString(R.string.about_fragment_vedenskiy_description),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_vedensky, null)) {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://vk.com/borisvedensky")
                        ));
                    }
                }
        );
        authorsList.add(
                new Author(
                        getString(R.string.about_fragment_istishev_title),
                        getString(R.string.about_fragment_istishev_description),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_istishev, null)) {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://vk.com/istishev")
                        ));
                    }
                }
        );

        recyclerViewAdapterSetting(v, authorsList, R.id.droider_ru_authors_recycler_view);
    }

    private void developersRecyclerViewInit(View v) {
        List<Author> developersList = new ArrayList<>();
        developersList.add(
                new Author(
                        getString(R.string.about_fragment_art_title),
                        getString(R.string.about_fragment_art_description),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_art, null)) {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://vk.com/awawave")
                        ));
                    }
                }
        );
        developersList.add(
                new Author(
                        getString(R.string.about_fragment_alex_title),
                        getString(R.string.about_fragment_alex_description),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_alex, null)) {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://vk.com/jackson23")
                        ));
                    }
                }
        );

        recyclerViewAdapterSetting(v, developersList, R.id.droider_app_authors_recycler_view);
    }

    private void recyclerViewAdapterSetting
            (View v, List<Author> authorsList, @IdRes int recyclerViewId) {

        AuthorsRecyclerViewAdapter authorsRecyclerViewAdapter =
                new AuthorsRecyclerViewAdapter(authorsList);
        RecyclerView authorsRecyclerView =
                (RecyclerView) v.findViewById(recyclerViewId);

        if (authorsRecyclerView != null) {
            authorsRecyclerView.setAdapter(authorsRecyclerViewAdapter);
            authorsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }

    class AuthorsViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout baseLinearLayout;
        private TextView titleTextView;
        private TextView descriptionTextView;
        private ImageView imageView;

        public AuthorsViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.authors_title_text_view);
            descriptionTextView = (TextView) itemView.findViewById(
                    R.id.authors_description_text_view);
            imageView = (ImageView) itemView.findViewById(R.id.authors_image_view);
            baseLinearLayout = (LinearLayout) itemView.findViewById(R.id.authors_base_linear_layout);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            if (baseLinearLayout != null) {
                baseLinearLayout.setOnClickListener(onClickListener);
            }
        }

        public TextView getTitleTextView() {
            return titleTextView;
        }

        public TextView getDescriptionTextView() {
            return descriptionTextView;
        }

        public ImageView getImageView() {
            return imageView;
        }

    }

    class AuthorsRecyclerViewAdapter extends RecyclerView.Adapter<AuthorsViewHolder> {

        private List<Author> authorList = new ArrayList<>();

        public AuthorsRecyclerViewAdapter(List<Author> authorList) {
            this.authorList = authorList;
        }

        @Override
        public AuthorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.authors_recycler_view_item, parent, false);
            return new AuthorsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AuthorsViewHolder holder, int position) {
            Author author = authorList.get(position);
            holder.getTitleTextView().setText(author.getTitleString());
            holder.getDescriptionTextView().setText(author.getDescriptionString());
            holder.getImageView().setImageDrawable(author.getImageDrawable());
            holder.setOnClickListener(author);
        }

        @Override
        public int getItemCount() {
            return authorList.size();
        }

    }

    private class Author implements View.OnClickListener {

        private String titleString;
        private String descriptionString;
        private Drawable imageDrawable;

        public Author(String titleString, String descriptionString,
                      Drawable imageDrawable) {
            this.titleString = titleString;
            this.descriptionString = descriptionString;
            this.imageDrawable = imageDrawable;
        }

        public String getTitleString() {
            return titleString;
        }

        public String getDescriptionString() {
            return descriptionString;
        }

        public Drawable getImageDrawable() {
            return imageDrawable;
        }

        @Override
        public void onClick(View view) {
//            Override it if you need
        }

    }

}
