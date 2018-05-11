package com.aikya.konnek2.base.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.aikya.konnek2.App;
import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.service.QBService;
import com.aikya.konnek2.call.core.service.QBServiceConsts;
import com.aikya.konnek2.call.core.utils.helpers.CoreSharedHelper;
import com.aikya.konnek2.call.services.model.QMUser;
import com.aikya.konnek2.ui.activities.authorization.LandingActivity;
import com.aikya.konnek2.ui.activities.base.BaseActivity;
import com.aikya.konnek2.utils.AppConstant;
import com.aikya.konnek2.utils.ToastUtils;
import com.aikya.konnek2.utils.helpers.ServiceManager;
import com.quickblox.auth.model.QBProvider;
import com.quickblox.auth.session.QBSessionManager;
import com.quickblox.auth.session.QBSessionParameters;
import com.quickblox.users.model.QBUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observer;
import rx.Subscriber;

public class AppSplashActivity extends BaseActivity {


    private static final String TAG = AppSplashActivity.class.getSimpleName();
    private static final int DELAY_FOR_OPENING_LANDING_ACTIVITY = 1000;

    public static void start(Context context) {
        Intent intent = new Intent(context, AppSplashActivity.class);
        context.startActivity(intent);
    }

    private static int SPLASH_TIME_OUT = AppConstant.SPLASH_TIME;


    @Override
    protected int getContentResId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_app_splash);

        generateFacebookKeyHash();

        if (QBSessionManager.getInstance().getSessionParameters() != null
                && QBProvider.TWITTER_DIGITS.equals(QBSessionManager.getInstance().getSessionParameters().getSocialProvider())) {
            restartAppWithFirebaseAuth();
            return;
        }
        appInitialized = true;
//        AppSession.load();
        processPushIntent();

        QBSessionParameters parameters = QBSessionManager.getInstance().getSessionParameters();

        if (parameters != null && parameters.getUserLogin() != null) {
            showProgress();
            String phNumber = appSharedHelper.getUserPhoneNumber();
            ServiceManager.getInstance().checkIfUserExist(appSharedHelper.getUserPhoneNumber()).subscribe(checkIfUserExists);
        } else {
            startActivity();
        }

        /*if (QBSessionManager.getInstance().getSessionParameters() != null && appSharedHelper.isSavedRememberMe()) {
            startLastOpenActivityOrMain();
        } else {

            if (appSharedHelper.getUserPhoneNumber() != null) {
            } else {
                startActivity();
            }
//            startLandingActivity();
//            startActivity();
        }*/
    }

    private void generateFacebookKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.android.konnek2", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public void startActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                LandingActivity.start(AppSplashActivity.this);
//                Intro.start(AppSplashActivity.this);
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
//                overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }


    private void processPushIntent() {
        boolean openPushDialog = getIntent().getBooleanExtra(QBServiceConsts.EXTRA_SHOULD_OPEN_DIALOG, false);
        CoreSharedHelper.getInstance().saveNeedToOpenDialog(openPushDialog);
    }

    private void startLandingActivity() {

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                LandingActivity.start(AppSplashActivity.this);
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
                lastActivityClass = AppHomeActivity.class;
            }
        } catch (ClassNotFoundException e) {
            needCleanTask = true;
            lastActivityClass = AppHomeActivity.class;
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

    private Observer<List<QMUser>> checkIfUserExists = new Observer<List<QMUser>>() {
        @Override
        public void onCompleted() {
            hideProgress();
        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "User List == " + e.getMessage());
        }

        @Override
        public void onNext(List<QMUser> userList) {

            if (userList.size() > 0) {
                startLastOpenActivityOrMain();
            } else {
                startActivity();
                ToastUtils.shortToast("User has been removed");

            }

        }
    };


}
