package com.foolman.xlog;

import android.support.annotation.NonNull;
import android.util.Log;


/**
 * Created by wangyuebanzi on 2017/12/13.
 */

public class DebugLogger extends BaseLogger {

    private static final int MAX_LOG_LENGTH = 4000;

    /**
     * Drawing toolbox
     */
    private static final char TOP_LEFT_CORNER = '┌';
    private static final char BOTTOM_LEFT_CORNER = '└';
    private static final char MIDDLE_CORNER = '├';
    private static final char HORIZONTAL_LINE = '│';
    private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
    private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
    private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
    private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

    private final int methodCount;
    private final int methodOffset;
    private final int maxMessageLine;
    private final boolean showThreadInfo;

    private DebugLogger(Builder builder) {
        methodCount = builder.methodCount;
        methodOffset = builder.methodOffset;
        maxMessageLine = builder.maxMessageLine;
        showThreadInfo = builder.showThreadInfo;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    protected void beforeLogMessage(int priority, String tag) {
        logTopBorder(priority, tag);
        logHeaderContent(priority, tag, methodCount);
    }


    @Override
    protected void afterLogMessage(int priority, String tag) {
        logBottomBorder(priority, tag);
    }


    private void logTopBorder(int priority, String tag) {
        Log.println(priority, tag, TOP_BORDER);
    }


    private void logHeaderContent(int priority, String tag, int methodCount) {
        StackTraceElement[] trace = new Throwable().getStackTrace();
        if (showThreadInfo) {
            Log.println(priority, tag, HORIZONTAL_LINE + " Thread: " + Thread.currentThread().getName());
            logDivider(priority, tag);
        }
        String level = "";

        int stackOffset = getStackOffset(trace) + methodOffset;

        //corresponding method count with the current stack may exceeds the stack trace. Trims the count
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(HORIZONTAL_LINE)
                    .append(' ')
                    .append(level)
                    .append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
            level += "   ";
            Log.println(priority, tag, builder.toString());
        }
        if (methodCount > 0) {
            logDivider(priority, tag);
        }
    }

    private int getStackOffset(StackTraceElement[] trace) {
        for (int i = CALL_STACK_INDEX; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(XLog.class.getName()) && !name.equals(Loggers.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

    private String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    private void logBottomBorder(int priority, String tag) {
        Log.println(priority, tag, BOTTOM_BORDER);
    }

    private void logDivider(int priority, String tag) {
        Log.println(priority, tag, MIDDLE_BORDER);
    }

    protected void realLog(int priority, String tag, @NonNull String message) {

        int line = 0;
        for (int i = 0, length = message.length(); i < length; i++) {
            int newline = message.indexOf('\n', i);
            newline = newline != -1 ? newline : length;
            do {
                int end = Math.min(newline, i + MAX_LOG_LENGTH);
                String part = message.substring(i, end);
                if (part.startsWith("\tat ")) {
                    part = HORIZONTAL_LINE + "   " + part;
                } else {
                    part = HORIZONTAL_LINE + " " + part;
                }
                Log.println(priority, tag, part);
                if (++line == maxMessageLine) {
                    return;
                }
                i = end;
            } while (i < newline);
        }
    }

    @Override
    public void modifyLogLevel(int priority) {
        defaultPriority = priority;
    }


    public final static class Builder {
        private int methodCount = 2;
        private int methodOffset = 0;
        private int maxMessageLine = Integer.MAX_VALUE;
        private boolean showThreadInfo = true;

        private Builder() {

        }

        public Builder methodCount(int count) {
            this.methodCount = count;
            return this;
        }

        public Builder methodOffset(int methodOffset) {
            this.methodOffset = methodOffset;
            return this;
        }

        public Builder maxMessageLine(int maxMessageLine) {
            this.maxMessageLine = maxMessageLine;
            return this;
        }

        public Builder showThreadInfo(boolean showThreadInfo) {
            this.showThreadInfo = showThreadInfo;
            return this;
        }

        public DebugLogger build() {
            return new DebugLogger(this);
        }
    }
}
