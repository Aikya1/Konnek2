package com.android.konnek2.ui.activities.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.konnek2.R;
import com.android.konnek2.ui.activities.agreements.UserAgreementActivity;
import com.android.konnek2.ui.activities.base.BaseLoggableActivity;

import butterknife.Bind;
import butterknife.OnClick;

public class AboutActivity extends BaseLoggableActivity {

    @Bind(R.id.app_version_textview)
    TextView appVersionTextView;

    public static void start(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return R.layout.activity_about;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setUpActionBarWithUpButton();
        fillUI();
    }

    private void initFields() {
        title = getString(R.string.about_title);
    }

    private void fillUI() {
//        appVersionTextView.setText(StringObfuscator.getAppVersionName());
        appVersionTextView.setText(R.string.app_name);
    }

    @OnClick(R.id.license_button)
    void openUserAgreement(View view) {
        UserAgreementActivity.start(this);
    }
}