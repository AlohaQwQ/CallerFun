package com.xixi.finance.callerfun.util;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.xixi.finance.callerfun.version.PersistentDataCacheEntity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlohaQoQ on 2018/1/26.
 */
public class RecordUtil {

    private static RecordUtil audioRecorder;
    //音频输入-麦克风
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采用频率
    //44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    //采样频率一般共分为22.05KHz、44.1KHz、48KHz三个等级  //16000
    private final static int AUDIO_SAMPLE_RATE = 16000;
    //声道 单声道
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    //编码
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    private int bufferSizeInBytes = 0;
    //录音对象
    private AudioRecord audioRecord;
    //录音状态
    private Status status = Status.STATUS_NO_READY;
    //文件名
   // private String tempFileName = "record";
    //录音文件
    private List<String> filesName = new ArrayList<>();

    /**
     * Call Phone
     */
    private String callPhone;
    /**
     * Call Customer Name
     */
    private String callCustomerName;

    /**
     * 呼叫状态，1-呼出, 2-呼入
     */
    private int callState = 0;

    private static OnRecordSaveCallBack onRecordSaveCallBack;

    /**
     * Created by HXL on 16/8/11.
     * 获取录音的音频流,用于拓展的处理
     */
    public interface RecordStreamListener {
        void recordOfByte(byte[] data, int begin, int end);
    }

    /**
     * Record 监听回调
     */
    public interface OnRecordSaveCallBack {
        /**
         * 取消按钮 监听回调
         */
        void onRecordSaveOver(File recordFile);
    }

    private RecordUtil() {}

    //单例模式
    public static RecordUtil getInstance() {
        if (audioRecorder == null) {
            audioRecorder = new RecordUtil();
        }
        return audioRecorder;
    }

    /**
     * 录音缓存文件
     *
     * @return
     */
    private File createTempFile(final Context context) {
        LogUtil.biubiubiu("createTempFile");
        File tempFile = new File(AudioFileUtils.getRecordDir(context), "record.raw");
        return tempFile;
    }

    //Create file to convert to .wav format
    private File createWavFile(final Context context) {
        File wavFile = new File(AudioFileUtils.getRecordDir(context),
                "record_" + System.currentTimeMillis() + callCustomerName + "_"+callPhone +"_" + String.valueOf(callState) +".wav");
        return wavFile;
    }

    /**
     * 创建录音对象
     */
    public void createAudio(String fileName, int audioSource, int sampleRateInHz,
                            int channelConfig, int audioFormat,OnRecordSaveCallBack onRecordSaveCallBack) {
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        //this.tempFileName = fileName;
    }

