package com.cwtcn.agingtest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cwtcn.agingtest.utils.Constants;
import com.cwtcn.agingtest.utils.Preferences;

/**
 * Created by leizhiheng on 2018/5/8.
 */
public abstract class BaseActivity extends Activity {
    protected String TAG = getClass().getSimpleName();
    protected static final int MSG_UPDATE_BATTERY_STATE = 0x1001;
    protected int testItemId;

    protected TextView tvBatteryState, tvItemTestTime;
    protected Button btnModifyTime;
    protected Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MSG_UPDATE_BATTERY_STATE) {
                tvBatteryState.setText(Preferences.getInstance().getString(TestControlActivity.PRE_KEY_BATTERY_STATE, ""));
                mHandler.sendEmptyMessageDelayed(MSG_UPDATE_BATTERY_STATE, 3000);
            }
            return handleMessages(msg);
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        log("onCreate");

    }

    public abstract boolean handleMessages(Message msg);

    public abstract void setTestItemId();

    public abstract void startTest();

    public abstract void stopTest();

    protected void setResultAndFinish(int resultCode) {
        setResult(resultCode);
        finish();
    }

    public void log(String log) {
        Log.d(TAG, "zhiheng-" + log);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setTestItemId();
        initCommonView();
        startTest();
    }

    private void initCommonView() {
        tvBatteryState = (TextView) findViewById(R.id.battery_state);
        tvItemTestTime = (TextView) findViewById(R.id.text_item_test_time);
        btnModifyTime = (Button) findViewById(R.id.button_modify);

        if (tvBatteryState != null) {
            mHandler.sendEmptyMessage(MSG_UPDATE_BATTERY_STATE);
        }

        if (tvItemTestTime != null) {
            int testTime = Preferences.getInstance().getInt(getClass().getSimpleName(), Constants.getDefaultTestTime(testItemId));
            tvItemTestTime.setText(testTime/1000 + "s");
        }

        if (btnModifyTime != null) {
            btnModifyTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopTest();
                    showModifyTimeDialog();
                }
            });
        }
    }

    private void showModifyTimeDialog() {

        final String preKey = getClass().getSimpleName();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_modify_item_test_time, null);
        final EditText input = (EditText) view.findViewById(R.id.et_input_test_time);
        //设置默认时间
        input.setText(Constants.getDefaultTestTime(testItemId)/1000 + "");
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int setTime;
                try {
                    setTime = Integer.valueOf(input.getText().toString());
                } catch (Exception e) {
                    setTime = 0;
                }
                setTime = setTime * 1000;
                if (setTime == 0) {
                    Toast.makeText(getApplicationContext(), "测试时间必须大于0", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    Preferences.setInt(preKey, setTime);
                    tvItemTestTime.setText(getTestTime() + "s");
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //不管是否修改都重新测试
                startTest();
            }
        });
        builder.create().show();
    }

    /**
     * 获取测试项的测试时间
     * @return
     */
    protected int getTestTime() {
        int time = Preferences.getInt(getClass().getSimpleName(), 0);
        if (time == 0) {
            time = Constants.getDefaultTestTime(testItemId);
        }

        return time/1000;
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        log("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTest();
        mHandler.removeMessages(MSG_UPDATE_BATTERY_STATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        log("onDestory");
    }
}
