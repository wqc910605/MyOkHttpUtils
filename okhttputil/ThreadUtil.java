package com.wwf.myapplication.okhttputil;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by wwf on 2017/10/12.
 */

public class ThreadUtil {

    private static Handler sHandler= new Handler(Looper.getMainLooper());
    private static Executor sExecutor = Executors.newCachedThreadPool();
    public static void runOnUiThread(Runnable runnable) {
        sHandler.post(runnable);
    }
    public static void runOnSubThread(Runnable runnable) {
        sExecutor.execute(runnable);
    }

}
