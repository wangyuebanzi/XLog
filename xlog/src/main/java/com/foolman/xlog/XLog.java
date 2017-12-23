package com.foolman.xlog;

import android.util.Log;

/**
 * Created by wangyuebanzi on 2017/12/13.
 */

public class XLog {

    public static void init(boolean isDebug, String filePath) {
        if (isDebug) {
            Loggers.addLogger(DebugLogger.newBuilder().build());
            Loggers.addLogger(FileLogger.newBuilder().filePath(filePath).build());
        } else {
            Loggers.addLogger(FileLogger.newBuilder().filePath(filePath).build());
        }
    }

    public static void v(String msg) {
        v(null, msg);
    }

    public static void v(String tag, String msg) {
        log(Loggers.MSG, Log.VERBOSE, tag, msg, null, null);
    }


    public static void obj(Object object) {
        obj(null, object);
    }

    public static void obj(String tag, Object object) {
        log(Loggers.OBJECT, Log.DEBUG, tag, null, object, null);
    }

    public static void d(String msg) {
        d(null, msg);
    }

    public static void d(String tag, String msg) {
        log(Loggers.MSG, Log.DEBUG, tag, msg, null, null);
    }

    public static void i(String msg) {
        i(null, msg);
    }

    public static void i(String tag, String msg) {
        log(Loggers.MSG, Log.INFO, tag, msg, null, null);
    }

    public static void w(String msg) {
        w(null, msg);
    }

    public static void w(String tag, String msg) {
        log(Loggers.MSG, Log.WARN, tag, msg, null, null);
    }


    public static void e(String msg) {
        e(null, msg);
    }

    public static void e(String tag, String msg) {
        log(Loggers.MSG, Log.ERROR, tag, msg, null, null);
    }

    public static void e(Throwable throwable) {
        e(null, throwable);
    }

    public static void e(String tag, Throwable throwable) {
        log(Loggers.MSG, Log.ERROR, tag, null, null, throwable);
    }

    public static void json(String json) {
        json(null, json);
    }

    public static void json(String tag, String json) {
        log(Loggers.JSON, Log.DEBUG, tag, json, null, null);
    }

    public static void xml(String xml) {
        xml(null, xml);
    }

    public static void xml(String tag, String xml) {
        log(Loggers.XML, Log.DEBUG, tag, xml, null, null);
    }

    public static void log(int type, int priority, String tag, String msg, Object obj, Throwable t) {
        Loggers.log(type, priority, tag, msg, obj, t);
    }

    /**
     * 修改日志打印等级
     *
     * @param priority
     */
    public static void modifyLogLevel(int priority) {
        Loggers.modifyLogLevel(priority);
    }


    public static String exportLogFile() {
        return Loggers.exportLogFile();
    }

    public static void addLogger(BaseLogger logger) {
        Loggers.addLogger(logger);
    }

    public static void removeLogger(BaseLogger logger) {
        Loggers.removeLogger(logger);
    }

    public static void clearAllLogger() {
        Loggers.clearAllLogger();
    }

}
