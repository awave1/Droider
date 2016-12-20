package com.apps.wow.droider.Feed.Presentor;

public interface FeedPresenter {
  void loadData(String category, String slug, int count, int offset);

  void loadMore(String url);

  void getDataWithClearing(String url);
}
