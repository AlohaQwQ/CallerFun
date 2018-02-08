package com.xixi.finance.callerfun.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by AlohaQoQ on 2018/1/29.
 */
public class AudioFileUtils {

    private static String recordRootPath = "record";

    private static String tempRecordFileName = "tempRecord";

    private static String recordFileName;
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取录音存储文件夹
     *
     * @return
     */
    public static File getRecordDir(final Context context) {
        return FileUtil.getCacheDir(context, recordRootPath);
    }

    /**
     * 是否有录音文件
     *
     * @param context
     * @return
     */
    public static boolean hasRecordFiles(final Context context) {
        return FileUtil.hasDirFile(getRecordDir(context));
    }

    /**
     * 获取录音文件夹大小
     *
     * @return
     */
    public static String getRecordFilesStorageSize(final Context context) {
        long size = 0;
        try {
            java.io.File[] fileList = getRecordDir(context).listFiles();
            for (int i = 0; i < fileList.length; i++) {
                size = size + fileList[i].length();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //return size/1048576;
        return getFormatSize(Double.valueOf(size));
    }

    /**
     * 清除录音文件缓存
     *
     * @return
     */
    public static void deleteRecordFolderFile(final Context context) {
        try {
            File file = getRecordDir(context);
            if (file.isDirectory()) {// 处理目录
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File tempFile = new File(files[i].getAbsolutePath());
                    if (tempFile != null && tempFile.exists() && !tempFile.isDirectory())
                        tempFile.delete();
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        String sizeString = null;
        if(size>0){
            double kiloByte = size / 1024; //Byte
            if (kiloByte < 1) {
                sizeString = kiloByte + "KB";
            }
            double megaByte = kiloByte / 1024; //KB
            if (megaByte < 1) {
                BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
                sizeString = result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
            }

            double gigaByte = megaByte / 1024;
            if (gigaByte < 1) {
                BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
                sizeString = result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
            }

            /*double teraBytes = gigaByte / 1024;
            if (teraBytes < 1) {
                BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
                return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
            }
            BigDecimal result4 = new BigDecimal(teraBytes);
            return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";*/
        } else {
            sizeString = "0.0MB";
        }
        return sizeString;
    }

    /**
     * 获取Pcm 原始不可播放存储文件
     *
     * @return
     */
    public static String getPcmFileAbsolutePath(Context context) {
        if (TextUtils.isEmpty(tempRecordFileName)) {
            throw new NullPointerException("fileName isEmpty");
        }
        if (!tempRecordFileName.endsWith(".pcm")) {
            tempRecordFileName = tempRecordFileName + "_" + System.currentTimeMillis() + ".pcm";
        }
        return getRecordDir(context).getAbsolutePath() + "/" + tempRecordFileName;
    }

    /**
     * 创建wav 可播放文件
     *
     * @param context
     * @param callCustomerName
     * @param callPhone
     * @return
     */
    public static String getWavFileAbsolutePath(Context context, String callCustomerName, String callPhone,String callState) {
        recordFileName = callCustomerName + "_" + callPhone + "_" + callState + "_" + System.currentTimeMillis() + ".wav";
        return getRecordDir(context).getAbsolutePath() + "/" + recordFileName;
    }

    /**
     * 获取本次录音文件
     *
     * @param context
     * @return
     */
    public static File getRecordFile(Context context) {
        return new File(getRecordDir(context).getAbsolutePath(), recordFileName);
    }

    /**
     * 获取全部wav录音文件列表
     *
     * @return
     */
    public static List<File> getWavRecordFiles(Context context) {
        List<File> list = new ArrayList<>();
        File[] files = getRecordDir(context).listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".wav")) {
                list.add(file);
            }
        }
        return list;
    }

    /**
     * 删除Pcm 缓存文件
     *
     * @return
     */
    public static void deletePcmFileAbsolutePath(Context context) {
        File file = new File(getPcmFileAbsolutePath(context));
        if (file.exists())
            file.delete();
    }

    /**
     * 获取录音文件时长(毫秒)
     *
     * @return
     */
    public static int getRecordDuration(String recordPath) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        int duration = 0;
        try {
            mediaPlayer.setDataSource(recordPath);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return duration;
    }

    /**
     * 获取录音文件时长(秒)
     *
     * @return
     */
    public static int getRecordDurationSecond(int duration) {
        return (int) Math.ceil(duration / 1000);
    }

    /**
     * 获取录音文件时长(分秒)
     *
     * @return
     */
    public static String getRecordMinute(String recordPath) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        StringBuffer sb = new StringBuffer();
        int duration = 0;
        try {
            mediaPlayer.setDataSource(recordPath);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();

            Integer ss = 1000;
            Integer mi = ss * 60;
            Integer hh = mi * 60;
            Integer dd = hh * 24;

            Long day = Long.valueOf(duration / dd);
            Long hour = (duration - day * dd) / hh;
            Long minute = (duration - day * dd - hour * hh) / mi;
            Long second = (duration - day * dd - hour * hh - minute * mi) / ss;

            if (minute > 0) {
                sb.append(minute + "分");
            }
            if (second > 0) {
                if (minute > 0) {
                    sb.append(second + "秒");
                } else {
                    sb.append("0分" + second + "秒");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取录音文件时长(分秒)
     *
     * @return
     */
    public static String getRecordMinute2(String recordPath) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        StringBuffer sb = new StringBuffer();
        int duration = 0;
        try {
            mediaPlayer.setDataSource(recordPath);
            mediaPlayer.prepare();
            duration = mediaPlayer.getDuration();

            Integer ss = 1000;
            Integer mi = ss * 60;
            Integer hh = mi * 60;
            Integer dd = hh * 24;

            Long day = Long.valueOf(duration / dd);
            Long hour = (duration - day * dd) / hh;
            Long minute = (duration - day * dd - hour * hh) / mi;
            Long second = (duration - day * dd - hour * hh - minute * mi) / ss;

            if (minute > 0) {
                sb.append(minute + ":");
            }
            if (second > 0) {
                if (minute > 0) {
                    sb.append(second);
                } else if(second<10) {
                    sb.append("00:0" + second);
                } else {
                    sb.append("00:" + second);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 获取录音文件创建时间(毫秒数)
     *
     * @return
     */
    public static String getRecordCreateTime(String recordPath) {
        return milliToRecentToday(new File(recordPath).lastModified());
    }

    /**
     * Created by Aloha <br>
     * -explain 判断录音创建时间是今天还是昨天
     *
     * @param mill 毫秒数
     * @return
     * @Date 2017/07/11 12:45
     */
    public static String milliToRecentToday(long mill) {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        int nowDate = new java.util.Date().getDate();
        String result = null;
        try {
            String resultDate = sdf.format(new java.sql.Date(mill));
            Date oo = sdf.parse(sdf.format(new java.sql.Date(mill)));
            if (nowDate - oo.getDate() == 0) {
                if (oo.getMinutes() < 10) {
                    result = "今天 " + oo.getHours() + ":0" + oo.getMinutes();
                } else {
                    result = "今天 " + oo.getHours() + ":" + oo.getMinutes();
                }
            } else if (nowDate - oo.getDate() == 1) {
                if (oo.getMinutes() < 10) {
                    result = "昨天 " + oo.getHours() + ":0" + oo.getMinutes();
                } else {
                    result = "昨天 " + oo.getHours() + ":" + oo.getMinutes();
                }
            } else {
                result = resultDate.substring(0, resultDate.length() - 3);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

}
