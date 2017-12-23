package com.foolman.xlog.demo;

import android.app.Application;

import com.foolman.xlog.XLog;

/**
 * Created by wangyuebanzi on 2017/12/23.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XLog.init(BuildConfig.DEBUG,getFilesDir().getAbsolutePath());
    }
}
