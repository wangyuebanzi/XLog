package com.foolman.xlog;

import android.text.TextUtils;
import android.util.Log;

import org.robolectric.shadows.ShadowLog;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by wangyuebanzi on 2017/12/18.
 */

public class LogAssert {
    private static final int defaultMethodCount = 2;
    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char MIDDLE_CORNER = '├';
    private static final char HORIZONTAL_LINE = '│';
    private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    private List<ShadowLog.LogItem> items;

    private String autoTag;

    private int index = 0;

    protected LogAssert(List<ShadowLog.LogItem> items, String autoTag) {
        this.items = items;
        this.autoTag = autoTag;
    }

    LogAssert setLogItems(List<ShadowLog.LogItem> items) {
        this.items = items;
        return this;
    }


    LogAssert hasVerboseDefaultForm(String tag, String threadName, String message) {
        return hasVerboseForm(tag, threadName, defaultMethodCount, message);
    }

    LogAssert hasVerboseForm(String tag, String threadName, int methodCount, String message) {
        return hasVerboseTopBorder(tag).hasVerboseThread(tag, threadName)
                .hasVerboseMiddleBorder(tag).skip(methodCount)
                .hasVerboseMiddleBorder(tag).hasVerboseMessage(tag, message)
                .hasVerboseBottomBorder(tag);
    }

    LogAssert hasDebugDefaultForm(String tag, String threadName, String message) {
        return hasDebugForm(tag, threadName, defaultMethodCount, message);
    }

    LogAssert hasDebugForm(String tag, String threadName, int methodCount, String message) {
        return hasDebugTopBorder(tag).hasDebugThread(tag, threadName)
                .hasDebugMiddleBorder(tag).skip(methodCount)
                .hasDebugMiddleBorder(tag).hasDebugMessage(tag, message)
                .hasDebugBottomBorder(tag);
    }


    LogAssert hasInfoDefaultForm(String tag, String threadName, String message) {
        return hasInfoForm(tag, threadName, defaultMethodCount, message);
    }

    LogAssert hasInfoForm(String tag, String threadName, int methodCount, String message) {
        return hasInfoTopBorder(tag).hasInfoThread(tag, threadName)
                .hasInfoMiddleBorder(tag).skip(methodCount)
                .hasInfoMiddleBorder(tag).hasInfoMessage(tag, message)
                .hasInfoBottomBorder(tag);
    }

    LogAssert hasWarnDefaultForm(String tag, String threadName, String message) {
        return hasWarnForm(tag, threadName, defaultMethodCount, message);
    }

    LogAssert hasWarnForm(String tag, String threadName, int methodCount, String message) {
        return hasWarnTopBorder(tag).hasWarnThread(tag, threadName)
                .hasWarnMiddleBorder(tag).skip(methodCount)
                .hasWarnMiddleBorder(tag).hasWarnMessage(tag, message)
                .hasWarnBottomBorder(tag);
    }


    LogAssert hasErrorDefaultForm(String tag, String threadName, String message) {
        return hasErrorForm(tag, threadName, defaultMethodCount, message);
    }

    LogAssert hasErrorForm(String tag, String threadName, int methodCount, String message) {
        return hasErrorTopBorder(tag).hasErrorThread(tag, threadName)
                .hasErrorMiddleBorder(tag).skip(methodCount)
                .hasErrorMiddleBorder(tag).hasErrorMessage(tag, message)
                .hasErrorBottomBorder(tag);
    }

    LogAssert hasVerboseTopBorder(String tag) {
        return hasLog(Log.VERBOSE, tag, TOP_BORDER);
    }

    LogAssert hasDebugTopBorder(String tag) {
        return hasLog(Log.DEBUG, tag, TOP_BORDER);
    }

    LogAssert hasInfoTopBorder(String tag) {
        return hasLog(Log.INFO, tag, TOP_BORDER);
    }

    LogAssert hasWarnTopBorder(String tag) {
        return hasLog(Log.WARN, tag, TOP_BORDER);
    }

    LogAssert hasErrorTopBorder(String tag) {
        return hasLog(Log.ERROR, tag, TOP_BORDER);
    }

    LogAssert hasVerboseBottomBorder(String tag) {
        return hasLog(Log.VERBOSE, tag, BOTTOM_BORDER);
    }

    LogAssert hasDebugBottomBorder(String tag) {
        return hasLog(Log.DEBUG, tag, BOTTOM_BORDER);
    }

    LogAssert hasInfoBottomBorder(String tag) {
        return hasLog(Log.INFO, tag, BOTTOM_BORDER);
    }

    LogAssert hasWarnBottomBorder(String tag) {
        return hasLog(Log.WARN, tag, BOTTOM_BORDER);
    }

    LogAssert hasErrorBottomBorder(String tag) {
        return hasLog(Log.ERROR, tag, BOTTOM_BORDER);
    }


    LogAssert hasVerboseMiddleBorder(String tag) {
        return hasLog(Log.VERBOSE, tag, MIDDLE_BORDER);
    }

    LogAssert hasDebugMiddleBorder(String tag) {
        return hasLog(Log.DEBUG, tag, MIDDLE_BORDER);
    }

    LogAssert hasInfoMiddleBorder(String tag) {
        return hasLog(Log.INFO, tag, MIDDLE_BORDER);
    }

