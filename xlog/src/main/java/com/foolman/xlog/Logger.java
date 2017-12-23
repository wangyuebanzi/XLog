package com.foolman.xlog;

/**
 * Created by wangyuebanzi on 2017/12/14.
 */

public interface Logger {

    void log(int priority, String tag, String msg, Throwable throwable);

    void modifyLogLevel(int priority);
}
