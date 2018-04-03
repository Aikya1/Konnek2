package com.aikya.konnek2.base.activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aikya.konnek2.R;
import com.aikya.konnek2.utils.AppConstant;


public class AppReferFriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_refer_friend);
        referFriend();

    }

    private void referFriend() {

        String info = "";
        info += AppConstant.REFER_FRIEND_MESSGAE;
        info += AppConstant.PLAY_STORE_LINK;
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType(AppConstant.REFER_FRIEND_ACTION_TYPE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_SUBJECT, AppConstant.APP_NAME);
        intent.putExtra(Intent.EXTRA_TEXT, info);
        startActivity(Intent.createChooser(intent, ""));
    }
}
