package com.xixi.finance.callerfun.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

/**
 * Created by Aloha <br>
 * -explain 文件工具类
 *
 * @Date 2018/1/26 13:59
 */
public class FileUtil {

    /**
     * Cache 文件夹缓存根目录
     */
    private static final String cacheBaseDirName = "core";
    private static File cacheBaseDir;

    /**
     * 获得缓存父文件夹
     *
     * @param context 上下文环境
     * @return 缓存父文件夹
     * @since 0.0.1
     */
    public static File getCacheBaseDir(final Context context) {
        if (null == cacheBaseDir) {
            File baseDir = null;
            if (Build.VERSION.SDK_INT >= 8) {
                baseDir = context.getExternalCacheDir();
            } else {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    baseDir = Environment.getExternalStorageDirectory();
                }
            }

            if (null == baseDir)
                baseDir = context.getCacheDir();

            if (null != baseDir) {
                cacheBaseDir = new File(baseDir, cacheBaseDirName);
                if (!cacheBaseDir.exists()) {
                    cacheBaseDir.mkdirs();
                }
            } // if (null != baseDir)

        } // if (null == cacheBaseDir)

        return cacheBaseDir;
    }

    /**
     * 获得缓存文件夹
     *
     * @param context      上下文环境
     * @param cacheDirName 缓存文件夹名   getExternalCacheDir()/cache/cacheDirName/
     * @return 缓存文件夹
     * @see #getCacheBaseDir(Context)
     * @since 0.0.1
     */
    public static File getCacheDir(final Context context, final String cacheDirName) {
        File cacheDir = null;
        cacheBaseDir = getCacheBaseDir(context);
        if (null != cacheBaseDir) {
            if (null != cacheDirName && 0 != cacheDirName.trim().length()) {
                cacheDir = new File(cacheBaseDir, cacheDirName);
            } else {
                cacheDir = cacheBaseDir;
            }
        }
        if (null != cacheDir && !cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                cacheDir = null;
            }
        }
        if (null == cacheDir) {
            cacheDir = cacheBaseDir;
        }
        return cacheDir;
    }

    /**
     * 获得缓存文件夹Name
     *
     * @param context      上下文环境
     * @return 缓存文件夹
     * @see #getCacheBaseDir(Context)
     * @since 0.0.1
     */
    public static String getCacheDirName(final Context context) {
        return getCacheBaseDir(context).getAbsolutePath();
    }

    /**
     * 获得缓存文件
     *
     * @param context       上下文环境
     * @param cacheDirName  缓存文件夹名
     * @param cacheFileName 缓存文件名
     * @return 缓存文件
     * @see #getCacheDir(Context, String)
     * @since 0.0.1
     */
    public static File getCacheFile(final Context context, final String cacheDirName, final String cacheFileName) {
        File cacheFile = null;

        File cacheDir = getCacheDir(context, cacheDirName);
        if (null != cacheDir && null != cacheFileName && 0 != cacheFileName.trim().length()) {
            cacheFile = new File(cacheDir, cacheFileName);
        }

        return cacheFile;
    }

    public static boolean deleteFile(String filePath) {
        File deleteFile = new File(filePath);
        return deleteFile.delete();
    }

    /**
     * 获取文件夹所占空间大小
     *
     * @param dir 文件夹
     * @return 内存大小
     * @since 0.0.1
     */
    public static long getDirSize(File dir) {
        if (dir == null) {
            return 0;
        }
        if (!dir.isDirectory()) {
            return 0;
        }
        long dirSize = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                dirSize += file.length();
            } else if (file.isDirectory()) {
                dirSize += file.length();
                dirSize += getDirSize(file); // 如果遇到目录则通过递归调用继续统计
            }
        }
        return dirSize;
    }

    /**
     * 删除文件夹
     *
     * @param path 文件夹路径
     * @since 0.0.1
     */
    public static void deleteFolder(File path) {
        if (!path.exists())
            return;
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            deleteFolder(files[i]);
        }
        //        path.delete();
    }

    /**
     * 获取外部缓存文件夹
     *
     * @param context 上下文环境
     * @param dirName 文件夹名称
     * @return
     * @since 0.0.2
     */
    public static File getExternalDir(final Context context, final String dirName) {
        File externalCacheDir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            externalCacheDir = new File(Environment.getExternalStorageDirectory(), dirName);
        }
        if (null != externalCacheDir && !externalCacheDir.exists()) {
            externalCacheDir.mkdirs();
        }
        if (null == externalCacheDir) {
            externalCacheDir = cacheBaseDir;
        }
        return externalCacheDir;
    }

    /**
     * 判断某个文件夹下是否有文件
     *
     * @return
     * @since 0.0.2
     */
    public static boolean hasDirFile(final File dirFile) {
        if(dirFile.exists() && dirFile.isDirectory()){
            if (dirFile.list().length>0){
                return true;
            }
        }
        return false;
    }
}
