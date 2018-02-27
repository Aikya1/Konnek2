package com.android.konnek2.ui.activities.authorization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.konnek2.R;
import com.android.konnek2.call.core.models.LoginType;
import com.android.konnek2.utils.helpers.SystemPermissionHelper;

import butterknife.OnClick;

public class LandingActivity extends BaseAuthActivity {

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
    }

    @OnClick(R.id.btnsignups)
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


    private boolean checkRecordPermission() {

        if (systemPermissionHelper.isContactPermissionGranted()) {
            return true;
        } else {
            systemPermissionHelper.requestPermissionsReadContacts();
            return false;
        }
    }
}