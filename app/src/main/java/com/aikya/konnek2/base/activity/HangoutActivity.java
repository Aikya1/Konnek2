package com.aikya.konnek2.base.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.aikya.konnek2.utils.AppConstant;


public class HangoutActivity extends AppCompatActivity {

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.aikya.konnek2.R.layout.activity_hangout);
        toolbar = findViewById(com.aikya.konnek2.R.id.toolbar_hangouts);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(AppConstant.HOME + AppConstant.GREATER_THAN + AppConstant.HANGOUTS);
        toolbar.setNavigationIcon(com.aikya.konnek2.R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(com.aikya.konnek2.R.color.white));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.aikya.konnek2.R.menu.menu_mstore, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
