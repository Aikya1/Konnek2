package com.aikya.konnek2.base.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.aikya.konnek2.ui.adapters.base.AppTravelAdapter;
import com.aikya.konnek2.utils.listeners.AppCommon;
import com.aikya.konnek2.utils.AppConstant;


public class AppTravelActivity extends AppCompatActivity {

    private GridView gridView;
    AppTravelAdapter appTravelAdapter;
    Toolbar toolbar;
    String[] travelName;
    TypedArray travelImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.aikya.konnek2.R.layout.activity_travel);
        toolbar = findViewById(com.aikya.konnek2.R.id.toolbar_travel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(AppConstant.HOME + AppConstant.GREATER_THAN + AppConstant.M_STORE + AppConstant.TRAVEL);
        toolbar.setNavigationIcon(com.aikya.konnek2.R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(com.aikya.konnek2.R.color.white));
        gridView = findViewById(com.aikya.konnek2.R.id.home_travelgrid);
        travelImage = getResources().obtainTypedArray(com.aikya.konnek2.R.array.Travel_image); // String  Values  from resource  files
        travelName = getResources().getStringArray(com.aikya.konnek2.R.array.Travel_name);
        appTravelAdapter = new AppTravelAdapter(AppTravelActivity.this, travelName, travelImage);
        gridView.setAdapter(appTravelAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        AppCommon.displayToast(AppConstant.TOAST_MESSAGE);
                        break;
                    case 1:
                        AppCommon.displayToast(AppConstant.TOAST_MESSAGE);
                        break;
                    case 2:
                        AppCommon.displayToast(AppConstant.TOAST_MESSAGE);
                        break;
                    case 3:
                        AppCommon.displayToast(AppConstant.TOAST_MESSAGE);
                        break;
                    default:
                        break;

                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.aikya.konnek2.R.menu.menu_travel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case com.aikya.konnek2.R.id.menu_homebutton1:
                Intent goToHome = new Intent(getApplicationContext(), AppHomeActivity.class);
                startActivity(goToHome);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
