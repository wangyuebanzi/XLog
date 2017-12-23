package com.foolman.xlog.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.foolman.xlog.DebugLogger;
import com.foolman.xlog.FileLogger;
import com.foolman.xlog.XLog;

public class DemoActivity extends AppCompatActivity {
    private static final String CUSTOM_TAG = "Custom_Tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        //默认当前类名为TAG
        XLog.v("activity is onCreate!!!");
        XLog.d("activity is onCreate!!!");
        XLog.i("activity is onCreate!!!");
        XLog.w("activity is onCreate!!!");
        XLog.e("activity is onCreate!!!");

        new Thread("testThread"){
            @Override
            public void run() {
                super.run();
                XLog.v("log in testThread!!!");
            }
        }.start();

        //自定义TAG
        XLog.v(CUSTOM_TAG, "activity is onCreate!!!");
        XLog.d(CUSTOM_TAG, "activity is onCreate!!!");
        XLog.i(CUSTOM_TAG, "activity is onCreate!!!");
        XLog.w(CUSTOM_TAG, "activity is onCreate!!!");
        XLog.e(CUSTOM_TAG, "activity is onCreate!!!");



        //设置不同需求的Logger
        XLog.clearAllLogger();
        XLog.addLogger(DebugLogger.newBuilder().showThreadInfo(false).methodCount(1).maxMessageLine(2).build());
        XLog.v("activity is onCreate!!!");

        XLog.clearAllLogger();
        XLog.addLogger(DebugLogger.newBuilder().showThreadInfo(false).methodCount(0).maxMessageLine(1).build());
        XLog.v("activity is onCreate!!!");

        XLog.clearAllLogger();
        XLog.addLogger(FileLogger.newBuilder().build());
    }
}
