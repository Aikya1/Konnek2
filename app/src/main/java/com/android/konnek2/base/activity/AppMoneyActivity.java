package com.android.konnek2.base.activity;

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

import com.android.konnek2.R;
import com.android.konnek2.ui.adapters.base.AppMoneyAdapter;
import com.android.konnek2.utils.AppConstant;
import com.android.konnek2.utils.listeners.AppCommon;


public class AppMoneyActivity extends AppCompatActivity {

    private GridView gridView;
    AppMoneyAdapter appMoneyAdapter;
    Toolbar toolbar;
    String[] MoneylName;
    TypedArray MoneyImage ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_money);


        toolbar = findViewById(R.id.toolbar_money);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(AppConstant.HOME + AppConstant.GREATER_THAN + AppConstant.M_STORE+AppConstant.MONEY );
        toolbar.setNavigationIcon(R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        gridView = findViewById(R.id.money_grid);

        MoneyImage = getResources().obtainTypedArray(R.array.money_image); // String  Values  from resource  files
        MoneylName = getResources().getStringArray(R.array.money_name);          //  Image  Values  from resource  files
        appMoneyAdapter = new AppMoneyAdapter(AppMoneyActivity.this, MoneylName, MoneyImage);
        gridView.setAdapter(appMoneyAdapter);
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
        inflater.inflate(R.menu.menu_money, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.menu_homebutton1:
                Intent goToHome= new Intent(getApplicationContext(),AppHomeActivity.class);
                startActivity(goToHome);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
