package com.android.konnek2.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


import com.android.konnek2.App;

import java.util.List;

public class SystemUtils {

    public static boolean isAppRunningNow() {
        ActivityManager activityManager = (ActivityManager) App.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
        ComponentName componentName = runningTaskInfo.topActivity;
        String pack = componentName.getPackageName();
        String pack2 = App.getInstance().getPackageName();
        boolean res = !runningTasks.isEmpty();

        if (!runningTasks.isEmpty()) {
            return runningTasks.get(0).topActivity.getPackageName().equalsIgnoreCase(App.getInstance().getPackageName());
        } else {
            return false;
        }
    }

    public static Intent getPreviousIntent(Context context) {
        ActivityManager activityManager = (ActivityManager) App.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
        String ourPackageName = App.getInstance().getPackageName();

        if (runningTasks != null) {
            for (ActivityManager.RunningTaskInfo taskInfo : runningTasks) {
                if (taskInfo.topActivity.getPackageName().equalsIgnoreCase(ourPackageName)) {
                    try {
                        Intent intent = new Intent(context, Class.forName(taskInfo.topActivity.getClassName()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        return intent;
                    } catch (ClassNotFoundException e) {
                        // This should never happen
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }

    public static int getThreadPoolSize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Runtime.getRuntime().availableProcessors();
        } else {
            return Runtime.getRuntime().availableProcessors() * 2;
        }
    }

    public static String getNameActivityOnTopStack() {
        ActivityManager activityManager = (ActivityManager) App.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
        String ourPackageName = App.getInstance().getPackageName();
        String topActivityName = null;

        if (runningTasks != null) {
            for (ActivityManager.RunningTaskInfo taskInfo : runningTasks) {
                if (taskInfo.topActivity.getPackageName().equalsIgnoreCase(ourPackageName)) {
                    topActivityName = taskInfo.topActivity.getClassName();
                }
            }
        }
        return topActivityName;
    }
}