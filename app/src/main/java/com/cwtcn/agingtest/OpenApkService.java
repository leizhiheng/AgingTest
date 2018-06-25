package com.cwtcn.agingtest;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

import com.cwtcn.agingtest.utils.Constants;
import com.cwtcn.agingtest.utils.FileUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class OpenApkService extends Service {
    private String TAG = "OpenApkService";
    private static HashMap<String, ComponentName> packageMap = new HashMap<String, ComponentName>();

    static {
        packageMap.put("com.cwtcn.kt.contacts", null);
        packageMap.put("com.cwtcn.kt.luncher.robot", null);
        packageMap.put("com.cwtcn.kt.watch.ansgame", null);
        packageMap.put("com.cwtcn.kt.community", null);
        packageMap.put("com.cwtcn.kt.navigation", null);
        packageMap.put("com.cwtcn.kt.luncher.dial", null);
        packageMap.put("com.cwtcn.kt.weather", null);
        packageMap.put("com.cwtcn.kt.sports", null);
        packageMap.put("com.cwtcn.kt.friend", null);
        //packageMap.put("com.android.gallery3d3", null);
        packageMap.put("com.cwtcn.kt.player", null);
        packageMap.put("com.cwtcn.kt.deskclock", null);
        packageMap.put("com.cwtcn.kt.soundrecorder", null);
        packageMap.put("com.cwtcn.kt.watch.stopwatch", null);
        packageMap.put("com.cwtcn.kt.calculator", null);
        packageMap.put("com.cwtcn.kt.settings", null);
        //packageMap.put("com.mediatek.camera", null);
        //packageMap.put("com.android.mms", null);
        packageMap.put("com.cwtcn.kt.travel", null);
        //packageMap.put("com.tencent.qqlite", null);
        packageMap.put("com.cwtcn.kt.luncher", null);
        packageMap.put("com.cwtcn.kt.appstore", null);
        packageMap.put("com.cwtcn.kt.background", null);
        packageMap.put("com.cwtcn.kt.ktwatchguide", null);
        packageMap.put("com.cwtch.kt.location", null);
        packageMap.put("com.cwtcn.kt.watchservice", null);
        packageMap.put("com.cwtcn.kt.watch.push", null);
    }

    private Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        getComponentName(this);
        startAllApk(this);
        Toast.makeText(this, "正在打开所有APP...", Toast.LENGTH_LONG).show();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(OpenApkService.this, TestControlActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(OpenApkService.this, "所有APP打开完毕。", Toast.LENGTH_LONG).show();

                stopSelf();
            }
        }, 4000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void getComponentName(Context context) {
        PackageManager pm = context.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> mApps = pm.queryIntentActivities(mainIntent, 0);

        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (ResolveInfo data : mApps) {
            String pckName = data.activityInfo.packageName;
            String actName = data.activityInfo.name;
            builder.append(pckName + "/" + actName + "\n");
            if (packageMap.containsKey(pckName)) {
                ComponentName componentName = new ComponentName(pckName, actName);
                packageMap.put(pckName, componentName);
                count++;
            }
        }
        File file = FileUtil.isFileExit(Constants.LOG_PATH + "/temp");
        if (file != null) {
            file.delete();
        }
        FileUtil.writeToFile("temp", "Will start" + count + "apps:\n" + builder.toString());
        Log.d(TAG, "Will start" + count + "apps:\n" + builder.toString());
    }

    private void startAllApk(Context context) {
        for (String key : packageMap.keySet()) {
            ComponentName name = packageMap.get(key);
            if (name != null) {
                try {
                    Log.d(TAG, "start package " + name.toString());
                    Intent intent = new Intent();
                    intent.setComponent(name);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    Log.e(TAG, "start package exception:" + e.getMessage());
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
