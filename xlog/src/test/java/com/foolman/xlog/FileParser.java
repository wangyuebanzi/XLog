package com.foolman.xlog;

import android.util.Log;

import org.robolectric.shadows.ShadowLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangyuebanzi on 2017/12/12.
 */

public class FileParser {
    public static List<ShadowLog.LogItem> parserFileToLogItem(File file) throws FileNotFoundException {
        if (file == null || !file.exists() || file.isDirectory()) {
            throw new FileNotFoundException("此文件不存在！！！");
        }
        List<ShadowLog.LogItem> logItemList = new ArrayList<>();
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                ShadowLog.LogItem logItem = parserFileToLogItem(line);
                if (logItem != null) {
                    logItemList.add(logItem);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
                bufferedReader = null;
            }
            if (fileReader != null) {
                fileReader.close();
                fileReader = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logItemList;
    }

    /***
     * 将字符串转换成LogItem
     * 格式：[2017-12-18 18:00:56 Loggers(D)] : modifyLogSize:100
     *   或者  [2017-12-18 18:00:56 test Loggers(D)] : modifyLogSize:100
     *   注意在命令行模式下的执行线程是：Test worker所以需要修改下解析的方法
     *   GUI执行的线程是main
     * @param str
     * @return
     */
    public static ShadowLog.LogItem parserFileToLogItem(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        if (!str.startsWith("[")) {
            return null;
        }
        String[] strings = str.split(" : ");
        if (strings.length != 2) {
            return null;
        }
        String msg = strings[1];
        String mixStr = strings[0];
        String[] mixStrings = mixStr.split(" ");
        if (mixStrings.length < 3) {
            return null;
        }
        if (mixStrings.length > 3) {
            String threadName = "threadName:";
            for (int i = 2; i < mixStrings.length - 1; i++) {
                threadName += mixStrings[2] + " ";
            }
            msg += " " + threadName.trim();
        }
        String temp = mixStrings[mixStrings.length - 1];
        String[] tempStrings = temp.split("\\(");
        if (tempStrings.length != 2) {
            return null;
        }
        String tag = tempStrings[0];
        String lev = tempStrings[1].substring(0, 1);
        int type = change2Int(lev);
        ShadowLog.LogItem logItem = new ShadowLog.LogItem(type, tag, msg, null);
        return logItem;
    }

    private static int change2Int(String level) {
        if (level == null || level.length() == 0) {
            return 0;
        }
        int type;
        switch (level) {
            case "V":
                type = Log.VERBOSE;
                break;
            case "D":
                type = Log.DEBUG;
                break;
            case "I":
                type = Log.INFO;
                break;
            case "W":
                type = Log.WARN;
                break;
            case "E":
                type = Log.ERROR;
                break;
            default:
                type = 0;
                break;
        }
        return type;
    }
}
