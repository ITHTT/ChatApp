package com.tysci.applibrary.app;

import android.app.Application;

/**
 * Created by Administrator on 2015/11/10.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActivityStacksManager.initActivityStacksManager();
    }
}
