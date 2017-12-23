package com.foolman.xlog;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * Created by wangyuebanzi on 2017/12/15.
 */

public class Loggers {

    public static final int MSG = 0;
    public static final int JSON = 1;
    public static final int XML = 2;
    public static final int OBJECT = 3;
    private static final int JSON_INDENT = 2;

    private static final List<BaseLogger> loggers = new ArrayList<>();

    @VisibleForTesting
    public void setAsyncWriteFile(boolean isAsync) {
        for (BaseLogger logger : loggers) {
            if (logger instanceof FileLogger) {
                ((FileLogger) logger).setAsyncWriteFile(isAsync);
            }

        }
    }

    public static void log(int type, int priority, String tag, String msg, Object obj, Throwable t) {
        switch (type) {
            case OBJECT:
                String objMessage = handleObject(obj);
                for (BaseLogger logger : loggers) {
                    logger.log(priority, tag, objMessage, null);
                }
                break;
            case JSON:
                String jsonMessage = json(msg);
                for (BaseLogger logger : loggers) {
                    logger.log(priority, tag, jsonMessage, null);
                }
                break;
            case XML:
                String xmlMessage = xml(msg);
                for (BaseLogger logger : loggers) {
                    logger.log(priority, tag, xmlMessage, null);
                }
                break;
            default:
                for (BaseLogger logger : loggers) {
                    logger.log(priority, tag, msg, t);
                }

        }

    }

    public static String json(String json) {
        if (TextUtils.isEmpty(json)) {
            return "Empty/Null json content";
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(JSON_INDENT);
                return message;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(JSON_INDENT);
                return message;
            }
        } catch (JSONException e) {
            return "Invalid Json";
        }
        return "";
    }

    public static String xml(String xml) {
        if (TextUtils.isEmpty(xml)) {
            return "Empty/Null xml content";
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (TransformerException e) {
            return "Invalid xml";
        }

    }

    private static String handleObject(Object object) {

        if (object == null) {
            return "object is null";
        }
        if (!object.getClass().isArray()) {
            if (object instanceof List) {
                return handleList((List) object);
            }
            if (object instanceof Set) {
                return handleSet((Set) object);
            }

            if (object instanceof Map) {
                return handleMap((Map) object);
            }
            return object.toString();
        }
        if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        }
        if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        }
        if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        }
        if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        }
        if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        }
        if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        }
        if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        }
        if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        }
        if (object instanceof Object[]) {
            return Arrays.deepToString((Object[]) object);
        }
        return "Couldn't find a correct type for the object";
    }

    private static String handleSet(Set set) {
        if (set.isEmpty()) {
            return "Set is empty!!!";
        } else {
            Iterator iterator = set.iterator();
            StringBuilder stringBuilder = new StringBuilder();
            while (iterator != null && iterator.hasNext()) {
                Object object = iterator.next();
                if (object != null) {
                    stringBuilder.append(handleObject(object) + ",");
                }
            }
            int index = stringBuilder.lastIndexOf(",");
            return stringBuilder.toString().substring(0, index);
        }
    }

    private static String handleList(List list) {
        if (list.isEmpty()) {
            return "List is empty!!!";
        } else {
            Iterator iterator = list.iterator();
            StringBuilder stringBuilder = new StringBuilder();
            while (iterator != null && iterator.hasNext()) {
                Object object = iterator.next();
                if (object != null) {
                    stringBuilder.append(handleObject(object) + ",");
                }
            }
            int index = stringBuilder.lastIndexOf(",");
            return stringBuilder.toString().substring(0, index);
        }
    }

    private static String handleMap(Map map) {
        if (map.isEmpty()) {
            return "Map is empty!!!";
        } else {
            Iterator<Map.Entry> iterator = map.entrySet().iterator();
            StringBuilder stringBuilder = new StringBuilder();
            while (iterator != null && iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                if (entry != null) {
                    stringBuilder.append("(" + handleObject(entry.getKey()) + " : " + handleObject(entry.getValue()) + ")\n");
                }
            }
            return stringBuilder.toString();
        }
    }


    public static void modifyLogLevel(int priority) {
        for (BaseLogger logger : loggers) {
            logger.modifyLogLevel(priority);
        }
    }

    public static String exportLogFile() {
        for (BaseLogger logger : loggers) {
            if (logger instanceof ExtendLogger) {
                return ((ExtendLogger) logger).exportLogFile();
            }
        }
        return null;
    }


    /**
     * Add a new logger.
     */
    @SuppressWarnings("ConstantConditions") // Validating public API contract.
    public static void addLogger(@NonNull BaseLogger logger) {
        if (logger == null) {
            throw new NullPointerException("logger == null");
        }
        synchronized (loggers) {
            loggers.add(logger);
        }
    }


    public static void removeLogger(@NonNull BaseLogger logger) {
        synchronized (loggers) {
            if (!loggers.remove(logger)) {
                throw new IllegalArgumentException("Cannot remove logger which is not exist in loggers: " + logger);
            }
        }
    }

    public static void clearAllLogger() {
        synchronized (loggers) {
            loggers.clear();
        }
    }

    public static int loggerCount() {
        synchronized (loggers) {
            return loggers.size();
        }
    }
}
