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

    Bundle bundle;

    @Override
    protected int getContentResId() {
        return R.layout.activity_gdpr;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gdpr);
        ButterKnife.bind(this);
        bundle = getIntent().getExtras();

        //if login type is manual then get phone nubmer with country code
        if (bundle != null && bundle.getString("loginType").equalsIgnoreCase(AppConstant.LOGIN_TYPE_MANUAL)) {
            phNo = getIntent().getExtras().getString("phNo");
            countryCode = getIntent().getExtras().getString("countryCode");
        }

        appSharedHelper.saveLoginType(bundle.getString("loginType"));

    }


    @OnClick(R.id.submitbtn)
    public void setSubmitBtn() {

        if (radioeval()) {
            if (rgYesBtn.isChecked()) {
                //OTP verification
                loginType = LoginType.FIREBASE_PHONE;
                startSocialLogin(phNo, countryCode);
            }
            if (rgNoBtn.isChecked()) {
                //No OTP VERIFICATION : GOTO INTRO screen
                //Manual login...
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
