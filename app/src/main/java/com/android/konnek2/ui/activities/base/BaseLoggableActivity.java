package com.android.konnek2.ui.activities.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;


import com.android.konnek2.call.core.models.AppSession;
import com.android.konnek2.call.core.utils.ConstsCore;
import com.android.konnek2.base.activity.AppSplashActivity;
import com.android.konnek2.utils.Loggable;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BaseLoggableActivity extends BaseActivity implements Loggable {

    public AtomicBoolean canPerformLogout = new AtomicBoolean(true);

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public void onAttachFragment(Fragment fragment) {

        super.onAttachFragment(fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!appInitialized) {

            startSplashActivity();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(CAN_PERFORM_LOGOUT)) {
            canPerformLogout = new AtomicBoolean(savedInstanceState.getBoolean(CAN_PERFORM_LOGOUT));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(CAN_PERFORM_LOGOUT, canPerformLogout.get());
        super.onSaveInstanceState(outState);
    }

    //This method is used for logout action when Activity is going to background
    @Override
    public boolean isCanPerformLogoutInOnStop() {
        return canPerformLogout.get();
    }

    protected void startSplashActivity(){
//        SplashActivity.start(this);
        AppSplashActivity.start(this);
        finish();
    }

    protected boolean isCurrentSessionValid() {
        return AppSession.isSessionExistOrNotExpired(TimeUnit.MINUTES.toMillis(
                ConstsCore.TOKEN_VALID_TIME_IN_MINUTES));
    }
}