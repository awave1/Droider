package com.apps.wow.droider;

import android.app.Application;

import com.apps.wow.droider.Utils.AppContext;

/**
 * Created by Jackson on 14/05/2017.
 */

public class DroiderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        new AppContext(getApplicationContext());
    }
}
