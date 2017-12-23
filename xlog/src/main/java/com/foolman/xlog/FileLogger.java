package com.foolman.xlog;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;


import com.foolman.xlog.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangyuebanzi on 2017/12/13.
 */

public class FileLogger extends BaseLogger implements ExtendLogger {

    private static final String TAG = FileLogger.class.getSimpleName();
    private static final String LOG_TEMP_FILE = "XLog.temp";
    private static final String LOG_LAST_FILE = "XLog_last.txt";
    private static final String LOG_NOW_FILE = "XLog_now.txt";
    private static final Object mLockObj = new Object();


    private long currentLogSize;
    private boolean mUseTask = true;
    private OutputStream mLogStream;
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(1);

    private final Date date;
    private final SimpleDateFormat dateFormat;
    private final String logPath;
    private final long defaultLogMaxSize;

    private FileLogger(Builder builder) {
        date = builder.date;
        dateFormat = builder.dateFormat;
        logPath = builder.filePath;
        defaultLogMaxSize = builder.logSize;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override
    protected void realLog(int priority, @Nullable String tag, @NonNull String message) {
        synchronized (lockObject) {
            message = getFormatLog(priority, tag, message);
            if (mUseTask) {
                final String Tag = tag;
                final String Msg = message;
                final int Priority = priority;
                try {
                    if (mExecutorService != null) {
                        mExecutorService.submit(new Runnable() {
                            @Override
                            public void run() {
                                logToFile(Tag, Msg, Priority);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "log -> " + e.toString());
                }
            } else {
                logToFile(tag, message, priority);
            }
        }

    }


    /**
     * 将log打到文件日志
     *
     * @param tag
     * @param msg
     * @param level
     */
    private void logToFile(String tag, String msg, int level) {
        synchronized (mLockObj) {
            OutputStream outStream = openLogFileOutStream();
            if (outStream != null) {
                try {
                    if (currentLogSize < defaultLogMaxSize) {
                        byte[] d = msg.getBytes("utf-8");
                        outStream.write(d);
                        outStream.write("\r\n".getBytes());
                        outStream.flush();
                        currentLogSize += d.length;
                        closeLogFileOutStream();
                    } else {
                        closeLogFileOutStream();
                        renameLogFile();
                        logToFile(tag, msg, level);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "logToFile -> " + e.toString());
                }
            } else {
                Log.w(TAG, "Log File open fail: [AppPath]=" + logPath + ",[LogName]:");
            }
        }
    }

    /**
     * 获取日志临时文件输入流
     *
     * @return
     */
    private OutputStream openLogFileOutStream() {
        if (mLogStream == null) {
            try {
                if (TextUtils.isEmpty(logPath)) {
                    return null;
                }
                File file = openAbsoluteFile(LOG_TEMP_FILE);

                if (file == null) {
                    return null;
                }

                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }

                if (file.exists()) {
                    mLogStream = new FileOutputStream(file, true);
                    currentLogSize = file.length();
                } else {
                    //	file.createNewFile();
                    mLogStream = new FileOutputStream(file);
                    currentLogSize = 0;
                }
            } catch (IOException e) {
                Log.e(TAG, "openLogFileOutStream -> " + e.toString());
            }
        }
        return mLogStream;
    }


    private File openAbsoluteFile(String name) {
        if (TextUtils.isEmpty(logPath)) {
            return null;
        } else {
            File file = new File(logPath, name);
            return file;
        }
    }

    /**
     * 关闭日志输出流
     */
    private void closeLogFileOutStream() {
        try {
            if (mLogStream != null) {
                mLogStream.close();
                mLogStream = null;
                currentLogSize = 0;
            }
        } catch (Exception e) {
            Log.e(TAG, "closeLogFileOutStream -> " + e.toString());
        }
    }


    /**
     * rename log file
     */
    private void renameLogFile() {
        synchronized (mLockObj) {
            File file = openAbsoluteFile(LOG_TEMP_FILE);
            File destFile = openAbsoluteFile(LOG_LAST_FILE);

            if (destFile != null && destFile.exists()) {
                destFile.delete();
            }
            if (file != null) {
                file.renameTo(destFile);
            }
        }
    }


    private String getFormatLog(int priority, String tag, String message) {
        String lev;
        switch (priority) {
            case Log.VERBOSE:
                lev = "V";
                break;
            case Log.DEBUG:
                lev = "D";
                break;
            case Log.INFO:
                lev = "I";
                break;
            case Log.WARN:
                lev = "W";
                break;
            case Log.ERROR:
                lev = "E";
                break;
            case Log.ASSERT:
                lev = "A";
                break;
            default:
                lev = "";
        }

        date.setTime(System.currentTimeMillis());
        String dt = dateFormat.format(date);
        String threadName = Thread.currentThread().getName();
        StringBuffer mBuffer = new StringBuffer();
        mBuffer.setLength(0);
        mBuffer.append("[");
        mBuffer.append(dt);
        if (!TextUtils.isEmpty(threadName) && !threadName.equals("main")) {
            mBuffer.append(" " + threadName);
        }
        mBuffer.append(" " + tag);
        if (!lev.trim().isEmpty()) {
            mBuffer.append("(");
            mBuffer.append(lev);
            mBuffer.append(")");
        }
        mBuffer.append("] : ");
        mBuffer.append(message);
        return mBuffer.toString();
    }

    @Override
    public String exportLogFile() {
        return backLogFile();
    }

    /**
     * back now log file
     */
    private String backLogFile() {
        synchronized (mLockObj) {
            try {
                closeLogFileOutStream();

                File destFile = openAbsoluteFile(LOG_NOW_FILE);

                if (destFile != null && destFile.exists()) {
                    destFile.delete();
                }

                try {
                    destFile.createNewFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return null;
                }

                File srcFile1 = openAbsoluteFile(LOG_LAST_FILE);
                File srcFile2 = openAbsoluteFile(LOG_TEMP_FILE);

                FileUtils.mergerFile(srcFile1, srcFile2, destFile);

                openLogFileOutStream();
                return destFile.getAbsolutePath();
            } catch (IOException e) {
                Log.e(TAG, "backLogFile -> " + e.toString());
            }
            return null;
        }
    }

    @Override
    public void modifyLogLevel(int priority) {
        defaultPriority = priority;
    }

    public void setAsyncWriteFile(boolean isAsync) {
        mUseTask = isAsync;
    }

    public static final class Builder {
        Date date;
        SimpleDateFormat dateFormat;
        String filePath;
        long logSize = 4 * 1024 * 1024;

        private Builder() {

        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder dateFormat(SimpleDateFormat dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder logSize(long logSize) {
            this.logSize = logSize;
            return this;
        }

        public FileLogger build() {
            if (date == null) {
                date = new Date();
            }
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            }

            if (filePath == null) {
                if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    throw new NullPointerException("SD卡不存在，请设置日志存放目录！！！");
                }
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
            return new FileLogger(this);
        }

    }

}
