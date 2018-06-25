package com.cwtcn.agingtest;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.widget.TextView;

import com.cwtcn.agingtest.utils.Constants;

import java.lang.reflect.Field;

/**
 * 铃声测试
 * 播放铃声，同时震动，测试时间为20秒
 */
public class RingtoneTestActivity extends BaseActivity {

    //每次测试时长
    private static final int MSG_STOP_PLAY_RINGTONE = 0x1;

    private TextView tvNotice;
    private static Ringtone ringtone;
    private Vibrator vibrator;

    @Override
    public boolean handleMessages(Message msg) {
        if (msg.what == MSG_STOP_PLAY_RINGTONE) {
            stopRingtone();
            vibrator.cancel();
            setResultAndFinish(RESULT_OK);
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone_test);

        tvNotice = (TextView) findViewById(R.id.tv_test_notice);
        // Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//短信提醒铃声
        //改成如下默认手机闹铃铃声更妥，或者raw放置小音频文件转换成uri (么实验过)
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);


    }

    @Override
    public void setTestItemId() {
        testItemId = Constants.TEST_ITEM_ID_RINGTONE;
    }

    @Override
    public void startTest() {
        tvNotice.setText(R.string.ringtone_test_notice);
        //播放铃声
        playRingtone();
        mHandler.sendEmptyMessageDelayed(MSG_STOP_PLAY_RINGTONE, getTestTime() * 1000);
    }

    @Override
    public void stopTest() {
        stopRingtone();
        vibrator.cancel();
        mHandler.removeMessages(MSG_STOP_PLAY_RINGTONE);
    }

    //反射设置闹铃重复播放
    private void setRingtoneRepeat(Ringtone ringtone) {
        Class<Ringtone> clazz =Ringtone.class;
        try {
            Field field = clazz.getDeclaredField("mLocalPlayer");//返回一个 Field 对象，它反映此 Class 对象所表示的类或接口的指定公共成员字段（※这里要进源码查看属性字段）
            field.setAccessible(true);
            MediaPlayer target = (MediaPlayer) field.get(ringtone);//返回指定对象上此 Field 表示的字段的值
            target.setLooping(true);//设置循环
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    //播放铃声
    public void playRingtone() {
        ringtone.setStreamType(AudioManager.STREAM_RING);//因为rt.stop()使得MediaPlayer置null,所以要重新创建（具体看源码）
        setRingtoneRepeat(ringtone);//设置重复提醒
        ringtone.play();

        vibrator.vibrate(new long[]{500, 1000, 500, 1000}, 0);
    }


    //停止铃声
    public void stopRingtone() {
        ringtone.stop();
    }
}
