package com.apps.wow.droider.Feed.Presentor;

 interface FeedPresenter {

    void loadData(String category, String slug, int count, int offset, boolean clear);

     void loadPopular();
}
