package com.demo.mpos;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sinvoicedemo.R;
import com.libra.sinvoice.Common;
import com.libra.sinvoice.LogHelper;
import com.libra.sinvoice.SinVoicePlayer;
import com.libra.sinvoice.SinVoiceRecognition;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UserReceivePayment extends AppCompatActivity implements
        SinVoiceRecognition.Listener, SinVoicePlayer.Listener {
    private final static String TAG = "UserReceivePayment";

    private final static int MSG_SET_RECG_TEXT = 1;
    private final static int MSG_RECG_START = 2;
    private final static int MSG_RECG_END = 3;
    private final static int MSG_PLAY_TEXT = 4;

    //    private final static int[] TOKENS = null;
//    private final static String TOKENS_str = null;
//    private final static int TOKEN_LEN = 10;
    private final static int[] TOKENS = { 32, 32, 32, 32, 32, 32 };
    private final static String TOKENS_str = "Beeba20141";
    private final static int TOKEN_LEN = TOKENS.length;

    private final static String BAKCUP_LOG_PATH = "/sinvoice_backup";

    private Handler mHanlder;
    private SinVoicePlayer mSinVoicePlayer;
    private SinVoiceRecognition mRecognition;
    private boolean mIsReadFromFile;
    private String mSdcardPath;
    private PowerManager.WakeLock mWakeLock;
    // private TextView mRegState;
    private String mPlayText;
    private char mRecgs[] = new char[100];
    private int mRecgCount;
    private JSONObject merchantData;
    String[] parts;

    static {
        System.loadLibrary("sinvoice");
        LogHelper.d(TAG, "sinvoice jnicall loadlibrary");
    }

    private int partsSent = 0;
    private int MAX_PARTS = 3;
    private static String receivedData = "";
    private TextView mRecognisedTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_receive_payment);

        /*Bundle b = getIntent().getExtras();

        try {
            merchantData = new JSONObject(b.getString("merchant_data"));
        } catch (JSONException e) {
            e.printStackTrace();
            merchantData = new JSONObject();
        }

        CharSequence cs = merchantData.toString();
        Iterable<String> result = Splitter.fixedLength(30).split(cs);
        parts = Iterables.toArray(result, String.class);
        MAX_PARTS = parts.length;*/

        mIsReadFromFile = false;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);

        mSdcardPath = Environment.getExternalStorageDirectory().getPath();

        mSinVoicePlayer = new SinVoicePlayer();
        mSinVoicePlayer.init(this);
        mSinVoicePlayer.setListener(this);

        mRecognition = new SinVoiceRecognition();
        mRecognition.init(this);
        mRecognition.setListener(this);

        // mPlayTextView = (EditText) findViewById(R.id.playtext);
        // mPlayTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
        mRecognisedTextView = (TextView) findViewById(R.id.req_rcvd);
        mHanlder = new UserReceivePayment.RegHandler(this);

        /*Button playStart = (Button) findViewById(R.id.start_play);
        playStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendData("");
            }
        });

        Button playStop = (Button) findViewById(R.id.stop_play);
        playStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mSinVoicePlayer.stop();
            }
        });

        Button recognitionStart = (Button) findViewById(R.id.start_reg);
        recognitionStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mRecognition.start(TOKEN_LEN, mIsReadFromFile);
            }
        });

        Button recognitionStop = (Button) findViewById(R.id.stop_reg);
        recognitionStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mRecognition.stop();
            }
        });*/

        Button recognitionStart = (Button) findViewById(R.id.rcv_pay);
        recognitionStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mRecognition.start(TOKEN_LEN, mIsReadFromFile);
                TextView tv = (TextView) findViewById(R.id.header);
                tv.setVisibility(View.VISIBLE);
            }
        });

        // sendData(parts[partsSent]);
    }

    private void sendData(String data) {
        try {
            String tmp = data;
            byte[] strs = tmp.getBytes("UTF8");
            if ( null != strs ) {
                int len = strs.length;
                int []tokens = new int[len];
                int maxEncoderIndex = mSinVoicePlayer.getMaxEncoderIndex();
                LogHelper.d(TAG, "maxEncoderIndex:" + maxEncoderIndex);
                String encoderText = tmp;
                for ( int i = 0; i < len; ++i ) {
                    if ( maxEncoderIndex < 255 ) {
                        tokens[i] = Common.DEFAULT_CODE_BOOK.indexOf(encoderText.charAt(i));
                    } else {
                        tokens[i] = strs[i];
                    }
                }
                partsSent++;
                mSinVoicePlayer.play(tokens, len, false, 2000);
            } else {
                partsSent++;
                mSinVoicePlayer.play(TOKENS, TOKEN_LEN, false, 2000);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mWakeLock.acquire();
    }

    @Override
    public void onPause() {
        super.onPause();

        mWakeLock.release();

        mSinVoicePlayer.stop();
        mRecognition.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mRecognition.uninit();
        mSinVoicePlayer.uninit();
    }

    private void clearBackup() {
        delete(new File(mSdcardPath + BAKCUP_LOG_PATH));

        Toast.makeText(this, "clear backup log info successful",
                Toast.LENGTH_SHORT).show();
    }

    private static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            file.delete();
        }
    }


    private static class RegHandler extends Handler {
        private StringBuilder mTextBuilder = new StringBuilder();
        private UserReceivePayment mAct;

        public RegHandler(UserReceivePayment act) {
            mAct = act;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_RECG_TEXT:
                    char ch = (char) msg.arg1;
//                mTextBuilder.append(ch);
                    mAct.mRecgs[mAct.mRecgCount++] = ch;
                    break;

                case MSG_RECG_START:
//                mTextBuilder.delete(0, mTextBuilder.length());
                    mAct.mRecgCount = 0;
                    break;

                case MSG_RECG_END:
                    LogHelper.d(TAG, "recognition end gIsError:" + msg.arg1);
                    if ( mAct.mRecgCount > 0 ) {
                        byte[] strs = new byte[mAct.mRecgCount];
                        for ( int i = 0; i < mAct.mRecgCount; ++i ) {
                            strs[i] = (byte)mAct.mRecgs[i];
                        }
                        try {
                            String strReg = new String(strs, "UTF8");
                            if (msg.arg1 >= 0) {
                                Log.d(TAG, "reg ok!!!!!!!!!!!!");
                                if (null != mAct) {
                                    // mAct.mRecognisedTextView.setText(strReg);
                                    receivedData += strReg;
                                    mAct.mRecognisedTextView.setText(receivedData);
                                    // mAct.mRegState.setText("reg ok(" + msg.arg1 + ")");
                                }
                            } else {
                                Log.d(TAG, "reg error!!!!!!!!!!!!!");
                                // mAct.mRecognisedTextView.setText(strReg);
                                // mAct.mRegState.setText("reg err(" + msg.arg1 + ")");
                                // mAct.mRegState.setText("reg err");
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case MSG_PLAY_TEXT:
//                mAct.mPlayTextView.setText(mAct.mPlayText);
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public void onSinVoiceRecognitionStart() {
        mHanlder.sendEmptyMessage(MSG_RECG_START);
    }

    @Override
    public void onSinVoiceRecognition(char ch) {
        mHanlder.sendMessage(mHanlder.obtainMessage(MSG_SET_RECG_TEXT, ch, 0));
    }

    @Override
    public void onSinVoiceRecognitionEnd(int result) {
        mHanlder.sendMessage(mHanlder.obtainMessage(MSG_RECG_END, result, 0));
    }

    @Override
    public void onSinVoicePlayStart() {
        LogHelper.d(TAG, "start play");
    }

    @Override
    public void onSinVoicePlayEnd() {

        LogHelper.d(TAG, "stop play");
        if(partsSent < MAX_PARTS){
            // sendData(parts[partsSent]);
        }
    }

    private static String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        return sdf.format(new Date());
    }

    private static void copyFile(File targetFile, File sourceFile)
            throws IOException {
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        byte[] b = new byte[1024 * 5];
        int len;
        while ((len = inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        outBuff.flush();

        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }

    private static void copyDirectiory(String targetDir, String sourceDir)
            throws IOException {
        (new File(targetDir)).mkdirs();
        File[] file = (new File(sourceDir)).listFiles();
        if (null != file) {
            for (int i = 0; i < file.length; i++) {
                if (file[i].isFile()) {
                    File sourceFile = file[i];
                    File targetFile = new File(
                            new File(targetDir).getAbsolutePath()
                                    + File.separator + file[i].getName());
                    copyFile(targetFile, sourceFile);
                }
                if (file[i].isDirectory()) {
                    String srcPath = sourceDir + "/" + file[i].getName();
                    String targetPath = targetDir + "/" + file[i].getName();
                    copyDirectiory(targetPath, srcPath);
                }
            }
        }
    }

    @Override
    public void onSinToken(int[] tokens) {

    }

}
