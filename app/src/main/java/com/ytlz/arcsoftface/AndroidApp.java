package com.ytlz.arcsoftface;

import android.app.Application;

import com.ytlz.mylibrary.LitepalUtils;

/**
 * Created by wyb on 2019-04-26.
 */

public class AndroidApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LitepalUtils.init(this);
    }
}
