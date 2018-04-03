package com.aikya.konnek2.base.activity;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aikya.konnek2.utils.listeners.AppCommon;
import com.aikya.konnek2.R;
import com.aikya.konnek2.utils.AppConstant;


public class CharityActivity extends AppCompatActivity {

    String[] charityName;
    String[] charityDescription;
    int[] charityImage = {
            R.drawable.charity_mother,
            R.drawable.help_india,
            R.drawable.charity_smile_foundation,
    };
    ImageView charityImageView;
    TextView charityTextview;
    Toolbar toolbar;
    AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity);

        toolbar = findViewById(R.id.toolbar_charity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(AppConstant.HOME+AppConstant.GREATER_THAN+AppConstant.CHARITY);
        toolbar.setNavigationIcon(R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        autoCompleteTextView = findViewById(R.id.charity_auto_txt_view);

        charityImageView = findViewById(R.id.charity_image);
        charityTextview = findViewById(R.id.charity_txt);
        Resources res = getResources();
        charityDescription = res.getStringArray(R.array.charity_description);
        charityName = res.getStringArray(R.array.charity);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, charityName);
        ArrayAdapter spinnerAdapter = new ArrayAdapter(this, R.layout.spinner_country_list, charityName);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_country_list);
        //Setting the ArrayAdapter data on the Spinner
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                charityImageView.setImageResource(charityImage[position]);
                charityTextview.setText(charityDescription[position]);
                AppCommon.displayToast(charityName[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
