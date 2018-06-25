package com.cwtcn.agingtest.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by leizhiheng on 2018/5/8.
 */

public class DateUtil {

    /**
     * 获取时间，用作文件名称后缀
     * @return
     */
    public static String getFilePostTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        return dateFormat.format(new Date());
    }

	/**
     * 获取时间，用作文件名称后缀
     * @return
     */
    public static String getNowTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return dateFormat.format(new Date());
    }

	/**
		 * 获取时间，用作文件名称后缀
		 * @return
		 */
		public static String getNowHour() {
			SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
			return dateFormat.format(new Date());
		}

	
}
