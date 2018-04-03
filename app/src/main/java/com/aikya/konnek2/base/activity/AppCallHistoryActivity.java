package com.aikya.konnek2.base.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.aikya.konnek2.App;
import com.aikya.konnek2.base.db.AppCallHistoryAdapter;
import com.aikya.konnek2.base.db.AppCallLogModel;
import com.aikya.konnek2.utils.AppPreference;
import com.aikya.konnek2.R;
import com.aikya.konnek2.utils.AppConstant;

import com.quickblox.users.model.QBUser;
import java.util.ArrayList;
import java.util.Collections;

public class AppCallHistoryActivity extends AppCompatActivity {


    //jghjhrfrhjf vggegeefeefe
    private AppCallHistoryAdapter appCallHistoryAdapter;
    private ListView callListivew;
    private ArrayList<AppCallLogModel> appCallLogModels;
    private QBUser currentUser;
    private String currentUserName;
    Toolbar toolbar;
    private String random = "sadasdasdas";

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_call_history);
        toolbar = findViewById(R.id.toolbar_hisory);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(AppConstant.HOME + AppConstant.GREATER_THAN +AppConstant.HISTORY);
        toolbar.setNavigationIcon(R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        initViews();
    }


    public void initViews() {

        callListivew = findViewById(R.id.call_logList);
        currentUserName = AppPreference.getUserName();
        appCallLogModels = new ArrayList<AppCallLogModel>();
        if (currentUserName != null && !currentUserName .isEmpty()) {
            appCallLogModels = App.appcallLogTableDAO.getCallHistory(currentUserName );
        }
        Collections.reverse(appCallLogModels);
        appCallHistoryAdapter = new AppCallHistoryAdapter(AppCallHistoryActivity.this, appCallLogModels);
        if (appCallHistoryAdapter.getCount() > 0) {
            callListivew.setAdapter(appCallHistoryAdapter);

        }

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
