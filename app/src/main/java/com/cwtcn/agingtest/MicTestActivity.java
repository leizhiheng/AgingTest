package com.cwtcn.agingtest;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cwtcn.agingtest.utils.Constants;
import com.cwtcn.agingtest.utils.DateUtil;

import java.io.File;
import java.io.IOException;

/**
 * MIC测试
 * 打开麦克风录音，录音时间20秒，测试结束后删除录音文件。
 */
public class MicTestActivity extends BaseActivity implements View.OnClickListener {

    //每次录音时间
    private static final int MSG_STOP_RECORD = 200;
    private static final int MSG_RECORD_TIME_COUNT = 202;
    private static final int MSG_STOP_PLAY = 201;

    private TextView mTextNotice;

    private Button btnStart;
    private Button btnStop;
    private Button btnPlay;

    private MediaRecorder mMediaRecorder;
    private File recAudioFile;
    private MediaPlayer mMediaPlayer;

    private int recordTime;
    private boolean isRecording;

    @Override
    public boolean handleMessages(Message msg) {
        switch (msg.what) {
            case MSG_STOP_RECORD:
                stopRecorder();
                isRecording = false;
                mHandler.removeMessages(MSG_RECORD_TIME_COUNT);
                btnPlay.performClick();
                btnPlay.setText("正在播放录音");
                break;
            case MSG_STOP_PLAY:
                setResultAndFinish(RESULT_OK);
                break;
            case MSG_RECORD_TIME_COUNT:
                recordTime++;
                btnStop.setText("正在录音：" + recordTime + "s");
                mHandler.sendEmptyMessageDelayed(MSG_RECORD_TIME_COUNT, 1000);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mic_test);

        mTextNotice = (TextView) findViewById(R.id.tv_test_notice);

        btnStart = (Button) findViewById(R.id.record);
        btnStop = (Button) findViewById(R.id.stop);
        btnPlay = (Button) findViewById(R.id.play);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnPlay.setOnClickListener(this);

        btnStop.setVisibility(View.GONE);
        btnPlay.setVisibility(View.GONE);

        //Toast.makeText(this, DateUtil.getFilePostTime(), Toast.LENGTH_LONG).show();
        //recAudioFile = new File(Constants.LOG_PATH, DateUtil.getFilePostTime() + ".amr");
        recAudioFile = new File(Constants.LOG_PATH, "micrecord.amr");
    }

    @Override
    public void setTestItemId() {
        testItemId = Constants.TEST_ITEM_ID_MIC;
    }

    @Override
    public void startTest() {
        mTextNotice.setText(getString(R.string.soundrecord_test_notice, getTestTime()/2+"", getTestTime()/2+""));
        startRecorder();
    }

    @Override
    public void stopTest() {
        if (isRecording) {
            stopRecorder();
        }
        stopPlayer();
        //停止测试的时候删除录音文件
        if (recAudioFile.exists()) {
            recAudioFile.delete();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record:
                startRecorder();
                break;
            case R.id.stop:
                stopRecorder();
                break;
            case R.id.play:
                startPlay();
                break;
            default:
                break;
        }
    }

    private void startRecorder() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mMediaRecorder.setOutputFile(recAudioFile.getAbsolutePath());
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaRecorder.start();
        isRecording = true;
        btnStart.setVisibility(View.GONE);
        btnStop.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.GONE);

        recordTime = 0;
        mHandler.sendEmptyMessageDelayed(MSG_RECORD_TIME_COUNT, 1000);
        mHandler.sendEmptyMessageDelayed(MSG_STOP_RECORD, getTestTime()/2 * 1000);
    }

    private void stopRecorder() {
        if (mMediaRecorder!=null && recAudioFile != null) {
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
        mHandler.removeMessages(MSG_STOP_RECORD);
        mHandler.removeMessages(MSG_RECORD_TIME_COUNT);
        btnPlay.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.GONE);
        btnStop.setVisibility(View.GONE);
    }

    private void startPlay() {
        if (recAudioFile != null && recAudioFile.exists()) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                return;
            }

            Uri uri = Uri.fromFile(recAudioFile);
            mMediaPlayer = MediaPlayer.create(this, uri);
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    btnStart.setVisibility(View.VISIBLE);
                    btnPlay.setVisibility(View.GONE);
                    btnStop.setVisibility(View.GONE);
                    mHandler.sendEmptyMessage(MSG_STOP_PLAY);
                }
            });
        }
    }

    public void stopPlayer() {
        if (null != mMediaPlayer && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mHandler.removeMessages(MSG_STOP_PLAY);
        }
    }
}
