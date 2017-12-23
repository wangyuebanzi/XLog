package com.foolman.xlog;

import android.content.Context;
import android.util.Log;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

/**
 * Created by wangyuebanzi on 2017/12/7.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LogTest {

    private static final String TAG = "LogTest";
    private static final String CUSTOM_TAG = "LogTest";
    private static final String LOG_TEMP_FILE = "XLog.temp";
    private static final String LOG_LAST_FILE = "XLog_last.txt";
    private static final String LOG_NOW_FILE = "XLog_now.txt";

    private Context context;

    private String currentThreadName;
    private String testThreadName = "test";

    @Before
    public void setUp() {
        context = RuntimeEnvironment.application;
    }

    @After
    public void tearDown() {
        clearLogFile();
        XLog.clearAllLogger();
    }

    private LogAssert assertLog() {
        return new LogAssert(ShadowLog.getLogs(), TAG);
    }


    private void clearLogFile() {
        File tempFile = new File(context.getFilesDir(), LOG_TEMP_FILE);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        File lastFile = new File(context.getFilesDir(), LOG_LAST_FILE);
        if (lastFile.exists()) {
            lastFile.delete();
        }
    }


    @Test
    public void testDebugLogger_CustomTag() {
        initDebugLogger();
        XLog.addLogger(DebugLogger.newBuilder().build());
        currentThreadName = Thread.currentThread().getName();
        XLog.v(CUSTOM_TAG, "testVerbose");
        XLog.d(CUSTOM_TAG, "testDebug");
        XLog.i(CUSTOM_TAG, "testInfo");
        XLog.w(CUSTOM_TAG, "testWarning");
        XLog.e(CUSTOM_TAG, "testError");
        assertLog().hasVerboseDefaultForm(CUSTOM_TAG, currentThreadName, "testVerbose")
                .hasDebugDefaultForm(CUSTOM_TAG, currentThreadName, "testDebug")
                .hasInfoDefaultForm(CUSTOM_TAG, currentThreadName, "testInfo")
                .hasWarnDefaultForm(CUSTOM_TAG, currentThreadName, "testWarning")
                .hasErrorDefaultForm(CUSTOM_TAG, currentThreadName, "testError")
                .hasNoMoreMessages();
    }

    @Test
    public void testDebugLogger_AutoTag() {
        initDebugLogger();
        XLog.addLogger(DebugLogger.newBuilder().build());
        currentThreadName = Thread.currentThread().getName();
        XLog.v("testVerbose");
        XLog.d("testDebug");
        XLog.i("testInfo");
        XLog.w("testWarning");
        XLog.e("testError");
        assertLog().hasVerboseDefaultForm(TAG, currentThreadName, "testVerbose")
                .hasDebugDefaultForm(TAG, currentThreadName, "testDebug")
                .hasInfoDefaultForm(TAG, currentThreadName, "testInfo")
                .hasWarnDefaultForm(TAG, currentThreadName, "testWarning")
                .hasErrorDefaultForm(TAG, currentThreadName, "testError")
                .hasNoMoreMessages();
    }

    @Test
    public void testDebugLogger_testThread() throws InterruptedException {
        initDebugLogger();
        XLog.addLogger(DebugLogger.newBuilder().methodCount(1).build());
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(testThreadName) {
            @Override
            public void run() {
                super.run();
                XLog.v("testVerbose");
                XLog.d("testDebug");
                XLog.i("testInfo");
                XLog.w("testWarning");
                XLog.e("testError");
                countDownLatch.countDown();
            }
        }.start();
        countDownLatch.await();
        assertLog().hasVerboseForm(TAG, testThreadName, 1, "testVerbose")
                .hasDebugForm(TAG, testThreadName, 1, "testDebug")
                .hasInfoForm(TAG, testThreadName, 1, "testInfo")
                .hasWarnForm(TAG, testThreadName, 1, "testWarning")
                .hasErrorForm(TAG, testThreadName, 1, "testError")
                .hasNoMoreMessages();
    }


    @Test
    public void testDebugLogger_modifyLogLevel() {
        initDebugLogger();
        XLog.addLogger(DebugLogger.newBuilder().build());
        currentThreadName = Thread.currentThread().getName();
        XLog.modifyLogLevel(Log.INFO);
        XLog.v("testVerbose");
        XLog.d("testDebug");
        XLog.i("testInfo");
        XLog.w("testWarning");
        XLog.e("testError");
        assertLog().hasInfoDefaultForm(TAG, currentThreadName, "testInfo")
                .hasWarnDefaultForm(TAG, currentThreadName, "testWarning")
                .hasErrorDefaultForm(TAG, currentThreadName, "testError")
                .hasNoMoreMessages();
    }


    @Test
    public void testDebugLogger_noThreadInfo() {
        initDebugLogger();
        XLog.addLogger(DebugLogger.newBuilder().showThreadInfo(false).build());
        XLog.d("testDebug");
        assertLog().hasDebugTopBorder(TAG).skip(2).hasDebugMiddleBorder(TAG)
                .hasDebugMessage(TAG, "testDebug").hasDebugBottomBorder(TAG)
                .hasNoMoreMessages();
    }

    @Test
    public void testDebugLogger_noMethodInfo() {
        initDebugLogger();
        XLog.addLogger(DebugLogger.newBuilder().methodCount(0).build());
        currentThreadName = Thread.currentThread().getName();
        XLog.d("testDebug");
        assertLog().hasDebugTopBorder(TAG).hasDebugThread(TAG, currentThreadName)
                .hasDebugMiddleBorder(TAG).hasDebugMessage(TAG, "testDebug")
                .hasDebugBottomBorder(TAG).hasNoMoreMessages();
    }

    @Test
    public void testDebugLogger_onlyMessage() {
        initDebugLogger();
        XLog.addLogger(DebugLogger.newBuilder().showThreadInfo(false).methodCount(0).build());
        XLog.d("testDebug");
        assertLog().hasDebugTopBorder(TAG).hasDebugMessage(TAG, "testDebug")
                .hasDebugBottomBorder(TAG).hasNoMoreMessages();
    }

    @Test
    public void testDebugLogger_exception() {
        initDebugLogger();
        XLog.addLogger(DebugLogger.newBuilder().showThreadInfo(false).methodCount(0).maxMessageLine(1).build());
        NullPointerException datThrowable = truncatedThrowable(NullPointerException.class);
        XLog.e(datThrowable);
        assertLog().hasExceptionForm(TAG, "", "java.lang.NullPointerException")
                .hasNoMoreMessages();
    }

    private void initDebugLogger() {
        XLog.init(true,context.getFilesDir().getAbsolutePath());
        XLog.clearAllLogger();
    }

    @Test
    public void testReleaseConsole() {
        XLog.init(false,context.getFilesDir().getAbsolutePath());
        XLog.v("testVerbose");
        XLog.d("testDebug");
        XLog.i("testInfo");
        XLog.w("testWarning");
        XLog.e("testError");
        assertLog().hasNoMoreMessages();
    }


    @Test
    public void testReleaseFile_autoTag() throws FileNotFoundException {
        XLog.init(false,context.getFilesDir().getAbsolutePath());
        XLog.v("testVerbose");
        XLog.d("testDebug");
        XLog.i("testInfo");
        XLog.w("testWarning");
        XLog.e("testError");
        sleepForAWhile();
        File file = new File(context.getFilesDir(), LOG_TEMP_FILE);
        if (!file.exists()) {
            fail();
        } else {
            List<ShadowLog.LogItem> logItemList = FileParser.parserFileToLogItem(file);
            assertLog().setLogItems(logItemList)
                    .hasFileVerboseMessage(TAG, "testVerbose")
                    .hasFileDebugMessage(TAG, "testDebug")
                    .hasFileInfoMessage(TAG, "testInfo")
                    .hasFileWarnMessage(TAG, "testWarning")
                    .hasFileErrorMessage(TAG, "testError")
                    .hasNoMoreMessages();
        }
    }

    @Test
    public void testReleaseFile_customTag() throws FileNotFoundException {
        XLog.init(false,context.getFilesDir().getAbsolutePath());
        XLog.v(CUSTOM_TAG, "testVerbose");
        XLog.d(CUSTOM_TAG, "testDebug");
        XLog.i(CUSTOM_TAG, "testInfo");
        XLog.w(CUSTOM_TAG, "testWarning");
        XLog.e(CUSTOM_TAG, "testError");
        sleepForAWhile();
        File file = new File(context.getFilesDir(), LOG_TEMP_FILE);
        if (!file.exists()) {
            fail();
        } else {
            List<ShadowLog.LogItem> logItemList = FileParser.parserFileToLogItem(file);
            assertLog().setLogItems(logItemList)
                    .hasFileVerboseMessage(CUSTOM_TAG, "testVerbose")
                    .hasFileDebugMessage(CUSTOM_TAG, "testDebug")
                    .hasFileInfoMessage(CUSTOM_TAG, "testInfo")
                    .hasFileWarnMessage(CUSTOM_TAG, "testWarning")
                    .hasFileErrorMessage(CUSTOM_TAG, "testError")
                    .hasNoMoreMessages();
        }

    }

    @Test
    public void testReleaseFile_testThread() throws InterruptedException, FileNotFoundException {
        XLog.init(false,context.getFilesDir().getAbsolutePath());
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        new Thread(testThreadName) {
            @Override
            public void run() {
                super.run();
                XLog.i("testInfo");
                countDownLatch.countDown();
            }
        }.start();
        countDownLatch.await();
        sleepForAWhile();
        File file = new File(context.getFilesDir(), LOG_TEMP_FILE);
        if (!file.exists()) {
            fail();
        } else {
            List<ShadowLog.LogItem> logItemList = FileParser.parserFileToLogItem(file);
            assertLog().setLogItems(logItemList)
                    .hasFileMessageWithThreadInfo(Log.INFO, TAG, "testInfo", testThreadName)
                    .hasNoMoreMessages();
        }

    }

    @Test
    public void testReleaseFile_modifyLogLevel() throws FileNotFoundException {
        XLog.init(false,context.getFilesDir().getAbsolutePath());
        XLog.modifyLogLevel(Log.INFO);
        XLog.v("testVerbose");
        XLog.d("testDebug");
        XLog.i("testInfo");
        XLog.w("testWarning");
        XLog.e("testError");
        sleepForAWhile();
        File file = new File(context.getFilesDir(), LOG_TEMP_FILE);
        if (!file.exists()) {
            fail();
        } else {
            List<ShadowLog.LogItem> logItemList = FileParser.parserFileToLogItem(file);
            assertLog().setLogItems(logItemList)
                    .hasFileInfoMessage(TAG, "testInfo")
                    .hasFileWarnMessage(TAG, "testWarning")
                    .hasFileErrorMessage(TAG, "testError")
                    .hasNoMoreMessages();
        }

    }


    @Test
    public void testReleaseConsole_exception() {
        XLog.init(false,context.getFilesDir().getAbsolutePath());
        NullPointerException datThrowable = truncatedThrowable(NullPointerException.class);
        XLog.e(TAG, datThrowable);
        assertLog().hasNoMoreMessages();
    }

    @Test
    public void testReleaseFile_exception() throws FileNotFoundException {
        XLog.init(false,context.getFilesDir().getAbsolutePath());
        NullPointerException datThrowable = truncatedThrowable(NullPointerException.class);
        XLog.e(datThrowable);
        sleepForAWhile();
        File file = new File(context.getFilesDir(), LOG_TEMP_FILE);
        if (!file.exists()) {
            fail();
        } else {
            List<ShadowLog.LogItem> logItemList = FileParser.parserFileToLogItem(file);
            assertLog().setLogItems(logItemList)
                    .hasExceptionMessage(TAG, "", "java.lang.NullPointerException")
                    .hasNoMoreMessages();
        }
    }


    @Test
    public void testReleaseFile_changeLogSize_lastNotExist() throws FileNotFoundException {
        XLog.addLogger(FileLogger.newBuilder().filePath(context.getFilesDir().getAbsolutePath()).logSize(100).build());
        XLog.d("modifyLogSize:100");
        XLog.e(repeat('a', 90));
        XLog.e(repeat('b', 90));
        sleepForAWhile();
        File tempFile = new File(context.getFilesDir(), LOG_TEMP_FILE);
        File lastFile = new File(context.getFilesDir(), LOG_LAST_FILE);
        if (!tempFile.exists()) {
            fail();
        } else {
            List<ShadowLog.LogItem> logItemList = FileParser.parserFileToLogItem(tempFile);
            assertLog().setLogItems(logItemList)
                    .hasFileErrorMessage(TAG, repeat('b', 90))
                    .hasNoMoreMessages();
        }
        if (!lastFile.exists()) {
            fail();
        } else {
            List<ShadowLog.LogItem> logItemList = FileParser.parserFileToLogItem(lastFile);
            assertLog().setLogItems(logItemList)
                    .hasFileDebugMessage(TAG, "modifyLogSize:100")
                    .hasFileErrorMessage(TAG, repeat('a', 90))
                    .hasNoMoreMessages();
        }
    }


    @Test
    public void testReleaseFile_changeLogSize_lastExist() throws FileNotFoundException {
        XLog.addLogger(FileLogger.newBuilder().filePath(context.getFilesDir().getAbsolutePath()).logSize(100).build());
        //前两次LOG保证了LOG_LAST_FILE是存在的
        XLog.e(repeat('a', 90));
        XLog.e(repeat('b', 90));
        XLog.e(repeat('c', 90));
        sleepForAWhile();
        File tempFile = new File(context.getFilesDir(), LOG_TEMP_FILE);
        File lastFile = new File(context.getFilesDir(), LOG_LAST_FILE);
        if (!tempFile.exists()) {
            fail();
        } else {
            List<ShadowLog.LogItem> logItemList = FileParser.parserFileToLogItem(tempFile);
            assertLog().setLogItems(logItemList)
                    .hasFileErrorMessage(TAG, repeat('c', 90))
                    .hasNoMoreMessages();
        }
        if (!lastFile.exists()) {
            fail();
        } else {
            List<ShadowLog.LogItem> logItemList = FileParser.parserFileToLogItem(lastFile);
            assertLog().setLogItems(logItemList)
                    .hasFileErrorMessage(TAG, repeat('b', 90))
                    .hasNoMoreMessages();
        }
    }


    @Test
    public void testReleaseFile_exportFile() throws FileNotFoundException {
        XLog.addLogger(FileLogger.newBuilder().filePath(context.getFilesDir().getAbsolutePath()).logSize(100).build());
        XLog.e(repeat('a', 90));
        XLog.e(repeat('b', 90));
        XLog.e(repeat('c', 90));
        sleepForAWhile();
        String logFilePath = XLog.exportLogFile();
        File tempFile = new File(context.getFilesDir(), LOG_NOW_FILE);
        if (!tempFile.exists()) {
            fail();
        } else {
            assertThat(logFilePath).isEqualTo(tempFile.getAbsolutePath());
            List<ShadowLog.LogItem> logItemList = FileParser.parserFileToLogItem(tempFile);
            assertLog().setLogItems(logItemList)
                    .hasFileErrorMessage(TAG, repeat('b', 90))
                    .hasFileErrorMessage(TAG, repeat('c', 90))
                    .hasNoMoreMessages();
        }

    }

    private void sleepForAWhile(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String repeat(char ch, int times) {
        char[] data = new char[times];
        Arrays.fill(data, ch);
        return new String(data);
    }


    private static <T extends Throwable> T truncatedThrowable(Class<T> throwableClass) {
        try {
            T throwable = throwableClass.newInstance();
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            int traceLength = stackTrace.length > 5 ? 5 : stackTrace.length;
            throwable.setStackTrace(Arrays.copyOf(stackTrace, traceLength));
            return throwable;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
