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

import com.aikya.konnek2.R;
import com.aikya.konnek2.ui.adapters.base.AppMstoreAdapter;
import com.aikya.konnek2.utils.AppConstant;
import com.aikya.konnek2.utils.listeners.AppCommon;


public class AppMobileStoreActivity extends AppCompatActivity {

    GridView gridView;
    AppMstoreAdapter appMstoreAdapter;
    Toolbar toolbar;
    String[] mStoreName;
    TypedArray mStoreImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_mobile_store);


        toolbar = findViewById(R.id.toolbar_mstore);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(AppConstant.HOME + AppConstant.GREATER_THAN + AppConstant.M_STORE);
        toolbar.setNavigationIcon(R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        gridView = findViewById(R.id.mobile_store);

        mStoreImage = getResources().obtainTypedArray(R.array.mstore_image); // String  Values  from resource  files
        mStoreName = getResources().getStringArray(R.array.mstore);          //  Image  Values  from resource  files
        appMstoreAdapter = new AppMstoreAdapter(AppMobileStoreActivity.this, mStoreName, mStoreImage);

        gridView.setAdapter(appMstoreAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        Intent goToNavigation5 = new Intent(getApplicationContext(), AppTravelActivity.class);
                        startActivity(goToNavigation5);
                        break;

                    case 1:
                        Intent goToMoney = new Intent(getApplicationContext(), AppMoneyActivity.class);
                        startActivity(goToMoney);
                        break;

                    case 2:
                        AppCommon.displayToast(AppConstant.TOAST_MESSAGE);
                        break;

                    case 3:
                        AppCommon.displayToast(AppConstant.TOAST_MESSAGE);
                        break;


                    case 4:
                        AppCommon.displayToast(AppConstant.TOAST_MESSAGE);
                        break;

                    case 5:
                        AppCommon.displayToast(AppConstant.TOAST_MESSAGE);
//                        Intent goToMoney = new Intent(getApplicationContext(), AppTravelActivity.class);
//                        startActivity(goToMoney);
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
        inflater.inflate(R.menu.menu_mstore, menu);
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
