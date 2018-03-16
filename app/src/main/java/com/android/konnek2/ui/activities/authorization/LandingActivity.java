package com.android.konnek2.ui.activities.authorization;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.konnek2.R;
import com.android.konnek2.base.activity.GdprActivity;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.utils.ToastUtils;
import com.android.konnek2.utils.helpers.ServiceManager;
import com.android.konnek2.utils.helpers.SystemPermissionHelper;
import com.hbb20.CountryCodePicker;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observer;

public class LandingActivity extends BaseAuthActivity {

    public static final String TAG = LandingActivity.class.getSimpleName();

    private SystemPermissionHelper systemPermissionHelper;
    CheckBox check1, check2;
    EditText etphoneno;

    private String phoneNumber;
    ServiceManager serviceManager;

    @Bind(R.id.country_isd_list)
    CountryCodePicker countryCodePicker;

    String countryCode, phNumber;

/*    @Bind(R.id.login_button)
    LoginButton fbLoginBtn;*/


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
        check1 = findViewById(R.id.checkBox1);
        check2 = findViewById(R.id.checkBox2);
        etphoneno = findViewById(R.id.etphoneno);
        serviceManager = ServiceManager.getInstance();
    }


    @Override
    public void onStart() {
        super.onStart();
        checkRecordPermission();
    }

    @OnClick(R.id.btnsignups)
    void phoneNumberConnect(View view) {
        if (checkNetworkAvailableWithError()) {
            /*loginType = LoginType.FIREBASE_PHONE;
            startSocialLogin();*/
            if (phoneeval() && checkeval()) {
                //  loginType = LoginType.FIREBASE_PHONE;
                //    startSocialLogin();
                //Go to gdpr page
               /* serviceManager.checkIfUserExist(etphoneno.getText().toString())
                        .subscribe(checkIfUserExists);*/


                countryCode = countryCodePicker.getDefaultCountryCodeWithPlus();
                String ccWithoutPlus = countryCodePicker.getDefaultCountryCode();
                phNumber = etphoneno.getText().toString();

                serviceManager.checkIfUserExist(ccWithoutPlus + phNumber)
                        .subscribe(checkIfUserExists);
/*
                appSharedHelper.saveUserPhoneNumber(phNo);
                appSharedHelper.saveCountryCode(cc);


                Intent i = new Intent(LandingActivity.this, GdprActivity.class);
                i.putExtra("phNo", etphoneno.getText().toString());
                i.putExtra("countryCode", cc);
                startActivity(i);*/
            }

        }
    }


    private boolean checkeval() {
        if (!check1.isChecked()) {
            ToastUtils.shortToast("Terms and conditions is Mandatory");
            return false;
        } else if (!check2.isChecked()) {
            ToastUtils.shortToast("Privacy Policy is Mandatory");
            return false;
        }
        return true;
    }

    private boolean phoneeval() {
        phoneNumber = etphoneno.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.shortToast("Phone number is Mandatory");
            return false;
        }
        return true;
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


    private Observer<List<QMUser>> checkIfUserExists = new Observer<List<QMUser>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Log.d(TAG, "User List == " + e.getMessage());
        }

        @Override
        public void onNext(List<QMUser> userList) {

            Log.d(TAG, "User List == " + userList.toString());

            if (userList.size() > 0) {
                //If the user already exists then send user to home page.

                String ph = userList.get(0).getPhone();
                String pass = userList.get(0).getPassword();
                Log.d(TAG, "Pass = " + pass);


            } else {
                appSharedHelper.saveUserPhoneNumber(phNumber);
                appSharedHelper.saveCountryCode(countryCode);
                Intent i = new Intent(LandingActivity.this, GdprActivity.class);
                i.putExtra("phNo", etphoneno.getText().toString());
                i.putExtra("countryCode", countryCode);
                startActivity(i);
            }

        }
    };
}