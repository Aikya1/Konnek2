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
    public List<QBChatDialog> qbPrivateDialogsList;

    private int[] tabIcons = {
            R.drawable.konnek2_tab_icon,
    };

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

        returnedBundle = new Bundle();
        qbRequestGetBuilder = new QBRequestGetBuilder();
        qbPrivateDialogsList = new ArrayList<>();
        TabViewPager = findViewById(R.id.viewPager_tab_contact);
        tabLayout = findViewById(R.id.tabLayout_tab_contact);
        toolbar = findViewById(R.id.toolbar_contact);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setSubtitle(AppConstant.HOME + AppConstant.GREATER_THAN + AppConstant.CONTECT);
        toolbar.setNavigationIcon(R.drawable.ic_app_back);

//      toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));

        title = " " + AppSession.getSession().getUser().getFullName();
        swipeViewer();
//        new getDialogListAsyn().execute();

    }

    public void swipeViewer() {

        try {

            tabLayout.addTab(tabLayout.newTab().setText(AppConstant.TAB_CONTACTS_KONNEK2_USERS));
            tabLayout.addTab(tabLayout.newTab().setText(AppConstant.TAB_TWO));
            tabLayout.addTab(tabLayout.newTab().setText(AppConstant.TAB_THREE));

            tabLayout.setBackgroundColor(getResources().getColor(R.color.white));
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            tabLayout.getTabAt(0).setCustomView(R.layout.contacts_custom_tab_layout);

            appContactsSwipeViewAdapter = new AppContactsSwipeViewAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

            TabViewPager.setAdapter(appContactsSwipeViewAdapter);
            tabLayout.setTabsFromPagerAdapter(appContactsSwipeViewAdapter);
            tabLayout.setupWithViewPager(TabViewPager);

//            setupTabIcons();

            tabLayout.setVisibility(View.VISIBLE);
            TabViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    /*if (tab.getPosition() == 0) {
                        tab.setIcon(R.drawable.konnek2_tab_icon);
                    }*/
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

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
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
