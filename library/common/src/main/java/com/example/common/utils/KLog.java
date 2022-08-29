package com.example.common.utils;

import com.example.common.base.BaseApplication;
import com.lazy.library.logging.Builder;
import com.lazy.library.logging.Logcat;

import java.io.File;


public class KLog {
    public final static String TAG = "";
    public final static String TAG_NEW = "--泰轻松--";

    public static void init(boolean isShowLog) {
        initLog(isShowLog);
    }

    private static void initLog(boolean isShowLog) {
        Builder builder = Logcat.newBuilder();
        //设置Log 保存的文件夹
        File texeasyLog = StorageUtils.getDiskDir("Texeasy/Log");
        if (texeasyLog == null) {
            texeasyLog = StorageUtils.getDiskCacheDir(BaseApplication.getInstance(), "Texeasy/Log");
        }
        builder.logSavePath(texeasyLog);
        //设置输出日志等级
        builder.logCatLogLevel(Logcat.SHOW_ALL_LOG);
        //设置输出文件日志等级
        builder.fileLogLevel(Logcat.SHOW_DEBUG_LOG | Logcat.SHOW_INFO_LOG | Logcat.SHOW_WARN_LOG | Logcat.SHOW_ERROR_LOG);
        //不显示日志
        builder.topLevelTag(TAG_NEW);
        //删除过了几天无用日志条目
        builder.deleteUnusedLogEntriesAfterDays(30);
        //是否自动保存日志到文件中
        builder.autoSaveLogToFile(true);
        //是否显示打印日志调用堆栈信息
        builder.showStackTraceInfo(true);
        //是否显示文件日志的时间
        builder.showFileTimeInfo(true);
        //是否显示文件日志级别
        builder.showFileLogLevel(true);
        //是否显示文件日志标签
        builder.showFileLogTag(false);
        //是否显示文件日志的进程以及Linux线程
        builder.showFilePidInfo(false);
        //是否显示文件日志调用堆栈信息
        builder.showFileStackTraceInfo(true);
        //添加该标签,日志将被写入文件
//        builder.addTagToFile(TAG_NEW);
        Logcat.initialize(BaseApplication.getInstance(), builder.build());
    }

    private static void v(String tag, String msg) {
        Logcat.v(msg);
    }

    private static void d(String tag, Object msg) {
        Logcat.d(msg);
    }

    private static void i(String tag, Object msg) {
        Logcat.i(msg);
    }

    private static void w(String tag, Object msg) {
        Logcat.w(msg);
    }

    private static void e(String tag, Object msg) {
        Logcat.e(msg);
    }
}