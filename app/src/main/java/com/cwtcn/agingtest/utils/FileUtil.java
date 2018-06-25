package com.cwtcn.agingtest.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by leizhiheng on 2018/5/8.
 */

public class FileUtil {

    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
    public static boolean writeToFile(String fileName, String log) {
        File file = new File(Constants.LOG_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
        File fileLog = new File(Constants.LOG_PATH + "/" + fileName);
        try {
            log = format.format(new Date()) + ":" + log;
            FileWriter writer = new FileWriter(fileLog, true);
            writer.write(log + "\n");
            writer.flush();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static long getFileLength(String fileName) {
        File fileLog = new File(Constants.LOG_PATH + "/" + fileName);
        if (!fileLog.exists()) {
            return 0;
        } else {
            return fileLog.length();
        }
    }

    /**
     * 输出测试记录到文件
     * @param log
     * @return
     */
    public static boolean writeTestRecordToFile(String log) {
        return writeToFile(Constants.TEST_RECORD_FILE, log);
    }

    /**
     * 输出异常信息到文件
     * @param log
     * @return
     */
    public static boolean writeExceptionToFile(String log) {
        return writeToFile(Constants.EXCEPTION_FILE, log);
    }

    /**
     * 删除所有的测试记录
     * @return
     */
    public static boolean clearTestRecord(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                clearTestRecord(path + "/" + tempList[i]);//先删除文件夹里面的文件
                clearTestRecord(path + "/" + tempList[i]);//再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    public static File isFileExit(String name) {
        File file = new File(name);
        if (file.exists()) {
            return file;
        }
        return null;
    }
}
