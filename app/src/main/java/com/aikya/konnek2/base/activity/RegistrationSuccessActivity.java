package com.aikya.konnek2.base.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aikya.konnek2.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrationSuccessActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_success);
        ButterKnife.bind(this);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, RegistrationSuccessActivity.class);
        context.startActivity(intent);
    }


    @OnClick(R.id.getStartedBtn)
    public void startAppHome() {
        Intro.start(RegistrationSuccessActivity.this);
    }
}
