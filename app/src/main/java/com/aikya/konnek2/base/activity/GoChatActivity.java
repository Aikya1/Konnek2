package com.aikya.konnek2.base.activity;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.aikya.konnek2.R;
import com.aikya.konnek2.base.fragment.AppKonnek2GOChat_CallsFragment;
import com.aikya.konnek2.base.fragment.AppKonnek2GOChat_ChatsFragment;
import com.aikya.konnek2.base.fragment.AppKonnek2GOChat_ContactsFragment;
import com.aikya.konnek2.utils.AppConstant;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class GoChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    private ViewPager tabViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_chat);

        toolbar = findViewById(R.id.toolbar_gochat);
        tabViewPager = findViewById(R.id.viewPager_tab_gochat);
        tabLayout = findViewById(R.id.tabLayout_tab_gochat);

        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(AppConstant.HOME + AppConstant.GREATER_THAN + AppConstant.GOCHAT);
        toolbar.setNavigationIcon(R.drawable.ic_app_back);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));

        setupViewPager(tabViewPager);
        tabLayout.setupWithViewPager(tabViewPager);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new AppKonnek2GOChat_ContactsFragment(), "ONE");
        adapter.addFragment(new AppKonnek2GOChat_ChatsFragment(), "TWO");
        adapter.addFragment(new AppKonnek2GOChat_CallsFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position)
        {
            return super.getPageTitle(position);
        }
    }

    /*private void swipeViewer()
    {
        tabLayout.addTab(tabLayout.newTab().setText(AppConstant.GOCHAT_TAB_ONE));
        tabLayout.addTab(tabLayout.newTab().setText(AppConstant.GOCHAT_TAB_TWO));
        tabLayout.addTab(tabLayout.newTab().setText(AppConstant.GOCHAT_TAB_THREE));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        appGoChatSwipeViewAdapter=new AppGoChatSwipeViewAdapter(getSupportFragmentManager(),tabLayout.getTabCount());


        tabViewPager.setAdapter(appGoChatSwipeViewAdapter);
        tabLayout.setTabsFromPagerAdapter(appGoChatSwipeViewAdapter);
        tabLayout.setupWithViewPager(TabViewPager);
        tabLayout.setVisibility(View.VISIBLE);

    }
*/

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
