package com.android.konnek2.ui.activities.authorization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.android.konnek2.R;
import com.android.konnek2.call.core.models.LoginType;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.helpers.SystemPermissionHelper;
import com.android.konnek2.utils.listeners.AppCommon;

import butterknife.Bind;
import butterknife.OnClick;

public class LandingActivity extends BaseAuthActivity {

    @Bind(R.id.app_version_textview)
    TextView appVersionTextView;

    @Bind(R.id.phone_number_connect_button)
    Button phoneNumberConnectButton;
    private SystemPermissionHelper systemPermissionHelper;

    public static void start(Context context) {
        Intent intent = new Intent(context, LandingActivity.class);
        context.startActivity(intent);
    }

    public static void start(Context context, Intent intent) {
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_landing;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        systemPermissionHelper = new SystemPermissionHelper(this);
        initVersionName();
    }

    @OnClick(R.id.login_button)
    void login(View view) {
//        LoginActivity.start(LandingActivity.this);
//        finish();
        AppCommon.displayToast(AppConstant.TOAST_MESSAGE);

    }

    @OnClick(R.id.phone_number_connect_button)
    void phoneNumberConnect(View view) {
        if (checkNetworkAvailableWithError()) {
            checkRecordPermission();
            loginType = LoginType.FIREBASE_PHONE;
            startSocialLogin();
        }
    }

    @Override
    public void checkShowingConnectionError() {
        // nothing. Toolbar is missing.
    }

    private void startSignUpActivity() {
        SignUpActivity.start(LandingActivity.this);
        finish();
    }

    private void initVersionName() {
        appVersionTextView.setText(R.string.about_version);
    }


    private boolean checkRecordPermission() {

        if (systemPermissionHelper.isContactPermissionGranted()) {
            return true;
        } else {
            systemPermissionHelper.requestPermissionsReadContacts();
            return false;
        }
    }
}