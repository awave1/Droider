package com.apps.wow.droider.Feed;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apps.wow.droider.Adapters.ArticleAdapter;
import com.apps.wow.droider.Adapters.FeedAdapter;
import com.apps.wow.droider.BuildConfig;
import com.apps.wow.droider.DroiderBaseActivity;
import com.apps.wow.droider.Feed.Interactors.FeedOrientation;
import com.apps.wow.droider.Feed.Presenter.FeedPresenterImpl;
import com.apps.wow.droider.Feed.View.FeedView;
import com.apps.wow.droider.Model.FeedModel;
import com.apps.wow.droider.R;
import com.apps.wow.droider.Utils.Utils;
import com.apps.wow.droider.databinding.FeedFragmentBinding;


public class FeedFragment extends android.app.Fragment implements
        FeedView, OnTaskCompleted, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "Feed";
    public static LinearLayoutManager sLinearLayoutManager;
    public static StaggeredGridLayoutManager sStaggeredGridLayoutManager;
    private FeedPresenterImpl presenter;
    private FeedFragmentBinding binding;
    private FeedAdapter feedAdapter;
    private ArticleAdapter mArticleAdapter;

    private String currentCategory;
    private String currentSlug;

    public static FeedFragment newInstance(String category, String slug) {
        FeedFragment feedFragment = new FeedFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.EXTRA_CATEGORY, category);
        bundle.putString(Utils.EXTRA_SLUG, slug);
        feedFragment.setArguments(bundle);
        feedFragment.setRetainInstance(true);
        return feedFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.feed_fragment, container, false);
        presenter = new FeedPresenterImpl();
        presenter.attachView(this, this); //first this = View interface, Second this = onTaskCompleted
        orientationDebugging();

        swipeRefreshLayoutSetup();
        currentCategory = getArguments().getString(Utils.EXTRA_CATEGORY);
        currentSlug = getArguments().getString(Utils.EXTRA_SLUG);
        presenter.loadData(currentCategory, currentSlug, Utils.DEFAULT_COUNT, 0, true);
        presenter.loadPopular();

        return binding.getRoot();
    }

    @Override
    public void onLoadingFeed() {
        binding.feedSwipeRefresh.setRefreshing(true);
    }

    @Override
    public void onLoadCompleted(FeedModel model, boolean clear) {
        //потому что при переходе на другой фрагмент и этот фрагмент не удаляется, благодаря setRetainInstance(true);
        // но все данные прикреплённые к ресайлеру удаляются, так как вью инфлейтится заново
        if (feedAdapter == null || clear) {
            Log.d(TAG, "onLoadCompleted: is null");
            FeedOrientation.offsetPortrait = 0;
            FeedOrientation.offsetLandscape = 0;

            binding.feedRecyclerView.setHasFixedSize(true);
            feedAdapter = new FeedAdapter(model);
            binding.feedRecyclerView.setAdapter(feedAdapter);
            initLayoutManager();
        } else {
            feedAdapter.getFeedModel().getPosts().addAll(model.getPosts());
        }
        onTaskCompleted();
    }

    @Override
    public void onLoadCompleted(FeedModel model) {
        FeedActivity parentActivity = (FeedActivity) getActivity();
        final LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        SnapHelper snapHelper = new PagerSnapHelper();

        parentActivity.binding.popularNews.setLayoutManager(layoutManager);
        parentActivity.binding.popularNews.setAdapter(new ArticleAdapter(model.getPosts()));
        snapHelper.attachToRecyclerView(parentActivity.binding.popularNews);

        onTaskCompleted();
    }

    @Override
    public void onLoadFailed() {
        onTaskCompleted();
        if (getActivity() != null)
            ((DroiderBaseActivity) getActivity()).initInternetConnectionDialog(getActivity());
    }

    @Override
    public void onRefresh() {
        if ((getActivity().getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE)
            presenter.loadData(currentCategory, Utils.SLUG_MAIN, Utils.DEFAULT_COUNT, FeedOrientation.offsetLandscape, true);
        else
            presenter.loadData(currentCategory, Utils.SLUG_MAIN, Utils.DEFAULT_COUNT, FeedOrientation.offsetPortrait, true);
        presenter.loadPopular();
    }

    private void swipeRefreshLayoutSetup() {
        binding.feedSwipeRefresh.setOnRefreshListener(this);
        binding.feedSwipeRefresh.setColorSchemeResources(
                android.R.color.holo_red_dark,
                android.R.color.holo_blue_dark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark);
        binding.feedSwipeRefresh.setSize(SwipeRefreshLayout.DEFAULT);
    }


    @Override
    public synchronized void onTaskCompleted() {
        if (feedAdapter != null) {
            feedAdapter.notifyDataSetChanged();
            if (binding.feedSwipeRefresh.isRefreshing())
                binding.feedSwipeRefresh.setRefreshing(false);
        }
        else
            onRefresh();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        binding.feedSwipeRefresh.setRefreshing(true);
    }

    private void initLayoutManager() {
        if (isAdded() && getActivity() != null) {
            if ((getActivity().getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
                setDoubleColFeedMode();
            } else {
                setSingleColFeedMode();
            }
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initLayoutManager();
                }
            }, 500);
        }
    }

    private void setDoubleColFeedMode() {
        sStaggeredGridLayoutManager = new StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL);
        binding.feedRecyclerView.setLayoutManager(sStaggeredGridLayoutManager);
        binding.feedRecyclerView.addOnScrollListener(
                new FeedOrientation(getActivity(), binding.feedSwipeRefresh) {
                    @Override
                    public void loadNextPage() {
                        presenter.loadData(Utils.CATEGORY_MAIN, Utils.SLUG_MAIN,
                                Utils.DEFAULT_COUNT, FeedOrientation.offsetLandscape, false);
                        onLoadingFeed();
                    }
                });
    }

    private void setSingleColFeedMode() {
        sLinearLayoutManager = new LinearLayoutManager(getActivity());
        binding.feedRecyclerView.setLayoutManager(sLinearLayoutManager);
        binding.feedRecyclerView.addOnScrollListener(
                new FeedOrientation(getActivity(), binding.feedSwipeRefresh) {
                    @Override
                    public void loadNextPage() {
                        presenter.loadData(currentCategory, Utils.SLUG_MAIN,
                                Utils.DEFAULT_COUNT, FeedOrientation.offsetPortrait, false);
                        onLoadingFeed();
                        if (!feedAdapter.getFeedModel().getPosts().isEmpty())
                            onTaskCompleted();
                    }
                });
    }

    private void orientationDebugging() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreateView: orientation = " + getActivity().getResources()
                    .getConfiguration().orientation);
        }
    }
}
