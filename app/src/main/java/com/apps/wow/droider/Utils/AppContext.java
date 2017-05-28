package com.apps.wow.droider.Utils;

import android.content.Context;

/**
 * Created by Jackson on 14/05/2017.
 */

public class AppContext {

    static Context mContext;

    public AppContext(Context mContext) {
        this.mContext = mContext;
    }

    public static Context getContext() {
        return mContext;
    }
}