    LogAssert hasWarnMiddleBorder(String tag) {
        return hasLog(Log.WARN, tag, MIDDLE_BORDER);
    }

    LogAssert hasErrorMiddleBorder(String tag) {
        return hasLog(Log.ERROR, tag, MIDDLE_BORDER);
    }


    LogAssert hasVerboseThread(String tag, String threadName) {
        return hasThread(Log.VERBOSE, tag, threadName);
    }

    LogAssert hasDebugThread(String tag, String threadName) {
        return hasThread(Log.DEBUG, tag, threadName);
    }

    LogAssert hasInfoThread(String tag, String threadName) {
        return hasThread(Log.INFO, tag, threadName);
    }

    LogAssert hasWarnThread(String tag, String threadName) {
        return hasThread(Log.WARN, tag, threadName);
    }

    LogAssert hasErrorThread(String tag, String threadName) {
        return hasThread(Log.ERROR, tag, threadName);
    }


    LogAssert hasThread(int priority, String tag, String threadName) {
        return hasLog(priority, tag, HORIZONTAL_LINE + " " + "Thread: " + threadName);
    }

    LogAssert hasVerboseMethodInfo(String tag, String methodInfo) {
        return hasMethodInfo(Log.VERBOSE, tag, methodInfo);
    }

    LogAssert hasDebugMethodInfo(String tag, String methodInfo) {
        return hasMethodInfo(Log.DEBUG, tag, methodInfo);
    }

    LogAssert hasInfoMethodInfo(String tag, String methodInfo) {
        return hasMethodInfo(Log.INFO, tag, methodInfo);
    }

    LogAssert hasWarnMethodInfo(String tag, String methodInfo) {
        return hasMethodInfo(Log.WARN, tag, methodInfo);
    }

    LogAssert hasErrorMethodInfo(String tag, String methodInfo) {
        return hasMethodInfo(Log.ERROR, tag, methodInfo);
    }


    LogAssert hasMethodInfo(int priority, String tag, String methodInfo) {
        return hasLog(priority, tag, HORIZONTAL_LINE + " " + methodInfo);
    }

    public LogAssert hasVerboseMessage(String tag, String message) {
        return hasMessage(Log.VERBOSE, tag, message);
    }

    public LogAssert hasDebugMessage(String tag, String message) {
        return hasMessage(Log.DEBUG, tag, message);
    }

    public LogAssert hasInfoMessage(String tag, String message) {
        return hasMessage(Log.INFO, tag, message);
    }

    public LogAssert hasWarnMessage(String tag, String message) {
        return hasMessage(Log.WARN, tag, message);
    }

    public LogAssert hasErrorMessage(String tag, String message) {
        return hasMessage(Log.ERROR, tag, message);
    }

    LogAssert hasMessage(int priority, String tag, String message) {
        return hasLog(priority, tag, HORIZONTAL_LINE + " " + message);
    }

    public LogAssert hasFileVerboseMessage(String tag, String message) {
        return hasFileMessage(Log.VERBOSE, tag, message);
    }

    public LogAssert hasFileDebugMessage(String tag, String message) {
        return hasFileMessage(Log.DEBUG, tag, message);
    }

    public LogAssert hasFileInfoMessage(String tag, String message) {
        return hasFileMessage(Log.INFO, tag, message);
    }

    public LogAssert hasFileWarnMessage(String tag, String message) {
        return hasFileMessage(Log.WARN, tag, message);
    }

    public LogAssert hasFileErrorMessage(String tag, String message) {
        return hasFileMessage(Log.ERROR, tag, message);
    }

    LogAssert hasFileMessage(int priority, String tag, String message) {
        return hasLog(priority, tag, message);
    }

    LogAssert hasFileMessageWithThreadInfo(int priority, String tag, String message, String threadName) {
        ShadowLog.LogItem item = items.get(index++);
        assertThat(item.type).isEqualTo(priority);
        assertThat(item.tag).isEqualTo(tag != null ? tag : autoTag);
        assertThat(item.msg).contains(message);
        assertThat(item.msg).contains(" threadName:" + threadName);
        return this;
    }

    LogAssert skip() {
        index++;
        return this;
    }

    LogAssert skip(int count) {
        index += count;
        return this;
    }

    public LogAssert hasExceptionForm(String tag, String message, String exceptionClass) {
        return hasErrorTopBorder(tag).hasExceptionMessage(tag, message, exceptionClass)
                .hasErrorBottomBorder(tag);
    }

    public LogAssert hasExceptionMessage(String tag, String message, String exceptionClass) {
        ShadowLog.LogItem log = items.get(index++);
        assertThat(log.type).isEqualTo(Log.ERROR);
        assertThat(log.tag).isEqualTo(tag != null ? tag : autoTag);
        if (!TextUtils.isEmpty(message)) {
            assertThat(log.msg).startsWith(HORIZONTAL_LINE + " " + message);
        }
        assertThat(log.msg).contains(exceptionClass);
        assertThat(log.throwable).isNull();
        return this;
    }

    private LogAssert hasLog(int priority, String tag, String message) {
        ShadowLog.LogItem item = items.get(index++);
        assertThat(item.type).isEqualTo(priority);
        assertThat(item.tag).isEqualTo(tag != null ? tag : autoTag);
        assertThat(item.msg).contains(message);
        return this;
    }

    public void hasNoMoreMessages() {
        assertThat(items).hasSize(index);
    }


}
