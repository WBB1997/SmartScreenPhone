package com.wubeibei.smartscreenphone.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by fangju on 2018/11/23
 */
public class LogUtil {
    private static final String TAG = "LogUtil";
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/Crash/out/";
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".log";
    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    private static final int NOTHING = 6;

    private static int level = VERBOSE;//预设等级

    public static void v(String tag, String msg) {
        if (level <= VERBOSE) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (level <= DEBUG) {
            Log.d(tag, msg);
//            try {
//                dumpExceptionToSDCard(null,tag,msg);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    public static void i(String tag, String msg) {
        if (level <= INFO) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (level <= WARN) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (level <= ERROR) {
            Log.e(tag, msg);
        }
    }

    /**
     * 将日志输出到本地文件
     *
     * @param ex
     * @param tag
     * @param msg
     * @throws IOException
     */
    private static void dumpExceptionToSDCard(Throwable ex, String tag, String msg) throws IOException {
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.w(TAG, "sdcard unmounted,skip dump exception");
        }
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date(current));
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        if (!file.exists()) {//文件不存在
            file.createNewFile();
        }
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
            pw.println(time + "-->");
            pw.println(tag + ":" + msg);
            pw.println();
            pw.close();
        } catch (Exception e) {
            Log.e(TAG, "dump crash info failed");
        }
    }
}
