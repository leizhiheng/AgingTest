package com.cwtcn.agingtest;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.cwtcn.agingtest.utils.FileUtil;

/**
 * Created by leizhiheng on 2018/5/8.
 */
public class AgingApplication extends Application implements Thread.UncaughtExceptionHandler{
    public static boolean isTestMode = false;
    public static Context sContext;
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
        sContext = getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e("TAG","currentThread:"+Thread.currentThread()+"---thread:"+t.getId()+"---ex:"+e.toString());
        String log = "Exception thread id:" + t.getId() + "\n" + "Exception:" + e.toString()
                + "\n Exception message:" + e.getMessage()
                + "\n stackTrack:" + getStackTrace(e.getStackTrace());
        FileUtil.writeExceptionToFile(log);
    }

    private String getStackTrace(StackTraceElement[] traces) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement traceElement : traces)
            builder.append("\tat " + traceElement + "\n");
        return builder.toString();
    }
}