    /**
     * 创建默认的录音对象
     *
     */
    public void createDefaultAudio(Context context,OnRecordSaveCallBack onRecordSaveCallBack) {
        //mContext = ctx;
        //mHandler = handler;
        // 获得缓冲区字节大小
        bufferSizeInBytes = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                AUDIO_CHANNEL, AUDIO_ENCODING);
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, bufferSizeInBytes);
        //this.tempFileName = createTempFile(context);
        status = Status.STATUS_READY;
        this.onRecordSaveCallBack = onRecordSaveCallBack;
    }

    /**
     * 开始录音
     *
     * @param listener 音频流的监听
     */
    public void startRecord(final Context context, final RecordStreamListener listener) {
        if (status == Status.STATUS_NO_READY) {
            //Toast.makeText(context,"录音尚未初始化,请检查是否禁止了录音权限",Toast.LENGTH_SHORT).show();
            throw new IllegalStateException("录音尚未初始化,请检查是否禁止了录音权限~");
        }
        if (status == Status.STATUS_START) {
            throw new IllegalStateException("正在录音");
        }
        LogUtil.biubiubiu("AudioRecorder:"+"===startRecord===" + audioRecord.getState());
        audioRecord.startRecording();

        new Thread(new Runnable() {
            @Override
            public void run() {
                writeDataTOFile(context,listener);
            }
        }).start();
    }

    /**
     * 将音频信息写入文件
     *
     * @param listener 音频流的监听
     */
    private void writeDataTOFile(final Context context,RecordStreamListener listener) {
        // new一个byte数组用来存一些字节数据，大小为缓冲区大小
        byte[] audiodata = new byte[bufferSizeInBytes];

        FileOutputStream fos = null;
        int readsize = 0;
        try {
            /*String currentFileName = tempFileName;
            if (status == Status.STATUS_PAUSE) {
                //假如是暂停录音 将文件名后面加个数字,防止重名文件内容被覆盖
                currentFileName += filesName.size();
            }*/
            filesName.add(AudioFileUtils.getPcmFileAbsolutePath(context));
            File file = new File(AudioFileUtils.getPcmFileAbsolutePath(context));
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);// 建立一个可存取字节的文件
        } catch (IllegalStateException e) {
            LogUtil.biubiubiu("AudioRecorder:"+e.getMessage());
            throw new IllegalStateException(e.getMessage());
        } catch (FileNotFoundException e) {
            LogUtil.biubiubiu("AudioRecorder:"+e.getMessage());
        }
        //将录音状态设置成正在录音状态
        status = Status.STATUS_START;
        while (status == Status.STATUS_START) {
            readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);
            if (AudioRecord.ERROR_INVALID_OPERATION != readsize && fos != null) {
                try {
                    fos.write(audiodata);
                    if (listener != null) {
                        //用于拓展业务
                        listener.recordOfByte(audiodata, 0, audiodata.length);
                    }
                } catch (IOException e) {
                    LogUtil.biubiubiu("AudioRecorder:"+e.getMessage());
                }
            }
        }
        try {
            if (fos != null) {
                fos.close();// 关闭写入流
            }
        } catch (IOException e) {
            LogUtil.biubiubiu("AudioRecorder:"+e.getMessage());
        }
    }

    /**
     * 暂停录音
     */
    public void pauseRecord() {
        LogUtil.biubiubiu("AudioRecorder:"+"===pauseRecord===");
        if (status != Status.STATUS_START) {
            throw new IllegalStateException("没有在录音");
        } else {
            audioRecord.stop();
            status = Status.STATUS_PAUSE;
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord(Context context) {
        LogUtil.biubiubiu("AudioRecorder:"+"===stopRecord===");
        saveCustomerInformation();
        if (status == Status.STATUS_NO_READY || status == Status.STATUS_READY) {
            throw new IllegalStateException("录音尚未开始");
        } else {
            audioRecord.stop();
            status = Status.STATUS_STOP;
            release(context);
        }
    }

    /**
     * 释放资源
     */
    public void release(Context context) {
        LogUtil.biubiubiu("AudioRecorder:"+"===release===");
        //假如有暂停录音
        try {
            if (filesName.size() > 0) {
                List<String> filePaths = new ArrayList<>();
                for (String fileName : filesName) {
                    filePaths.add(AudioFileUtils.getPcmFileAbsolutePath(context));
                }
                //清除
                filesName.clear();
                //将多个pcm文件转化为wav文件
                //mergePCMFilesToWAVFile(filePaths,context);
                makePCMFileToWAVFile(context);
            } else {
                //这里由于只要录音过filesName.size都会大于0,没录音时fileName为null
                //会报空指针 NullPointerException
                // 将单个pcm文件转化为wav文件
                //LogUtil.biubiubiu("AudioRecorder:"+"=====makePCMFileToWAVFile======");
                //makePCMFileToWAVFile();
            }
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        }

        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        status = Status.STATUS_NO_READY;
    }

    /**
     * 取消录音
     */
    public void canel() {
        filesName.clear();
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
        status = Status.STATUS_NO_READY;
    }

    /**
     * 将pcm合并成wav
     *
     * @param filePaths
     */
    private void mergePCMFilesToWAVFile(final List<String> filePaths, final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (PcmToWavUtil.mergePCMFilesToWAVFile(filePaths,
                        AudioFileUtils.getWavFileAbsolutePath(context,callCustomerName,callPhone,String.valueOf(callState)))) {
                    //操作成功
                    LogUtil.biubiubiu("录音合成成功");
                    //wavToM4a();
                } else {
                    //操作失败
                    LogUtil.biubiubiu("AudioRecorder:"+"mergePCMFilesToWAVFile fail");
                    throw new IllegalStateException("mergePCMFilesToWAVFile fail");
                }
            }
        }).start();
    }

    /**
     * 将单个pcm文件转化为wav文件
     */
    private void makePCMFileToWAVFile(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (PcmToWavUtil.makePCMFileToWAVFile(
                        AudioFileUtils.getPcmFileAbsolutePath(context),
                        AudioFileUtils.getWavFileAbsolutePath(context,callCustomerName,callPhone,String.valueOf(callState)), true)) {
                    AudioFileUtils.deletePcmFileAbsolutePath(context);
                    onRecordSaveCallBack.onRecordSaveOver(getRecordFile(context));
                    //操作成功
                } else {
                    //操作失败
                    LogUtil.biubiubiu("AudioRecorder:"+"makePCMFileToWAVFile fail");
                    throw new IllegalStateException("makePCMFileToWAVFile fail");
                }
            }
        }).start();
    }

    /**
     * 获取录音对象的状态
     *
     * @return
     */
    public Status getStatus() {
        return status;
    }

    /**
     * 获取本次录音文件的个数
     *
     * @return
     */
    public int getPcmFilesCount() {
        return filesName.size();
    }

    /**
     * 获取本次录音文件
     * @return
     */
    public File getRecordFile(Context context) {
        return AudioFileUtils.getRecordFile(context);
    }

    /**
     * 存储本次通话信息
     */
    public void saveCustomerInformation() {
        //callPhone = customerPhone;
        callPhone = PersistentDataCacheEntity.getInstance().getCallNumber();
        callCustomerName = PersistentDataCacheEntity.getInstance().getCallCustomerName();
        callState = PersistentDataCacheEntity.getInstance().getCallState();
    }

    /**
     * 录音对象的状态
     */
    public enum Status {
        //未开始
        STATUS_NO_READY,
        //预备
        STATUS_READY,
        //录音
        STATUS_START,
        //暂停
        STATUS_PAUSE,
        //停止
        STATUS_STOP
    }
}
