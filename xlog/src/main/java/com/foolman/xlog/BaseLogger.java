package com.foolman.xlog;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.util.Log.getStackTraceString;

/**
 * Created by wangyuebanzi on 2017/12/14.
 */

public abstract class BaseLogger implements Logger {
    private static final int MAX_TAG_LENGTH = 23;
    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");
    static final int CALL_STACK_INDEX = 3;

    final ThreadLocal<String> explicitTag = new ThreadLocal<>();
    int defaultPriority = Log.VERBOSE;
    static Object lockObject = new Object();

    String getTag() {
        String tag = explicitTag.get();
        if (tag != null) {
            explicitTag.remove();
            return tag;
        }

        // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
        // because Robolectric runs them on the JVM but on Android the elements are different.
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length <= CALL_STACK_INDEX) {
            throw new IllegalStateException(
                    "Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }
        int needStackTraceIndex = CALL_STACK_INDEX;
        for (int i = CALL_STACK_INDEX; i < stackTrace.length; i++) {
            StackTraceElement e = stackTrace[i];
            String name = e.getClassName();
            if (!name.equals(XLog.class.getName()) && !name.equals(Loggers.class.getName())) {
                needStackTraceIndex = i;
                break;
            }
        }
        return createStackElementTag(stackTrace[needStackTraceIndex]);
    }

    @Nullable
    private String createStackElementTag(@NonNull StackTraceElement element) {
        String tag = element.getFileName();
        tag = tag.substring(0,tag.lastIndexOf('.'));
        // Tag length limit was removed in API 24.
        if (tag.length() <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return tag;
        }
        return tag.substring(0, MAX_TAG_LENGTH);
    }

    @Override
    public void log(int priority, String tag, String msg, Throwable throwable) {
        synchronized (lockObject) {
            if (tag == null) {
                tag = getTag();
            }

            if (!isLoggable(priority)) {
                return;
            }

            beforeLogMessage(priority, tag);

            String message = handleMessage(msg, throwable);

            realLog(priority, tag, message);

            afterLogMessage(priority, tag);
        }
    }

    protected abstract void realLog(int priority, String tag, String message);


    boolean isLoggable(int priority) {
        return priority >= defaultPriority;
    }

    private String handleMessage(String message, Throwable t) {

        if (message == null) {
            message = "(message is null!!!)";
        }
        if (message.isEmpty()) {
            message = "(message is empty!!!)";
        }
        if (t != null) {
            message += "\t\t" + getStackTraceString(t);
        }

        return message;
    }


    protected void afterLogMessage(int priority, String tag) {

    }


    protected void beforeLogMessage(int priority, String tag) {

    }


}
