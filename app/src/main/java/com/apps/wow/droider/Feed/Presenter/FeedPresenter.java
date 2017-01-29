package com.apps.wow.droider.Feed.Presenter;

 interface FeedPresenter {

    void loadData(String category, String slug, int count, int offset, boolean clear);

     void loadPopular();
}
