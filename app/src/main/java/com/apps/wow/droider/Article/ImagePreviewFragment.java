package com.apps.wow.droider.Article;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.apps.wow.droider.R;
import com.apps.wow.droider.databinding.FragmentImagePrevBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by awave on 2016-12-30.
 */

public class ImagePreviewFragment extends Fragment {
    private static final String TAG = "ImagePreviewFragment";
    public static final String IMAGE_URL = "IMAGE_URL";
    private FragmentImagePrevBinding mBinding;
    private PhotoViewAttacher mAttacher;

    public static ImagePreviewFragment newInstance(String imageUrl) {
        Bundle args = new Bundle();
        args.putString(IMAGE_URL, imageUrl);
        ImagePreviewFragment fragment = new ImagePreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_image_prev, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setNavigationBarColor(ContextCompat.getColor(
                    getContext(),
                    R.color.image_preview_bckg
            ));
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        mAttacher = new PhotoViewAttacher(mBinding.img);


        Callback imageLoaded = new Callback() {
            @Override
            public void onSuccess() {
                if (mAttacher != null)
                    mAttacher.update();
                else
                    mAttacher = new PhotoViewAttacher(mBinding.img);
            }

            @Override
            public void onError() {

            }
        };

        Picasso.with(getContext())
                .load(getArguments().getString(IMAGE_URL))
                .into(mBinding.img, imageLoaded);


        mBinding.closeBtn.setOnClickListener(view -> {
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .remove(ImagePreviewFragment.this)
                    .commit();

           onDestroy();
        });
        return mBinding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        mAttacher.cleanup();

        ((ArticleActivity) getActivity())
                .binding.articleBackgroundNSV.setNestedScrollingEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
