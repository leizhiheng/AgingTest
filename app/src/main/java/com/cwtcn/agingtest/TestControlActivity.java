package com.cwtcn.agingtest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cwtcn.agingtest.utils.Constants;
import com.cwtcn.agingtest.utils.FileUtil;
import com.cwtcn.agingtest.utils.DateUtil;
import com.cwtcn.agingtest.utils.Preferences;


import java.io.Serializable;
import java.util.HashMap;

/**
 * 整个测试循环测试15轮，每轮测试包括：
 * 1、喇叭、视频、震动测试：播放音视频，同时每隔10s震动一次，每次震动20s; 测试时长：60s * 1
 * 2、MIC测试：录音10s,播放录音10s; 测试时长：20s * 1
 * 3、LED测试：一次显示红、绿、蓝、白、黑5种颜色，每种颜色显示1s; 测试时长： 5 * 1
 * 4、震动铃声：播放铃声，同时震动； 测试时长：20s * 1
 * 5、摄像头：打开摄像头，进行画面预览；测试时长：60s * 1

 每轮测试时长165秒
 */
public class TestControlActivity extends Activity implements View.OnClickListener {

    public static final String PRE_KEY_BATTERY_STATE = "battery.state";

    private static final int MSG_START_TEST = 0x100;
    private static final int MSG_STOP_TEST = 0x101;
    private static final int MSG_START_ITEM_TEST = 0x102;
    private static final int MSG_STOP_ITEM_TEST = 0x103;
    private static final int MSG_START_NEW_ROUND_TEST = 0x104;
    private static final int MSG_FINISHED_A_ROUND_TEST = 0x105;

    private static final int PERMISSION_REQUEST_CODE = 10;

    //每轮测试的时间间隔，在这个时间间隔里可以停止测试。
    private static final int INTERVAL_TIME_BETWEEN_PER_ROUND = AgingApplication.isTestMode ? 10 : 10;
    //要求：每轮测试时间 * 测试轮数 = 4小时
    private static final int DEFAULT_TEST_ROUNDS = Constants.getDefaultTestRounds();

    //循环测试15次
    private int allTestRounds;
    //当前测试轮数
    private int currentTestRound = 0;

    public static final int MIN_TEST_TIME = 1;
    public static final int MAX_TEST_TIME = 200;
    private EditText editText;
    private TextView textView, roundRecordTextView;
    private Button button, clearButton;

    private StringBuilder resultBuilder;

    private static HashMap<Integer, TestItem> testItems = new HashMap<Integer, TestItem>();

    static {
        int requestCode = 0;
        testItems.put(requestCode, new TestItem(requestCode, VideoPlayTestActivity.class, "Video测试"));
		requestCode++;
        testItems.put(requestCode, new TestItem(requestCode, MicTestActivity.class, "Mic测试"));
		requestCode++;
        testItems.put(requestCode, new TestItem(requestCode, RingtoneTestActivity.class, "Ringtone测试"));
		requestCode++;
        testItems.put(requestCode, new TestItem(requestCode, CameraTestActivity.class, "Camera测试"));
		requestCode++;
        testItems.put(requestCode, new TestItem(requestCode, LedTestActivity.class, "Led测试"));
		requestCode++;
        testItems.put(requestCode, new TestItem(requestCode, WriteReadFileTestActivity.class, "文件读写测试"));
    }

    //最后一个测试项的requestCode.
    private int lastTestItemRequestCode = testItems.size() - 1;

    //是否正在测试
    private boolean isTesting;
    //是否完成了完整测试
    private boolean isTestFinished;
    private boolean isFirstIn;
    private int batteryLevel;
    private float batteryTemperature;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START_TEST:
                    if (isFirstIn) {
                        saveResult("");
                        isFirstIn = false;
                    }
                    startTest();
                    return true;
                case MSG_STOP_TEST:
                    stopTest();
                    return true;
                case MSG_START_ITEM_TEST:
                    int requestCode = msg.arg1;
                    TestItem item = testItems.get(requestCode);
                    startTestItem(requestCode);
                    FileUtil.writeTestRecordToFile("开始 " + item.testName + "--" + Preferences.getString(PRE_KEY_BATTERY_STATE, ""));
                    return true;
                case MSG_STOP_ITEM_TEST:
                    requestCode = msg.arg1;
                    item = testItems.get(requestCode);
                    FileUtil.writeTestRecordToFile(item.testName + "结果：" + (item.result ? "成功" : "失败") + "--" + Preferences.getString(PRE_KEY_BATTERY_STATE, ""));
                    return true;
                case MSG_FINISHED_A_ROUND_TEST:
                    //输出这一轮的测试结果到页面上。
                    printResult();
                    saveResult(resultBuilder.toString());
                    if (currentTestRound < allTestRounds) {
                        //开始新一轮测试。新一轮的测试10钟后开始，在这10钟内可以终止测试。
                        mHandler.sendEmptyMessageDelayed(MSG_START_NEW_ROUND_TEST, INTERVAL_TIME_BETWEEN_PER_ROUND * 1000);
                        button.setEnabled(true);
                        button.setText(INTERVAL_TIME_BETWEEN_PER_ROUND + "秒内" + getString(R.string.button_stop_test));
                    } else {
                        mHandler.sendEmptyMessage(MSG_STOP_TEST);
                    }
                    return true;
                case MSG_START_NEW_ROUND_TEST:
                    currentTestRound++;
                    //新的一轮测试从requestCode为0的测试项开始。
                    sendTestMsg(MSG_START_ITEM_TEST, 0);
                    roundRecordTextView.setText("测试" + allTestRounds + "轮，当前第" + currentTestRound + "轮");
                    FileUtil.writeTestRecordToFile("开始第" + currentTestRound + "轮测试");

