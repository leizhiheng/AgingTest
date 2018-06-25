package com.cwtcn.agingtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * 整个测试循环测试15轮，每轮测试包括：
 * 1、喇叭、视频、震动测试：播放音视频，同时每隔10s震动一次，每次震动20s; 测试时长：480s * 1
 * 2、MIC测试：录音10s,播放录音10s; 测试时长：20s * 1
 * 3、LED测试：一次显示红、绿、蓝、白、黑5种颜色，每种颜色显示1s; 测试时长： 5 * 10
 * 4、震动铃声：播放铃声，同时震动； 测试时长：20s * 1
 * 5、摄像头：打开摄像头，进行画面预览；测试时长：60s * 1
 */
public class AgingTestMainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aging_test_main);
//        findViewById(R.id.open_apps).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
                //Intent intent = new Intent(AgingTestMainActivity.this, OpenApkService.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startService(intent);
                Intent intent = new Intent(this, TestControlActivity.class);
                startActivity(intent);
                finish();
//            }
//        });
    }
//    private static final int MSG_START_TEST = 0x100;
//    private static final int MSG_STOP_TEST = 0x101;
//    private static final int MSG_START_ITEM_TEST = 0x102;
//    private static final int MSG_STOP_ITEM_TEST = 0x103;
//    private static final int MSG_START_NEW_ROUND_TEST = 0x104;
//    private static final int MSG_FINISHED_A_ROUND_TEST = 0x105;
//
//    private static final int PERMISSION_REQUEST_CODE = 10;
//
//    private static final int REQUEST_CODE_VIDEO_TEST = 0;
//    private static final int REQUEST_CODE_MIC_TEST = REQUEST_CODE_VIDEO_TEST + 1;
//    private static final int REQUEST_CODE_LED_TEST = REQUEST_CODE_MIC_TEST + 1;
//    private static final int REQUEST_CODE_RINGTONE_TEST = REQUEST_CODE_LED_TEST + 1;
//    private static final int REQUEST_CODE_CAMERA_TEST = REQUEST_CODE_RINGTONE_TEST + 1;
//
//    //每轮测试的时间间隔，在这个时间间隔里可以停止测试。
//    private static final int INTERVAL_TIME_BETWEEN_PER_ROUND = AgingApplication.isTestMode ? 5 : 10;
//
//    //循环测试15次
//    private int allTestRounds;
//    //当前测试轮数
//    private int currentTestRound = 0;
//
//    public static final int MIN_TEST_TIME = 1;
//    public static final int MAX_TEST_TIME = 20;
//    private EditText editText;
//    private TextView textView, roundRecordTextView;
//    private Button button, clearButton;
//
//    private StringBuilder resultBuilder;
//
//    private static HashMap<Integer, TestItem> testItems = new HashMap<Integer, TestItem>();
//
//    static {
//        testItems.put(REQUEST_CODE_VIDEO_TEST, new TestItem(REQUEST_CODE_VIDEO_TEST, VideoPlayTestActivity.class, "Video测试"));
//        testItems.put(REQUEST_CODE_MIC_TEST, new TestItem(REQUEST_CODE_MIC_TEST, MicTestActivity.class, "Mic测试"));
//        testItems.put(REQUEST_CODE_LED_TEST, new TestItem(REQUEST_CODE_LED_TEST, LedTestActivity.class, "Led测试"));
//        testItems.put(REQUEST_CODE_RINGTONE_TEST, new TestItem(REQUEST_CODE_RINGTONE_TEST, RingtoneTestActivity.class, "Ringtone测试"));
//        testItems.put(REQUEST_CODE_CAMERA_TEST, new TestItem(REQUEST_CODE_CAMERA_TEST, CameraTestActivity.class, "Camera测试"));
//    }
//
//    //最后一个测试项的requestCode.
//    private int lastTestItemRequestCode = REQUEST_CODE_CAMERA_TEST;
//
//    //是否正在测试
//    private boolean isTesting;
//    //是否完成了完整测试
//    private boolean isTestFinished;
//    private boolean isOpenedApps;
//
//    private Handler mHandler = new Handler(new Handler.Callback() {
//        @Override
//        public boolean handleMessage(Message msg) {
//            switch (msg.what) {
//                case MSG_START_TEST:
//                    startTest();
//                    return true;
//                case MSG_STOP_TEST:
//                    stopTest();
//                    return true;
//                case MSG_START_ITEM_TEST:
//                    int requestCode = msg.arg1;
//                    TestItem item = testItems.get(requestCode);
//                    startTestItem(requestCode);
//                    FileUtil.writeTestRecordToFile("开始 " + item.testName);
//                    return true;
//                case MSG_STOP_ITEM_TEST:
//                    requestCode = msg.arg1;
//                    item = testItems.get(requestCode);
//                    FileUtil.writeTestRecordToFile(item.testName + "结果：" + (item.result ? "成功" : "失败"));
//                    return true;
//                case MSG_FINISHED_A_ROUND_TEST:
//                    //输出这一轮的测试结果到页面上。
//                    printResult();
//                    if (currentTestRound < allTestRounds) {
//                        //开始新一轮测试。新一轮的测试10钟后开始，在这10钟内可以终止测试。
//                        mHandler.sendEmptyMessageDelayed(MSG_START_NEW_ROUND_TEST, INTERVAL_TIME_BETWEEN_PER_ROUND * 1000);
//                        button.setEnabled(true);
//                        button.setText(INTERVAL_TIME_BETWEEN_PER_ROUND + "秒内" + getString(R.string.button_stop_test));
//                    } else {
//                        mHandler.sendEmptyMessage(MSG_STOP_TEST);
//                    }
//                    return true;
//                case MSG_START_NEW_ROUND_TEST:
//                    currentTestRound++;
//                    //新的一轮测试从requestCode为0的测试项开始。
//                    sendTestMsg(MSG_START_ITEM_TEST, REQUEST_CODE_VIDEO_TEST);
//                    roundRecordTextView.setText("测试" + allTestRounds + "轮，当前第" + currentTestRound + "轮");
//                    FileUtil.writeTestRecordToFile("开始第" + currentTestRound + "轮测试");
//
//                    button.setEnabled(false);
//                    button.setText(getString(R.string.button_testing));
//                    isTesting = true;
//                    return true;
//            }
//            return false;
//        }
//    });
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        setContentView(R.layout.activity_main);
//
//        initView();
//        checkPermission();
//        if (savedInstanceState != null) {
//            isOpenedApps = savedInstanceState.getBoolean("isOpenedApps", false);
//            Toast.makeText(this, "isOpenedApps = " + isOpenedApps, Toast.LENGTH_LONG).show();
//
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (isOpenedApps) button.performClick();
//    }
//
//    private void initView() {
//        editText = (EditText) findViewById(R.id.test_time);
//        textView = (TextView) findViewById(R.id.notice);
//        roundRecordTextView = (TextView) findViewById(R.id.test_round_record);
//        button = (Button) findViewById(R.id.button);
//        clearButton = (Button) findViewById(R.id.button_clear);
//
//        button.setOnClickListener(this);
//        clearButton.setOnClickListener(this);
//
//        //设置滚动
//        textView.setMovementMethod(new ScrollingMovementMethod());
//        initResultBuilder();
//
//        //默认设置为测试14轮
//        editText.setText("14");
//    }
//
//    private void initResultBuilder() {
//        if (resultBuilder == null) resultBuilder = new StringBuilder();
//        resultBuilder.delete(0, resultBuilder.length());
//        //resultBuilder.append(Constants.NOTICE + "\n\n");
//        resultBuilder.append("Test result:\n");
//        textView.setText(resultBuilder.toString());
//    }
//
//    private void checkPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length < 1 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    finish();
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.button:
//                if (!isOpenedApps) {
//                    openApps();
//                    return;
//                }
//                if (isTesting) {
//                    mHandler.removeMessages(MSG_START_NEW_ROUND_TEST);
//                    mHandler.sendEmptyMessage(MSG_STOP_TEST);
//                } else {
//                    if (checkTestRounds()) {
//                        mHandler.sendEmptyMessage(MSG_START_TEST);
//                    }
//                }
//                break;
//            case R.id.button_clear:
//                if (isTestFinished) {
//                    recoveryFactorySettings();
//                } else {
//                    //清除测试记录
//                    deleteRecord();
//                }
//                break;
//        }
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean("isOpenedApps", isOpenedApps);
//    }
//
//    private void openApps() {
//        StartAppAsyncTask task = new StartAppAsyncTask(getApplicationContext());
//        task.setOnStartAppsListener(new StartAppAsyncTask.OnStartAppsListener() {
//            @Override
//            public void onStartAppFinish() {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Intent intent = new Intent(AgingTestMainActivity.this, AgingTestMainActivity.class);
//                startActivity(intent);
//                Toast.makeText(AgingTestMainActivity.this, "再次打开Aging main Activity", Toast.LENGTH_LONG).show();
//            }
//        });
//        task.execute();
//        isOpenedApps = true;
//    }
//
//    private void startTest() {
//        editText.setVisibility(View.GONE);
//        roundRecordTextView.setVisibility(View.VISIBLE);
//
//        isTestFinished = false;
//        clearButton.setText(getString(R.string.clear_test_record));
//
//        FileUtil.writeTestRecordToFile("开始测试====>");
//        resultBuilder.append("开始新的测试，测试" + allTestRounds + "轮\n");
//        //发送消息，开始新一轮测试
//        mHandler.sendEmptyMessage(MSG_START_NEW_ROUND_TEST);
//    }
//
//    private void startTestItem(int requestCode) {
//        TestItem item = testItems.get(requestCode);
//        Intent intent = new Intent(this, item.testActivityClass);
//        startActivityForResult(intent, item.requestCode);
//    }
//
//    private void stopTest() {
//        editText.setVisibility(View.VISIBLE);
//        roundRecordTextView.setVisibility(View.GONE);
//
//        isTesting = false;
//        button.setEnabled(true);
//        button.setText(getString(R.string.button_start_test));
//
//        if (currentTestRound == allTestRounds) {
//            isTestFinished = true;
//            clearButton.setText(getString(R.string.recovery_factory_settings));
//            showRecoveryDialog();
//        }
//
//        currentTestRound = 0;
//        FileUtil.writeTestRecordToFile("结束测试<=====");
//        Toast.makeText(this, "测试已结束！", Toast.LENGTH_LONG).show();
//    }
//
//    private void showRecoveryDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(getString(R.string.recovery_factory_settings));
//        builder.setMessage("点击确定恢复出厂设置。\n点击取消查看测试结果。");
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                recoveryFactorySettings();
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        builder.create().show();
//    }
//
//    private void recoveryFactorySettings() {
//        Intent intent = new Intent();
//        ComponentName componentName = new ComponentName("com.android.settings",
//                "com.android.settings.MasterClearActivity");
//        intent.setComponent(componentName);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        //finish();
//    }
//
//    private void deleteRecord() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("删除测试记录");
//        builder.setMessage("确认要删除测试记录吗？");
//        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                FileUtil.clearTestRecord(Constants.LOG_PATH);
//                initResultBuilder();
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        builder.create().show();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        TestItem item = testItems.get(requestCode);
//        if (resultCode == RESULT_OK) {
//            item.result = true;
//        } else {
//            item.result = false;
//        }
//
//        //发送测试项测试结束消息
//        sendTestMsg(MSG_STOP_ITEM_TEST, requestCode);
//
//        //判断是不是最后一个测试项。
//        if (requestCode < lastTestItemRequestCode) {
//            requestCode++;
//            //发送开始新的测试项测试的消息
//            sendTestMsg(MSG_START_ITEM_TEST, requestCode);
//        } else {
//            mHandler.sendEmptyMessage(MSG_FINISHED_A_ROUND_TEST);
//        }
//    }
//
//    private void sendTestMsg(int msgCode, int requestCode) {
//        Message message = mHandler.obtainMessage();
//        message.what = msgCode;
//        message.arg1 = requestCode;
//        mHandler.sendMessage(message);
//    }
//
//    private void printResult() {
//        resultBuilder.append("第" + currentTestRound + "测试： ");
//        for (int i = 0; i <= lastTestItemRequestCode; i++) {
//            TestItem item = testItems.get(i);
//            resultBuilder.append(item.result + "  ");
//            item.result = false;
//        }
//        resultBuilder.append("\n");
//        textView.setText(resultBuilder.toString());
//    }
//
//    private boolean checkTestRounds() {
//        if (TextUtils.isEmpty(editText.getText().toString().trim())) {
//            Toast.makeText(AgingTestMainActivity.this, "请输入测试轮数！", Toast.LENGTH_LONG).show();
//            return false;
//        }
//        int rounds = getInputRounds(editText.getText().toString());
//        if (rounds < MIN_TEST_TIME) {
//            Toast.makeText(this, "最少测试轮数为：" + MIN_TEST_TIME + "轮", Toast.LENGTH_LONG).show();
//            return false;
//        } else if (rounds > MAX_TEST_TIME) {
//            Toast.makeText(this, "最多测试轮数为：" + MAX_TEST_TIME + "轮", Toast.LENGTH_LONG).show();
//            return false;
//        } else {
//            allTestRounds = rounds;
//            return true;
//        }
//    }
//
//    private int getInputRounds(String tString) {
//
//        int time = 0;
//        try {
//            time = Integer.valueOf(tString);
//        } catch (Exception e) {
//            time = 0;
//        }
//        return time;
//    }
//
//    static class TestItem implements Serializable {
//        int requestCode;
//        Class testActivityClass;
//        boolean result;
//        String testName;
//
//        public TestItem(int requestCode, Class testActivityClass, String testName) {
//            this.requestCode = requestCode;
//            this.testActivityClass = testActivityClass;
//            this.testName = testName;
//        }
//    }
}
