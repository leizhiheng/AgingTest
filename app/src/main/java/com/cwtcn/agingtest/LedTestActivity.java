package com.cwtcn.agingtest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.cwtcn.agingtest.utils.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * LED屏测试：
 * 依次显示红、绿、蓝、白、黑5种颜色，每种颜色显示一秒。循环测试1次，总测试时长5秒。
 */
public class LedTestActivity extends BaseActivity {
    //颜色切换间隔时间
    private static final int CHANGE_COLOR_INTERVAL = 1 * 1000;
    private static final int MSG_CHANGE_COLOR = 0x1;

    private ImageView mImageView;
    private int changeTimes;
    @Override
    public boolean handleMessages(Message msg) {
        if (msg.what == MSG_CHANGE_COLOR) {
            if (changeTimes >= getTestTime()) {
                setResultAndFinish(RESULT_OK);
            } else {
                changeTimes++;
                changeColor(changeTimes%5);
                mHandler.sendEmptyMessageDelayed(MSG_CHANGE_COLOR, CHANGE_COLOR_INTERVAL);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_led_test);
        setFullScreen();

        mImageView = (ImageView) findViewById(R.id.imageview);
        mImageView.setBackgroundColor(Color.WHITE);
    }

    @Override
    public void setTestItemId() {
        testItemId = Constants.TEST_ITEM_ID_LED;
    }

    @Override
    public void startTest() {
        //开始测试
        changeTimes = 0;
        mHandler.sendEmptyMessageDelayed(MSG_CHANGE_COLOR, 0);
    }

    @Override
    public void stopTest() {
        mHandler.removeMessages(MSG_CHANGE_COLOR);
    }

    private void changeColor(int colorIndex) {
        switch (colorIndex) {
            case 0:
                mImageView.setBackgroundColor(Color.RED);
                break;
            case 1:
                mImageView.setBackgroundColor(Color.GREEN);
                break;
            case 2:
                mImageView.setBackgroundColor(Color.BLUE);
                break;
            case 3:
                mImageView.setBackgroundColor(Color.WHITE);
                break;
            case 4:
                mImageView.setBackgroundColor(Color.BLACK);
                break;
        }
    }

    // 显示全屏
    private void setFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    // [代码] 退出全屏函数：
    private void quitFullScreen() {
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
