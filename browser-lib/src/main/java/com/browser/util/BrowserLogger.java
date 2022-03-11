package com.browser.util;

import android.util.Log;

import com.browser.BrowserSdk;


/**
 * Created by Amit on 7/1/2017.
 */

public class BrowserLogger {
    public static final String SDK_NAME = "BrowserSdk";
    public static final String TAG = SDK_NAME + "-OkHttp-log";

    public static void e(String s) {
        if (BrowserSdk.getInstance().isEnableDebugMode()) {
            Log.d(TAG, LINE_BREAK_START);
            Log.d(TAG, s);
            Log.d(TAG, LINE_BREAK_END);
        }
    }

    public static void e(String q, String s) {
        if (BrowserSdk.getInstance().isEnableDebugMode()) {
            Log.d(TAG, LINE_BREAK_START);
            Log.d(TAG, q + " : " + s);
            Log.d(TAG, LINE_BREAK_END);
        }
    }

    public static void d(String s) {
        if (BrowserSdk.getInstance().isEnableDebugMode()) {
            Log.d(TAG, LINE_BREAK_START);
            Log.d(TAG, s);
            Log.d(TAG, LINE_BREAK_END);
        }
    }

    public static void d(String... s) {
        if (BrowserSdk.getInstance().isEnableDebugMode()) {
            Log.d(TAG, LINE_BREAK_START);
            for (String m : s) {
                Log.d(TAG, m);
            }
            Log.d(TAG, LINE_BREAK_END);
        }
    }

    public static void e(String... s) {
        if (BrowserSdk.getInstance().isEnableDebugMode()) {
            Log.e(TAG, LINE_BREAK_START);
            for (String m : s) {
                Log.e(TAG, m);
            }
            Log.e(TAG, LINE_BREAK_END);
        }
    }

    public static void i(String... s) {
        i(false, s);
    }
    public static void info(String... s) {
        i(true, s);
    }
    public static void i(boolean isHideBreakLine,String... s) {
        if (BrowserSdk.getInstance().isEnableDebugMode()) {
            if(!isHideBreakLine) {
                Log.i(TAG, LINE_BREAK_START);
            }
            for (String m : s) {
                Log.i(TAG, m);
            }
            if(!isHideBreakLine) {
                Log.i(TAG, LINE_BREAK_END);
            }
        }
    }
    /**
     * @param currentThread = Thread.currentThread().getStackTrace()
     * @return Getting the name of the currently executing method
     */
    public static String getClassPath(StackTraceElement[] currentThread) {
        try {
            if (currentThread != null && currentThread.length >= 3) {
                if (currentThread[2] != null) {
                    return currentThread[2].toString() + " [Line Number = " + currentThread[2].getLineNumber() + "]";
                }
            }
        } catch (Exception e) {
            return "";
        }
        return "";
    }

    public static String getClassPath(Class<?> classReference, String methodName) {
        if (methodName == null) {
            methodName = "";
        }
        return classReference.getName() + "->" + methodName + "";
    }

    public static final String LINE_BREAK_START = "-----------------------------" + SDK_NAME + "----------------------------->";
    public static final String LINE_BREAK_END = "<-----------------------------" + SDK_NAME + "------------------------------";

    public static String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].toString();
    }

}