                    button.setEnabled(false);
                    button.setText(getString(R.string.button_testing));
                    isTesting = true;
                    return true;
            }
            return false;
        }
    });

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
            batteryLevel = (int)(((float)level / scale) * 100);
            batteryTemperature = (float) (temperature * 1.0 / 10);

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            String strStatus = "未充电";
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    strStatus = "充电中";
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    strStatus = "放电";
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    strStatus = "未充电";
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    strStatus = "充电完成";
                    break;
            }

            Preferences.setString(PRE_KEY_BATTERY_STATE, "battery:" + batteryLevel + "% " + batteryTemperature + "℃" + "-" + strStatus);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        initData();
        initView();
        checkPermission();
        registerReceiver();
    }

    private void initData() {
        isFirstIn = true;
    }

    private void initView() {
        editText = (EditText) findViewById(R.id.test_time);
        textView = (TextView) findViewById(R.id.notice);
        roundRecordTextView = (TextView) findViewById(R.id.test_round_record);
        button = (Button) findViewById(R.id.button);
        clearButton = (Button) findViewById(R.id.button_clear);

        button.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        //设置滚动
        textView.setMovementMethod(new ScrollingMovementMethod());
        //Toast.makeText(this, mPreferences.getString("result", ""), Toast.LENGTH_LONG).show();
        initResultBuilder();
        if (!TextUtils.isEmpty(Preferences.getString("result", ""))) {
            textView.setText(Preferences.getString("result", ""));
        }

        //设置默认测试轮数
        editText.setText(DEFAULT_TEST_ROUNDS + "");
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    private void initResultBuilder() {
        if (resultBuilder == null) resultBuilder = new StringBuilder();
        resultBuilder.delete(0, resultBuilder.length());
        //resultBuilder.append(Constants.NOTICE + "\n\n");
        resultBuilder.append("Test result:\n");
        textView.setText(resultBuilder.toString());
    }


    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length < 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button:
                if (isTesting) {
                    mHandler.removeMessages(MSG_START_NEW_ROUND_TEST);
                    mHandler.sendEmptyMessage(MSG_STOP_TEST);
                } else {
                    if (checkTestRounds()) {
                        mHandler.sendEmptyMessage(MSG_START_TEST);
                    }
                }
                break;
            case R.id.button_clear:
                FileUtil.writeTestRecordToFile("Clicked clear button, isTestFinished is : " + isTestFinished);
                //Toast.makeText(this, isTestFinished ? "Click to recovery factory settings!" : "Click to delete test record!", Toast.LENGTH_LONG).show();
                if (isTestFinished) {
                    recoveryFactorySettings();
                } else {
                    //清除测试记录
                    deleteRecord();
                }
                break;
        }
    }

    private void startTest() {
        editText.setVisibility(View.GONE);
        roundRecordTextView.setVisibility(View.VISIBLE);

        isTestFinished = false;
        clearButton.setText(getString(R.string.clear_test_record));

        FileUtil.writeTestRecordToFile("开始测试====>");
        resultBuilder.append("开始新的测试，测试" + allTestRounds + "轮\n");
        //发送消息，开始新一轮测试
        mHandler.sendEmptyMessage(MSG_START_NEW_ROUND_TEST);
    }

    private void startTestItem(int requestCode) {
        TestItem item = testItems.get(requestCode);
        Intent intent = new Intent(this, item.testActivityClass);
        startActivityForResult(intent, item.requestCode);
    }

    private void stopTest() {
        editText.setVisibility(View.VISIBLE);
        roundRecordTextView.setVisibility(View.GONE);

        isTesting = false;
        button.setEnabled(true);
        button.setText(getString(R.string.button_start_test));

        if (currentTestRound == allTestRounds) {
            isTestFinished = true;
            clearButton.setText(getString(R.string.recovery_factory_settings));
            showRecoveryDialog();
        }

        currentTestRound = 0;
        FileUtil.writeTestRecordToFile("结束测试<=====");
        Toast.makeText(this, "测试已结束！", Toast.LENGTH_LONG).show();
    }

    private void showRecoveryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.recovery_factory_settings));
        builder.setMessage("点击确定恢复出厂设置。\n点击取消查看测试结果。");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recoveryFactorySettings();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void recoveryFactorySettings() {
        FileUtil.writeTestRecordToFile("===>recoveryFactorySettings");
        //Intent intent = new Intent();
        //ComponentName componentName = new ComponentName("com.android.settings",
        //        "com.android.settings.MasterClearActivity");
        //intent.setComponent(componentName);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);
        //finish();
        doMasterClear();
    }


    private void doMasterClear() {
//        Intent intent = new Intent(Intent.ACTION_MASTER_CLEAR);
//        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
//        intent.putExtra(Intent.EXTRA_REASON, "MasterClearConfirm");
//        intent.putExtra(Intent.EXTRA_WIPE_EXTERNAL_STORAGE, true);
//        sendBroadcast(intent);
        // Intent handling is asynchronous -- assume it will happen soon.
    }

    private void deleteRecord() {
        FileUtil.writeTestRecordToFile("===>deleteRecord");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("删除测试记录");
        builder.setMessage("确认要删除测试记录吗？");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FileUtil.clearTestRecord(Constants.LOG_PATH);
                FileUtil.writeTestRecordToFile("===>deleteRecord finished");
                Preferences.clear();
                initResultBuilder();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FileUtil.writeTestRecordToFile("===>deleteRecord canceled");
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        TestItem item = testItems.get(requestCode);
        if (resultCode == RESULT_OK) {
            item.result = true;
        } else {
            item.result = false;
        }

        //发送测试项测试结束消息
        sendTestMsg(MSG_STOP_ITEM_TEST, requestCode);

        //判断是不是最后一个测试项。
        if (requestCode < lastTestItemRequestCode) {
            requestCode++;
            //发送开始新的测试项测试的消息
            sendTestMsg(MSG_START_ITEM_TEST, requestCode);
        } else {
            mHandler.sendEmptyMessage(MSG_FINISHED_A_ROUND_TEST);
        }
    }

    private void sendTestMsg(int msgCode, int requestCode) {
        Message message = mHandler.obtainMessage();
        message.what = msgCode;
        message.arg1 = requestCode;
        mHandler.sendMessage(message);
    }


    private void printResult() {
        resultBuilder.append(currentTestRound + ":");
        for (int i = 0; i <= lastTestItemRequestCode; i++) {
            TestItem item = testItems.get(i);
            resultBuilder.append(item.result + " ");
            item.result = false;
        }
        resultBuilder.append(" -battery:" + batteryLevel + "  " + DateUtil.getNowHour());
        resultBuilder.append("\n");
        textView.setText(resultBuilder.toString());

    }

    private void saveResult(String msg) {
        Preferences.setString("result", msg);
    }

    private boolean checkTestRounds() {
        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
            Toast.makeText(TestControlActivity.this, "请输入测试轮数！", Toast.LENGTH_LONG).show();
            return false;
        }
        int rounds = getInputRounds(editText.getText().toString());
        if (rounds < MIN_TEST_TIME) {
            Toast.makeText(this, "最少测试轮数为：" + MIN_TEST_TIME + "轮", Toast.LENGTH_LONG).show();
            return false;
        } else if (rounds > MAX_TEST_TIME) {
            Toast.makeText(this, "最多测试轮数为：" + MAX_TEST_TIME + "轮", Toast.LENGTH_LONG).show();
            return false;
        } else {
            allTestRounds = rounds;
            return true;
        }
    }

    private int getInputRounds(String tString) {

        int time = 0;
        try {
            time = Integer.valueOf(tString);
        } catch (Exception e) {
            time = 0;
        }
        return time;
    }

    static class TestItem implements Serializable {
        int requestCode;
        Class testActivityClass;
        boolean result;
        String testName;

        public TestItem(int requestCode, Class testActivityClass, String testName) {
            this.requestCode = requestCode;
            this.testActivityClass = testActivityClass;
            this.testName = testName;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}

