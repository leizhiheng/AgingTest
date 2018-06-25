package com.cwtcn.agingtest.utils;

import android.os.Environment;

import com.cwtcn.agingtest.AgingApplication;

/**
 * Created by leizhiheng on 2018/5/8.
 */

public class Constants {
    public static final String LOG_PATH = Environment.getExternalStorageDirectory() + "/AgingTest";
    public static final String EXCEPTION_FILE = "exceptions.txt";
    public static final String TEST_RECORD_FILE = "testrecord.txt";

    public static final String NOTICE = "注意：\n1、手动干预测试，被干预的测试项的测试结果都为false!" +
            "\n2、所有测试信息会输出到：" + LOG_PATH +" 目录下；"+ "测试记录在" + TEST_RECORD_FILE + "文件中；" + "异常信息在" + EXCEPTION_FILE +"文件中。" +
            "\n3、清除测试记录会删除AgingTest目录，并删除屏幕上的输出内容。" +
            "\n4、true表示该测试项测试成功，false表示测试失败。";

    public static final String SHARE_REPFERENCES_NAME = "agingtest.preferences";

    //默认总的测试时间4小时
    public static final int DEFAULT_TEST_TIME_TOTAL = 4 * 3600 * 1000;

    public static final int TEST_ITEM_ID_VIDEO = 0;
    public static final int TEST_ITEM_ID_MIC = 1;
    public static final int TEST_ITEM_ID_RINGTONE = 2;
    public static final int TEST_ITEM_ID_CAMERA = 3;
    public static final int TEST_ITEM_ID_LED = 4;
    public static final int TEST_ITEM_ID_WRFILE = 5;

    //默认音频测试时间
    public static final int DEFAULT_TEST_TIME_VIDEO = AgingApplication.isTestMode ? 60*1000 : 60*1000;
    //默认MIC测试时间
    public static final int DEFAULT_TEST_TIME_MIC = AgingApplication.isTestMode ? 60*1000 : 20*1000;
    //默认RINGTONE测试时间
    public static final int DEFAULT_TEST_TIME_RINGTONE = AgingApplication.isTestMode ? 60*1000 : 20*1000;
    //默认Camera测试时间
    public static final int DEFAULT_TEST_TIME_CAMERA = AgingApplication.isTestMode ? 60*1000 : 20*1000;
    //默认LED测试时间
    public static final int DEFAULT_TEST_TIME_LED = AgingApplication.isTestMode ? 60*1000 : 15*1000;
    //默认文件读写测试时间
    public static final int DEFAULT_TEST_TIME_WRFILE = AgingApplication.isTestMode ? 60*1000 : 5*1000;


    /**
     * 获取默认测试轮数 = 总的测试时间/每轮测试时间 + 1
     * @return
     */
    public static int getDefaultTestRounds() {
        return DEFAULT_TEST_TIME_TOTAL / (DEFAULT_TEST_TIME_VIDEO + DEFAULT_TEST_TIME_MIC + DEFAULT_TEST_TIME_RINGTONE
                + DEFAULT_TEST_TIME_CAMERA + DEFAULT_TEST_TIME_LED + DEFAULT_TEST_TIME_WRFILE) + 1;
    }

    public static int getDefaultTestTime(int itemId) {
        int time = 0;
        switch (itemId) {
            case TEST_ITEM_ID_VIDEO:
                time = DEFAULT_TEST_TIME_VIDEO;
                break;
            case TEST_ITEM_ID_MIC:
                time = DEFAULT_TEST_TIME_MIC;
                break;
            case TEST_ITEM_ID_RINGTONE:
                time = DEFAULT_TEST_TIME_RINGTONE;
                break;
            case TEST_ITEM_ID_CAMERA:
                time = DEFAULT_TEST_TIME_CAMERA;
                break;
            case TEST_ITEM_ID_LED:
                time = DEFAULT_TEST_TIME_LED;
                break;
            case TEST_ITEM_ID_WRFILE:
                time = DEFAULT_TEST_TIME_WRFILE;
                break;
        }
        return time;
    }
}
