package com.android.konnek2.base.db;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.android.konnek2.R;
import com.android.konnek2.base.fragment.AppKonnek2ContactFragment;
import com.android.konnek2.base.fragment.AppKonnek2NonContactFragment;
import com.android.konnek2.base.fragment.AppKonnek2OnlineContactFragment;
import com.android.konnek2.utils.AppConstant;

/**
 * Created by Lenovo on 10-01-2018.
 */

public class AppContactsSwipeViewAdapter extends FragmentStatePagerAdapter {

    int mNumOfContactTabs;
    CharSequence contactsTitles[] = {AppConstant.TAB_CONTACTS_KONNEK2_USERS, AppConstant.TAB_CONTACTS_ONLINE_KONNEK2, AppConstant.TAB_CONTACTS_NON_KONNEK2};

    public AppContactsSwipeViewAdapter(FragmentManager fm, int mNumOfContactTabs) {
        super(fm);
        this.mNumOfContactTabs = mNumOfContactTabs;

    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {

            case 0:

                return new AppKonnek2ContactFragment();
            case 1:

                return new AppKonnek2OnlineContactFragment();

            case 2:
                return new AppKonnek2NonContactFragment();

            default:
                return null;

        }
    }

    @Override
    public int getCount() {

        return mNumOfContactTabs;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position >= 1) {
            return "" + contactsTitles[position];
        } else {
            return "";
        }
    }


}
