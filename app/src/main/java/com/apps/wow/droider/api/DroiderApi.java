package com.apps.wow.droider.api;

import com.apps.wow.droider.Model.NewFeedModel;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Jackson on 20/12/2016.
 */

public interface DroiderApi {

    @GET("/wp-content/themes/droider/feed.php?category=0&slug=main&count=12&offset=24")
    Observable<Response<NewFeedModel>> getFeed(@Query("category") String category,
                                               @Query("slug") String slug,
                                               @Query("count") int count,
                                               @Query("offset") int offset);


}
