package com.a21vianet.sample.customview;

import android.app.Application;
import android.content.Context;

/**
 * Created by wang.rongqiang on 2017/7/11.
 */

public class MyApplication extends Application {
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
    }

    public static Context getContext() {
        return sContext;
    }
}
