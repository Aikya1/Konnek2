package com.aikya.konnek2.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.qb.commands.chat.QBLogoutAndDestroyChatCommand;
import com.aikya.konnek2.ui.activities.base.BaseActivity;
import com.aikya.konnek2.call.core.qb.commands.chat.QBLoginChatCompositeCommand;
import com.quickblox.core.helper.Lo;
import com.quickblox.chat.QBChatService;


public class ActivityLifecycleHandler implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = ActivityLifecycleHandler.class.getSimpleName();

    private int numberOfActivitiesInForeground;
    private boolean chatDestroyed = false;

    @SuppressLint("LongLogTag")
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @SuppressLint("LongLogTag")
    public void onActivityStarted(Activity activity) {

        boolean activityLogeable = isActivityLogeable(activity);
        chatDestroyed = chatDestroyed && !isLoggedIn();
        if (numberOfActivitiesInForeground == 0 && activityLogeable) {
            AppSession.getSession().updateState(AppSession.ChatState.FOREGROUND);
            if (chatDestroyed) {
                boolean isLoggedIn = AppSession.getSession().isLoggedIn();
                Log.d(TAG, "isSessionExist()" + isLoggedIn);
                boolean canLogin = chatDestroyed && isLoggedIn;
                boolean networkAvailable = ((BaseActivity) activity).isNetworkAvailable();
                Log.d(TAG, "networkAvailable" + networkAvailable);
                if (canLogin) {
                    QBLoginChatCompositeCommand.start(activity);
                }
            }
        }

        if (activityLogeable) {
            ++numberOfActivitiesInForeground;
        }

    }

    @SuppressLint("LongLogTag")
    public void onActivityResumed(Activity activity) {

    }

    public boolean isActivityLogeable(Activity activity) {
        return (activity instanceof Loggable);
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
        //Count only our app logeable activity
        if (activity instanceof Loggable) {
            --numberOfActivitiesInForeground;
        }
        Lo.g("onActivityStopped" + numberOfActivitiesInForeground);

        if (numberOfActivitiesInForeground == 0 && activity instanceof Loggable) {
            AppSession.getSession().updateState(AppSession.ChatState.BACKGROUND);
            boolean isLogedIn = isLoggedIn();
            if (!isLogedIn) {
                return;
            }
            chatDestroyed = ((Loggable) activity).isCanPerformLogoutInOnStop();
            if (chatDestroyed) {
                QBLogoutAndDestroyChatCommand.start(activity, true);
            }
        }
    }

    private boolean isLoggedIn() {
        return QBChatService.getInstance().isLoggedIn();
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityDestroyed(Activity activity) {
    }
}