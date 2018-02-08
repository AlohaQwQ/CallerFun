package com.xixi.finance.callerfun.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.xixi.finance.callerfun.util.LogUtil;
import com.xixi.finance.callerfun.util.RecordUtil;

import java.io.File;

/**
 * Created by AlohaQoQ on 2018/1/26.
 * 后台运行录音Service
 */
public class CallRecordingService extends Service {

    /**
     * Record 监听回调
     */
    public interface OnRecordOverCallBack {
        /**
         * 取消按钮 监听回调
         */
        void onRecordOver(File recordFile);
    }

    private static int RECORD_RATE = 0;
    private static int RECORD_BPP = 32;
    private static int RECORD_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static int RECORD_ENCODER = AudioFormat.ENCODING_PCM_16BIT;
    private Boolean isRecording = false;
    private int bufferEle = 1024, bytesPerEle = 2;// want to play 2048 (2K) since 2 bytes we use only 1024 2 bytes in 16bit format
    private static int[] recordRate = {44100, 22050, 11025, 8000};
    private int bufferSize = 0;
    private File uploadFile;
    private IBinder recordBinder;
    private RecordUtil audioRecorder;

    private static OnRecordOverCallBack onRecordOverCallBack;

    private RecordUtil.OnRecordSaveCallBack onRecordSaveCallBack = new RecordUtil.OnRecordSaveCallBack() {
        @Override
        public void onRecordSaveOver(File recordFile) {
            uploadFile = recordFile;
            if (uploadFile!=null & uploadFile.exists()) {
                getApplicationContext().sendBroadcast(
                        new Intent("Intent.ACTION_MEDIA_SCANNER_SCAN_FILE").
                                setData(Uri.fromFile(uploadFile)));
                LogUtil.biubiubiu("AudioRecorderService:stopRecord() - UploadFile exists");
                onRecordOverCallBack.onRecordOver(uploadFile);
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //maintain the relationship between the caller activity and the callee service, currently useless here
        if (recordBinder == null)
            recordBinder = new RecordBinder();
        return recordBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.biubiubiu("current process ID" + android.os.Process.myPid());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRecording) {
            startRecord();
            //Toast.makeText(getApplicationContext(), "Recording start.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "录音已经开始", Toast.LENGTH_SHORT).show();
        }
               /* Service.START_STICKY_COMPATIBILITY
                Service.START_STICKY
                Service.START_NOT_STICKY
                Service.START_REDELIVER_INTENT*/
        return Service.START_STICKY;
    }

    private void startRecord() {
        LogUtil.biubiubiu("startRecord");
        audioRecorder = RecordUtil.getInstance();
        audioRecorder.createDefaultAudio(getApplicationContext(),onRecordSaveCallBack);
        if (audioRecorder != null) {
            Toast.makeText(getApplicationContext(), "录音开始", Toast.LENGTH_SHORT).show();
            audioRecorder.startRecord(getApplicationContext(),new RecordUtil.RecordStreamListener() {
                @Override
                public void recordOfByte(byte[] data, int begin, int end) {
                    //获取音频做额外拓展
                }
            });
            isRecording = true;
        }
    }

    /**
     * Method to stop and release audio record
     */
    private void stopRecord() {
        LogUtil.biubiubiu("stopRecord");
        if (null != audioRecorder) {
            isRecording = false;
            audioRecorder.stopRecord(getApplicationContext());
            audioRecorder = null;
            Toast.makeText(getApplicationContext(), "录音停止", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        if (isRecording) {
            CallRecordingService.this.stopRecord();
        } else {
            Toast.makeText(getApplicationContext(), "录音已经停止", Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }

    public class RecordBinder extends Binder {

        public CallRecordingService getService() {
            // Return this instance of LocalService so clients can call public methods
            return CallRecordingService.this;
        }

        public void setRecordOverCallBack(OnRecordOverCallBack onRecordOverCallBack){
            CallRecordingService.onRecordOverCallBack = onRecordOverCallBack;
        }

        public void stopCallRecord() {
            if (isRecording) {
                CallRecordingService.this.stopRecord();
            } else {
               // Toast.makeText(getApplicationContext(), "Recording is already stopped", Toast.LENGTH_SHORT).show();
            }
        }
    }
}