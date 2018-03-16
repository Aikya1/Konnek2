package com.android.konnek2.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;

import com.android.konnek2.R;
import com.android.konnek2.call.core.models.LoginType;
import com.android.konnek2.ui.activities.authorization.BaseAuthActivity;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.ToastUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GdprActivity extends BaseAuthActivity {

    public final static String TAG = GdprActivity.class.getSimpleName();

    private String phNo, countryCode;
    @Bind(R.id.rgYes)
    RadioButton rgYesBtn;
    @Bind(R.id.rgNo)
    RadioButton rgNoBtn;
    @Bind(R.id.submitbtn)
    Button submit;


    @Override
    protected int getContentResId() {
        return R.layout.activity_gdpr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gdpr);
        ButterKnife.bind(this);

        phNo = getIntent().getExtras().getString("phNo");
        countryCode = getIntent().getExtras().getString("countryCode");
    }


    @OnClick(R.id.submitbtn)
    public void setSubmitBtn() {

        if (radioeval()) {
            if (rgYesBtn.isChecked()) {
                //OTP verification
                loginType = LoginType.FIREBASE_PHONE;
                appSharedHelper.saveLoginType(AppConstant.LOGIN_TYPE_FIREBASE);
                startSocialLogin(phNo, countryCode);
            }
            if (rgNoBtn.isChecked()) {
                //No OTP VERIFICATION : GOTO INTRO screen
                //Manual login...
                loginType = LoginType.MANUAL;
                appSharedHelper.saveLoginType(AppConstant.LOGIN_TYPE_MANUAL);
                Intent i = new Intent(GdprActivity.this, Intro.class);
                i.putExtra("phNo", phNo);
                startActivity(i);
            }

        }
    }

    private boolean radioeval() {
        if (!rgYesBtn.isChecked() && !rgNoBtn.isChecked()) {
            ToastUtils.shortToast("Select an option");
            return false;
        } else {
            return true;
        }
    }

}
