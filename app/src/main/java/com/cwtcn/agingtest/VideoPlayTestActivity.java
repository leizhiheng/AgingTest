package com.cwtcn.agingtest;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cwtcn.agingtest.utils.Constants;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 喇叭、视频、震动测试
 * 播放视频和音频；每隔10秒震动依次，每次震动20秒；测试时间为60秒
 */
public class VideoPlayTestActivity extends BaseActivity {
    private static final String TAG = VideoPlayTestActivity.class.getSimpleName();
    //震动间隔时长
    private static final int VIBRATE_INTERVAL = AgingApplication.isTestMode ? 5 * 1000 : 10 * 1000;
    //每次震动时长
    private static final int VIBRATE_DURATION = AgingApplication.isTestMode ? 5 * 1000 : 20 * 1000;

    private static final int MSG_STOP_VIBRATE = 0x01;
    private static final int MSG_START_VIBRATE = 0x02;

    private TextView timeTextView;
    private Button stopButton;
    private int testTime;
    private int testTimeSeconds;

    private SurfaceView surfaceView;//能够播放图像的控件
    private SeekBar seekBar;//进度条
    private SurfaceHolder holder;
    private MediaPlayer player;//媒体播放器
    private Timer timer;//定时器
    private TimerTask seekBarTask;//定时器任务
    private int position = 0;

    private Vibrator vibrator;
    private int currentVolume;

    @Override
    public boolean handleMessages(Message msg) {
        switch (msg.what) {
            case MSG_START_VIBRATE:
                vibrator.vibrate(new long[]{500, 1000, 500, 1000}, 0);
                mHandler.sendEmptyMessageDelayed(MSG_STOP_VIBRATE, VIBRATE_DURATION);
                return true;
            case MSG_STOP_VIBRATE:
                vibrator.cancel();
                mHandler.sendEmptyMessageDelayed(MSG_START_VIBRATE, VIBRATE_INTERVAL);
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_test);

        initData();
        initView();
        //设置屏幕亮度到最大值
        //setLight(this, 255);
    }

    private void initData() {
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    private void initView() {
        timeTextView = (TextView) findViewById(R.id.test_time);
        stopButton = (Button) findViewById(R.id.stop_button);

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTest();
            }
        });

        surfaceView = (SurfaceView) findViewById(R.id.sfv);
        seekBar = (SeekBar) findViewById(R.id.sb);

        holder = surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //当进度条停止拖动的时候，把媒体播放器的进度跳转到进度条对应的进度
                if (player != null) {
                    player.seekTo(seekBar.getProgress());
                }
            }
        });

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //为了避免图像控件还没有创建成功，用户就开始播放视频，造成程序异常，所以在创建成功后才使播放按钮可点击
                Log.d(TAG,"surfaceCreated");
                player.setDisplay(holder);//将影像播放控件与媒体播放控件关联起来
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG,"surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                //当程序没有退出，但不在前台运行时，因为surfaceview很耗费空间，所以会自动销毁，
                // 这样就会出现当你再次点击进程序的时候点击播放按钮，声音继续播放，却没有图像
                //为了避免这种不友好的问题，简单的解决方式就是只要surfaceview销毁，我就把媒体播放器等
                //都销毁掉，这样每次进来都会重新播放，当然更好的做法是在这里再记录一下当前的播放位置，
                //每次点击进来的时候把位置赋给媒体播放器，很简单加个全局变量就行了。
                Log.d(TAG,"surfaceDestroyed");
                if (player != null) {
                    stop();
                }
            }
        });
    }

    @Override
    public void setTestItemId() {
        testItemId = Constants.TEST_ITEM_ID_VIDEO;
    }

    @Override
    public void startTest() {
        testTime = getTestTime();
        testTimeSeconds = testTime;
        timeTextView.setText("测试时长：" + caculateRestTime(testTime));
        //将声音音量设置到最大
        setAudio();
        setTimer();
        play();
        //mHandler.sendEmptyMessageDelayed(MSG_START_VIBRATE, VIBRATE_INTERVAL);
    }

    @Override
    public void stopTest() {
        stop();
        vibrator.cancel();
        mHandler.removeMessages(MSG_START_VIBRATE);
        mHandler.removeMessages(MSG_STOP_VIBRATE);
        if (timer!= null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }


    private void setTimer() {
        if (timer == null) {
            timer = new Timer();
        }
        //计算剩余测试时间
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                testTimeSeconds--;
                if (testTimeSeconds == 0) {
                    stop();
                    setResultAndFinish(RESULT_OK);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeTextView.setText("测试时长" + caculateRestTime(testTime) + "\t" + "剩余时间："+caculateRestTime(testTimeSeconds));
                        }
                    });
                }
            }
        }, 0, 1000);
    }

    private String caculateRestTime(int testTimeSeconds) {
        StringBuffer buffer = new StringBuffer();
        int h = testTimeSeconds/3600;
        int m = (testTimeSeconds - h *3600)/60;
        int s = testTimeSeconds - h*3600 - m*60;
        return h+"h:" + m + "m:" + s + "s";
    }

    private void play() {
        if (isPause) {//如果是暂停状态下播放，直接start
            isPause = false;
            player.start();
            return;
        }

        try {
            player = new MediaPlayer();
            AssetFileDescriptor afd = getAssets().openFd("gee_test.mp4");
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.setLooping(true);
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {//视频播放完成后，释放资源
                    stop();
                }
            });

            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //媒体播放器就绪后，设置进度条总长度，开启计时器不断更新进度条，播放视频
                    Log.d(TAG,"onPrepared");
                    seekBar.setMax(player.getDuration());
                    seekBarTask = new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                if (player != null) {
                                    int time = player.getCurrentPosition();
                                    seekBar.setProgress(time);
                                }
                            } catch (Exception e) {
                                Log.d(TAG, "exception:" + e.getMessage());
                            }
                        }
                    };
                    if (timer != null) timer.schedule(seekBarTask,0,500);
                    seekBar.setProgress(position);
                    player.setDisplay(holder);//将影像播放控件与媒体播放控件关联起来
                    player.seekTo(position);
                    player.start();
                }
            });

            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isPause;
    private void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
            isPause = true;
        }
    }

    private void replay() {
        isPause = false;
        if (player != null) {
            stop();
            play();
        }
    }

    private void stop(){
        isPause = false;
        if (player != null) {
            seekBar.setProgress(0);
            player.stop();
            player.release();
            player = null;
        }
    }

    /**
     * 设置当前屏幕亮度
     * @param context
     * @param brightness
     */
    private void setLight(Activity context, int brightness) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        context.getWindow().setAttributes(lp);
    }

    public void setAudio(){
        AudioManager audioManager = (AudioManager)getSystemService(AUDIO_SERVICE);
        //当前音量
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        //最大音量
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
        //Toast.makeText(this, "max = " + max + ", current = " + currentVolume, Toast.LENGTH_LONG).show();
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, max - 2, AudioManager.FLAG_PLAY_SOUND);

        int mxMusicVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mxMusicVolume - 2, AudioManager.FLAG_PLAY_SOUND);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将声音音量设置到最大
//        setAudio(currentVolume);
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }
}
