package com.aikya.konnek2.ui.activities.authorization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.aikya.konnek2.ui.activities.main.MainActivity;
import com.aikya.konnek2.App;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.models.AppSession;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.core.utils.helpers.CoreSharedHelper;
import com.aikya.konnek2.utils.helpers.ServiceManager;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.auth.session.QBSessionManager;


import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;

public class SplashActivity extends BaseAuthActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int DELAY_FOR_OPENING_LANDING_ACTIVITY = 1000;

    public static void start(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_splash;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        //TODO VT temp code for correct migration from Twitter Digits to Firebase Phone Auth
        //should be removed in next release
        if (QBSessionManager.getInstance().getSessionParameters() != null
                && QBProvider.TWITTER_DIGITS.equals(QBSessionManager.getInstance().getSessionParameters().getSocialProvider())) {
            restartAppWithFirebaseAuth();
            return;
        }
        //TODO END

        appInitialized = true;
        AppSession.load();

        processPushIntent();

        if (QBSessionManager.getInstance().getSessionParameters() != null && appSharedHelper.isSavedRememberMe()) {
            startLastOpenActivityOrMain();
        } else {
            startLandingActivity();
        }
    }

    private void processPushIntent() {
        boolean openPushDialog = getIntent().getBooleanExtra(QBServiceConsts.EXTRA_SHOULD_OPEN_DIALOG, false);
        CoreSharedHelper.getInstance().saveNeedToOpenDialog(openPushDialog);
    }

    private void startLandingActivity() {
        Log.v(TAG, "startLandingActivity();");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                LandingActivity.start(SplashActivity.this);
                finish();
            }
        }, DELAY_FOR_OPENING_LANDING_ACTIVITY);
    }

    private void startLastOpenActivityOrMain() {
        Class<?> lastActivityClass;
        boolean needCleanTask = false;
        try {
            String lastActivityName = appSharedHelper.getLastOpenActivity();
            if (lastActivityName != null) {
                lastActivityClass = Class.forName(appSharedHelper.getLastOpenActivity());
            } else {
                needCleanTask = true;
                lastActivityClass = MainActivity.class;
            }
        } catch (ClassNotFoundException e) {
            needCleanTask = true;
            lastActivityClass = MainActivity.class;
        }
        Log.v(TAG, "start " + lastActivityClass.getSimpleName());
        startActivityByName(lastActivityClass, needCleanTask);
    }

    private void restartAppWithFirebaseAuth() {
        ServiceManager.getInstance().logout(new Subscriber<Void>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Intent intent = new Intent(App.getInstance(), LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                LandingActivity.start(App.getInstance(), intent);
                finish();
            }

            @Override
            public void onNext(Void aVoid) {
                Intent intent = new Intent(App.getInstance(), LandingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                LandingActivity.start(App.getInstance(), intent);
                finish();
            }
        });
    }
}