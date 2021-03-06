package com.aikya.konnek2.ui.fragments.search;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.aikya.konnek2.ui.fragments.base.BaseFragment;
import com.aikya.konnek2.R;
import com.aikya.konnek2.ui.adapters.search.SearchViewPagerAdapter;
import com.aikya.konnek2.ui.fragments.chats.DialogsListFragment;
import com.aikya.konnek2.utils.KeyboardUtils;

import java.lang.reflect.Field;

import butterknife.Bind;

public class SearchFragment extends BaseFragment implements SearchView.OnQueryTextListener, MenuItemCompat.OnActionExpandListener {

    @Bind(R.id.search_viewpager)
    ViewPager searchViewPager;

   /* @Bind(R.id.search_radiogroup)
    RadioGroup searchRadioGroup;*/

    private SearchViewPagerAdapter searchViewPagerAdapter;
//    private AppGlobalSearch appGlobalSearchCallback;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_search, container, false);



        activateButterKnife(view);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        initViewPagerAdapter();
//
//  initCustomListeners();

        return view;
    }

    @Override
    public void initActionBar() {
        super.initActionBar();
        actionBarBridge.setActionBarUpButtonEnabled(true);
    }

    public interface AppGlobalSearch {
        void globalSearchTrigger();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        final SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;

        if (searchMenuItem != null) {
            searchView = (SearchView) searchMenuItem.getActionView();
            MenuItemCompat.expandActionView(searchMenuItem);
            MenuItemCompat.setOnActionExpandListener(searchMenuItem, this);

            AutoCompleteTextView searchTextView = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
            try {
                searchTextView.setBackgroundColor(getResources().getColor(R.color.white));
                searchTextView.setTextColor(getResources().getColor(R.color.black));
                Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
            } catch (Exception e) {
            }

        }


        if (searchView != null) {
            searchView.setQueryHint(getString(R.string.action_bar_search_hint));
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String searchQuery) {
        KeyboardUtils.hideKeyboard(baseActivity);
        search(searchQuery);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchQuery) {
        if (searchQuery.length() >= 3) {
            search(searchQuery);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KeyboardUtils.hideKeyboard(getActivity());
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        cancelSearch();
        baseActivity.getSupportFragmentManager().popBackStack();
//        appGlobalSearchCallback.globalSearchTrigger();
        return true;
    }

    private void initViewPagerAdapter() {
        searchViewPagerAdapter = new SearchViewPagerAdapter(getChildFragmentManager());
        searchViewPager.setAdapter(searchViewPagerAdapter);
//        searchViewPager.setOnPageChangeListener(new PageChangeListener());
//        searchRadioGroup.check(R.id.local_search_radiobutton);
    }

    /*private void initCustomListeners() {
        searchRadioGroup.setOnCheckedChangeListener(new RadioGroupListener());
    }*/

    private void launchDialogsListFragment() {
        baseActivity.setCurrentFragment(DialogsListFragment.newInstance());
    }

    private void search(String searchQuery) {
        if (searchViewPagerAdapter != null && searchViewPager != null) {

            searchViewPagerAdapter.search(searchViewPager.getCurrentItem(), searchQuery);

        }
    }

    private void cancelSearch() {
        if (searchViewPagerAdapter != null && searchViewPager != null) {
            searchViewPagerAdapter.cancelSearch(searchViewPager.getCurrentItem());
        }
    }

    /*private class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            switch (checkedId) {
//                case R.id.local_search_radiobutton:
//                    searchViewPager.setCurrentItem(SearchViewPagerAdapter.LOCAl_SEARCH);
//                    searchViewPagerAdapter.prepareSearch(SearchViewPagerAdapter.LOCAl_SEARCH);
//
//                    break;
                case R.id.global_search_radiobutton:
                    searchViewPager.setCurrentItem(SearchViewPagerAdapter.GLOBAL_SEARCH);
                    searchViewPagerAdapter.prepareSearch(SearchViewPagerAdapter.GLOBAL_SEARCH);

                    break;
            }
        }
    }*/

    private class PageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            switch (position) {
//                case SearchViewPagerAdapter.LOCAl_SEARCH:
//                    searchRadioGroup.check(R.id.local_search_radiobutton);
//
//                    break;
                case SearchViewPagerAdapter.GLOBAL_SEARCH:
//                    searchRadioGroup.check(R.id.global_search_radiobutton);

                    break;
            }
        }
    }
}