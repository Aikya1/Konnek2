package com.android.konnek2.base.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.android.konnek2.R;
import com.android.konnek2.base.db.AppContactsSwipeViewAdapter;
import com.android.konnek2.call.core.models.AppSession;
import com.android.konnek2.call.services.model.QMUser;
import com.android.konnek2.ui.activities.base.BaseLoggableActivity;
import com.android.konnek2.utils.AppConstant;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AppContactActivity extends BaseLoggableActivity {

    private Toolbar toolbar;
    private ViewPager TabViewPager;
    private TabLayout tabLayout;
    private AppContactsSwipeViewAdapter appContactsSwipeViewAdapter;
    public Bundle returnedBundle;
    public QBRequestGetBuilder qbRequestGetBuilder;
    private List<QBChatDialog> qbDialogsList;
    public List<QBChatDialog> qbPrivateDialogsList;
    private List<QMUser> qmUsersList;
    private List<Integer> OpponentsList;
    private QBUser qbUser;

    @Override
    protected int getContentResId() {

        return R.layout.activity_app_contact;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_contact);
        if (!isChatInitializedAndUserLoggedIn()) {
            loginChat();
        }

        initViews();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    private void initViews() {

        qbUser = AppSession.getSession().getUser();
        returnedBundle = new Bundle();
        qbRequestGetBuilder = new QBRequestGetBuilder();
        qbDialogsList = new ArrayList<>();
        qbPrivateDialogsList = new ArrayList<>();
        qmUsersList = new ArrayList<>();
        OpponentsList = new ArrayList<>();
        TabViewPager = findViewById(R.id.viewPager_tab_contact);
        tabLayout = findViewById(R.id.tabLayout_tab_contact);
        toolbar = findViewById(R.id.toolbar_contact);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(AppConstant.HOME + AppConstant.GREATER_THAN + AppConstant.CONTECT);
        toolbar.setNavigationIcon(R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        title = " " + AppSession.getSession().getUser().getFullName();
        swipeViewer();
        new getDialogListAsyn().execute();

    }

    public void swipeViewer() {

        try {

            tabLayout.addTab(tabLayout.newTab().setText(AppConstant.TAB_ONE));
            tabLayout.addTab(tabLayout.newTab().setText(AppConstant.TAB_TWO));
            tabLayout.addTab(tabLayout.newTab().setText(AppConstant.TAB_THREE));

            tabLayout.setBackgroundColor(getResources().getColor(R.color.white));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
            appContactsSwipeViewAdapter = new AppContactsSwipeViewAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

            TabViewPager.setAdapter(appContactsSwipeViewAdapter);
            tabLayout.setTabsFromPagerAdapter(appContactsSwipeViewAdapter);
            tabLayout.setupWithViewPager(TabViewPager);
            tabLayout.setVisibility(View.VISIBLE);
            TabViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    TabViewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });


        } catch (Exception e) {
            e.getMessage();
        }
    }


    private class getDialogListAsyn extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Object doInBackground(Object... arg0) {
            try {

                getDialogs(qbRequestGetBuilder, returnedBundle);
            } catch (QBResponseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            privateDialogUsersList();


        }
    }

    public List<QBChatDialog> getDialogs(QBRequestGetBuilder qbRequestGetBuilder, Bundle returnedBundle) throws QBResponseException {
        qbDialogsList = QBRestChatService.getChatDialogs(null, qbRequestGetBuilder).perform();
        return qbDialogsList;
    }

    public void privateDialogUsersList() {

        for (int i = 0; i < qbDialogsList.size(); i++) {

            if (qbDialogsList.get(i).getType().equals(QBDialogType.PRIVATE)) {

                qbPrivateDialogsList.add(qbDialogsList.get(i));
            }
        }
        EventBus.getDefault().post(new MessageEvent(qbPrivateDialogsList));
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
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
