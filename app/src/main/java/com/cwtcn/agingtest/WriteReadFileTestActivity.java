package com.cwtcn.agingtest;

import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import com.cwtcn.agingtest.utils.Constants;
import com.cwtcn.agingtest.utils.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

/**
 * Created by leizhiheng on 2018/6/13.
 */

public class WriteReadFileTestActivity extends BaseActivity{
    private static final String FILE_NAME = "WriteReadTestFile";
    private static final int MSG_READ_FILE = 0x1;
    private static final int MSG_WRITE_FILE = 0x2;
    private static final int MSG_TEST_FINISH = 0x10;

    private static final int INTERVAL_TIME = 10;

    private TextView textView;

    private int writeTimes, readTimes;
    private long fileLength, readLength;
    private boolean isWriteReadSuccess;
    private StringBuffer stringBuffer;
    @Override
    public boolean handleMessages(Message msg) {
        switch (msg.what) {
            case MSG_WRITE_FILE:
                writeFile();
                mHandler.sendEmptyMessageDelayed(MSG_READ_FILE, INTERVAL_TIME);
                return true;
            case MSG_READ_FILE:
                readFile();
                mHandler.sendEmptyMessageDelayed(MSG_WRITE_FILE, INTERVAL_TIME);
                return true;
            case MSG_TEST_FINISH:
                testFinish();
                return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_read_file);
        textView = (TextView) findViewById(R.id.text);
        stringBuffer = new StringBuffer();
    }

    @Override
    public void setTestItemId() {
        testItemId = Constants.TEST_ITEM_ID_WRFILE;
    }

    @Override
    public void startTest() {
        mHandler.sendEmptyMessage(MSG_WRITE_FILE);
        mHandler.sendEmptyMessageDelayed(MSG_TEST_FINISH, getTestTime() * 1000);
    }

    @Override
    public void stopTest() {
        mHandler.removeMessages(MSG_WRITE_FILE);
        mHandler.removeMessages(MSG_READ_FILE);
        mHandler.removeMessages(MSG_TEST_FINISH);

        deleteTestFile();
    }

    private void deleteTestFile() {
        File fileLog = new File(Constants.LOG_PATH + "/" + FILE_NAME);
        if (fileLog.exists()) {
            fileLog.delete();
        }
    }

    private void writeFile() {
        writeTimes++;
        //stringBuffer.append("write start time:" + (new Date().getTime())+"\n");
        for (int i = 1; i <= 10; i++) {
            FileUtil.writeToFile(FILE_NAME, writeTimes + "-" + i + ":" + CONTENT);
        }
        //stringBuffer.append("write end time:" + (new Date().getTime())+"\n");
        fileLength = FileUtil.getFileLength(FILE_NAME);
        updateText(true);
    }

    private void readFile() {
        readTimes++;
        //stringBuffer.append("read start time:" + (new Date().getTime())+"\n");
        File fileLog = new File(Constants.LOG_PATH + "/" + FILE_NAME);
        try {
            byte []buf = new byte[1024];
            readLength = 0;
            FileInputStream fis = new FileInputStream(fileLog);
            while (true) {
                int i = fis.read(buf);
                if (i > 0) {
                    readLength += i;
                } else {
                    break;
                }
            }
            //stringBuffer.append("read end time:" + (new Date().getTime())+"\n");
            updateText(false);
            //Toast.makeText(this, "fileLength = " + fileLength +", readLength = " + readLength, Toast.LENGTH_LONG).show();
            if (readLength == fileLength) {
                isWriteReadSuccess = true;
            } else {
                isWriteReadSuccess = false;
                mHandler.sendEmptyMessage(MSG_TEST_FINISH);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void testFinish() {
        deleteTestFile();
        if (isWriteReadSuccess) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void updateText(boolean isWrite) {
        if (isWrite) {
            stringBuffer.append("写文件 " + writeTimes + " 次， 文件长度：" + fileLength).append("\n");
        } else {
            stringBuffer.append("第 " + readTimes + " 读文件，读取长度：" + readLength).append("\n");
        }
		textView.setText(stringBuffer.toString());
    }

    private static final String CONTENT = "和羹之美，在于合异。在上海合作组织成员国元首理事会第十八次会议上，习近平主席发表重要讲话强调，尽管文明冲突、文明优越等论调不时沉渣泛起，但文明多样性是人类进步的不竭动力，不同文明交流互鉴是各国人民共同愿望。习近平主席的重要讲话对于上合组织各成员国形成多彩、平等、包容的文明交往规则，形成多元共生、多态共融的文明交流格局，提供了重要启示和路径。\n" +
            "\n" +
            "　　以文明交流超越文明隔阂、以文明互鉴超越文明冲突，以文明共存超越文明优越，树立平等、互鉴、对话、包容的文明观一直是中国所倡导的构建人类命运共同体思想的重要方面。人类文明多样性是世界的基本特征，也是人类进步的源泉。世界上有200多个国家和地区、2500多个民族、多种宗教。不同历史和国情，不同民族和习俗，孕育了不同文明，不同文明求同存异、开放包容，并肩书写相互尊重的壮丽诗篇，携手绘就了共同发展的美好画卷。\n" +
            "\n" +
            "　　促进和而不同、兼收并蓄的文明交流对话，在竞争比较中取长补短，在交流互鉴中共同发展，使文明交流互鉴成为增进各国人民友谊的桥梁、推动人类社会进步的动力、维护世界和平的纽带。习近平主席在布鲁日欧洲学院的演讲中讲过一个生动的例子：“正如中国人喜欢茶而比利时人喜爱啤酒一样，茶的含蓄内敛和酒的热烈奔放代表了品味生命、解读世界的两种不同方式。但是，茶和酒并不是不可兼容的，既可以酒逢知己千杯少，也可以品茶品味品人生。”这个精彩的比喻道出了文明交流的无限可能性——两种甚至多种异质文明既可以和谐相处，又可以在此基础上形成一种新的思维方式或生活方式。";
}
